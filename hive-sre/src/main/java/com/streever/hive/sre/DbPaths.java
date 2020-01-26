package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hadoop.HadoopSession;
import com.streever.hadoop.shell.command.CommandReturn;
import static com.streever.hive.reporting.ReportCounter.*;

import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbPaths extends SRERunnable  {

    private DbSet parent;
    private HadoopSession session;

    private List<CommandReturnCheck> checks = new ArrayList<CommandReturnCheck>();

    public DbSet getParent() {
        return parent;
    }

    public void setParent(DbSet parent) {
        this.parent = parent;
    }

    public List<CommandReturnCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<CommandReturnCheck> checks) {
        this.checks = checks;
    }

    public DbPaths(String name, DbSet dbSet) {
        setName(name);
        setParent(dbSet);
        for (CommandReturnCheck check: dbSet.getChecks()) {
            try {
                CommandReturnCheck newCheck = (CommandReturnCheck)check.clone();
                checks.add(newCheck);
                // Connect CommandReturnCheck counter to this counter as a child.
                // TODO: Need to set Counters name from the 'check'
                getCounter().addChild(newCheck.getCounter());
                // TODO: Set success and error printstreams to output files.
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        this.session = HadoopSession.get("DB Paths for: " + name);
        String[] api = {"-api"};
        try {
            this.session.start(api);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.setStatus(STARTED);
        try (Connection conn = getParent().getParent().getConnectionPools().
                getMetastoreDirectConnection()) {

            QueryDefinition queryDefinition = getParent().getQueryDefinitions().
                    getQueryDefinition(getParent().getPathsListingQuery());
            PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);

            Properties overrides = new Properties();
            overrides.setProperty("dbs", getName());
            JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, overrides);

            ResultSet epRs = preparedStatement.executeQuery();
            ResultArray rarray = new ResultArray(epRs);
            epRs.close();

            // Need to get 'path_check' into a parameter in config files.
            String[] columnArray = rarray.getColumn("path_check");
            this.setTotalCount(rarray.getCount() * this.getCounterChildren().size());
            this.setStatus(PROCESSING);
            // Loop through the paths
            for (int i = 0; i < columnArray.length; i++) { //String path : columnArray) {
                String path = columnArray[i];
                for (CommandReturnCheck lclCheck : getChecks()) {
                    String[] commandArgs = {path};
                    String rcmd = lclCheck.getFullCommand(commandArgs);
                    CommandReturn cr = session.processInput(rcmd);
                    lclCheck.incProcessed(1);
                    if (!cr.isError()) {
                        lclCheck.onSuccess(cr);
                        lclCheck.incSuccess(1);
                        this.incSuccess(1);
                    } else {
                        lclCheck.onError(cr);
                        lclCheck.incError(1);
                        this.incError(1);
                    }
                }
                incProcessed(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus(ERROR);
        }
        setStatus(COMPLETED);
    }

}
