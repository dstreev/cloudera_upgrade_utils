package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hadoop.HadoopSession;
import com.streever.hadoop.shell.command.CommandReturn;

import static com.streever.hive.reporting.ReportCounter.*;

import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;
import org.apache.commons.lang.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbPaths extends SRERunnable {

    private DbSet parent;
    private HadoopSession cliSession;

    private List<CommandReturnCheck> checks = new ArrayList<CommandReturnCheck>();

    public DbSet getParent() {
        return parent;
    }

    public void setParent(DbSet parent) {
        this.parent = parent;
    }

    public HadoopSession getCliSession() {
        return cliSession;
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
        for (CommandReturnCheck check : dbSet.getChecks()) {
            try {
                CommandReturnCheck newCheck = (CommandReturnCheck) check.clone();
                checks.add(newCheck);
                // Connect CommandReturnCheck counter to this counter as a child.
                // TODO: Need to set Counters name from the 'check'
                getCounter().addChild(newCheck.getCounter());
                // TODO: Set success and error printstreams to output files.
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        this.cliSession = HadoopSession.get("DB Paths for: " + name);
        String[] api = {"-api"};
        try {
            this.cliSession.start(api);
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

            // TODO: Need to get 'path_check' into a parameter in config files.
            String[] columnArray = rarray.getColumn("path_check");
            String[] columns = getParent().getListingColumns();

            String[][] columnsArray = rarray.getColumns(columns);
            this.setTotalCount(rarray.getCount() * this.getCounterChildren().size());
            // Loop through the paths
            if (columnsArray[0].length > 0) {
                this.setStatus(PROCESSING);

                for (int i = 0; i < columnsArray[0].length; i++) { //String path : columnArray) {
                    String[] args = new String[columnsArray.length];
                    for (int a = 0; a < columnsArray.length; a++) {
                        args[a] = columnsArray[a][i];
                    }
//                path = columnArray[i];
                    // Match path is not null.
                    for (CommandReturnCheck lclCheck : getChecks()) {
//                        String[] commandArgs = {args};
                        try {
                            String rcmd = lclCheck.getFullCommand(args);
                            if (rcmd != null) {
//                            System.err.println("Rcmd: " + rcmd);
//                            try {
                                CommandReturn cr = getCliSession().processInput(rcmd);
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
//                            } catch (Throwable npe) {
//                                npe.printStackTrace();
//                                throw new RuntimeException(rcmd, npe);
//                            }
                            }
                        } catch (RuntimeException t) {
                            // Malformed cli request.  Input is missing an element required to complete call.
                            // Unusual, but not an expection.
                        }
                    }
                    incProcessed(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus(ERROR);
        } catch (Throwable t) {
            System.err.println("Failure in DbPaths");
            t.printStackTrace();
        }
        setStatus(COMPLETED);
    }

}
