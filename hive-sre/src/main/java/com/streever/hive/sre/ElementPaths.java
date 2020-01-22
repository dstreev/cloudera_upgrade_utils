package com.streever.hive.sre;

import com.streever.hadoop.HadoopSession;
import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.Eval;
import com.streever.hive.ReportingStats;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ElementPaths extends SRERunnable {

    private String database;
    private Eval eval;
    private HadoopSession session;
    private ReportingStats reporting;
    private ElementCheck[] checks = null;

    public ElementPaths(String database, Eval eval) {
        this.database = database;
        checks = new ElementCheck[1];
        checks[0] = new DirectoryExists();

        this.eval = eval;
        this.session = HadoopSession.get("Element paths for: " + database);
        String[] api = {"-api"};
        try {
            this.session.start(api);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (Connection conn = eval.getMetastoreConnection()) {

            String targetQueryDef = "elementPaths";
            QueryDefinition queryDefinition = eval.getQueryDefinitions().getQueryDefinition(targetQueryDef);
            PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
            QueryDefinition queryOverride = eval.getQueryOverride(targetQueryDef);
            Properties overrides = new Properties();
            overrides.setProperty("dbs", database);
            JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, overrides);

            ResultSet epRs = preparedStatement.executeQuery();
            ResultArray rarray = new ResultArray(epRs);
            epRs.close();
            String[] columnArray = rarray.getColumn("path_check");
            // Loop through the paths
            for (int i = 0; i < columnArray.length; i++) { //String path : columnArray) {
                String path = columnArray[i];
                for (ElementCheck check: checks) {
                    String[] commandArgs = {path};
                    String rcmd = check.getFullCommand(commandArgs);
//                System.out.println("Cmd: "+ rcmd);
                    CommandReturn cr = session.processInput(rcmd);
                    if (cr.isError()) {
                        StringBuilder error = new StringBuilder();
                        error.append(rarray.getField("db_name", i)).append(" ");
                        error.append(rarray.getField("tbl_name", i)).append(": Issue:");
                        error.append(cr.getError());
                        System.out.println(error.toString());
                    }
                }
            }
            // Print Paths examined.
            //            for (String field : columnArray) {
//                System.out.println(field);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.err.println("============ DONE ============");
    }

}
