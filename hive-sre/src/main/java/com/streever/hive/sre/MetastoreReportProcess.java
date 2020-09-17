package com.streever.hive.sre;

import com.streever.hive.config.Metastore;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;
import com.streever.hive.reporting.ReportingConf;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.streever.hive.reporting.ReportCounter.*;

public class MetastoreReportProcess extends MetastoreProcess {

    private List<MetastoreQuery> metastoreQueryDefinitions = new ArrayList<MetastoreQuery>();
    private ScriptEngine scriptEngine = null;

    @Override
    public void run() {
        setStatus(PROCESSING);

        ScriptEngineManager sem = new ScriptEngineManager();
        scriptEngine = sem.getEngineByName("nashorn");

        success.println(ReportingConf.substituteVariables(getTitle()));

        if (getNote() != null)
            success.println(getNote());

        if (getHeader() != null)
            success.println(getHeader());

        this.setTotalCount(getMetastoreQueryDefinitions().size());
        for (MetastoreQuery metastoreQueryDefinition: getMetastoreQueryDefinitions()) {
            String[][] metastoreRecords = null;
            try (Connection conn = getParent().getConnectionPools().getMetastoreDirectConnection()) {
                String targetQueryDef = metastoreQueryDefinition.getQuery();
                // build prepared statement for targetQueryDef
                QueryDefinition queryDefinition = getQueryDefinitions().getQueryDefinition(targetQueryDef);
                PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
                // apply any overrides from the user configuration.
                QueryDefinition queryOverride = getQueryOverride(targetQueryDef);
                JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, queryOverride);
                // Run
                ResultSet rCheck = preparedStatement.executeQuery();
                // Convert Result to an array
                ResultArray rarray = new ResultArray(rCheck);
                // Close ResultSet
                rCheck.close();
                // build array of columns
                metastoreRecords = rarray.getColumns(metastoreQueryDefinition.getListingColumns());

                if (metastoreRecords[0] != null && metastoreRecords[0].length > 0) {
                    if (metastoreQueryDefinition.getResultMessageHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageHeader());
                    }
                    if (metastoreQueryDefinition.getResultMessageDetailHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageDetailHeader());
                    }
                    setTotalCount(metastoreRecords[0].length);
                    for (int i = 0; i < metastoreRecords[0].length; i++) {
                    incSuccess(1);
                        String[] record = new String[metastoreQueryDefinition.getListingColumns().length];
                        for (int j = 0; j < metastoreQueryDefinition.getListingColumns().length; j++) {
                            record[j] = metastoreRecords[j][i];
//                        serdeRecords[0][i], serdeRecords[1][i], serdeRecords[2][i]
                        }

                        // Use the Check OR the Result Message Template
                        if (metastoreQueryDefinition.getCheck() != null && metastoreQueryDefinition.getCheck().getTest() != null) {
                            // Params
                            List combined = new LinkedList(Arrays.asList(record));
                            // Configured Params
                            if (metastoreQueryDefinition.getCheck().getParams() != null)
                                combined.addAll(Arrays.asList(metastoreQueryDefinition.getCheck().getParams()));
                            try {
                                String testStr = String.format(metastoreQueryDefinition.getCheck().getTest(), combined.toArray());
                                Boolean checkTest = null;
                                checkTest = (Boolean) scriptEngine.eval(testStr);
                                if (checkTest) {
                                    if (metastoreQueryDefinition.getCheck().getPass() != null) {
                                        String passStr = String.format(metastoreQueryDefinition.getCheck().getPass(), combined.toArray());
                                        String passResult = (String) scriptEngine.eval(passStr);
                                        success.println(passResult);
//                                        sb.append(passResult).append("\n");
                                    }

                                } else {
                                    if (metastoreQueryDefinition.getCheck().getFail() != null) {
                                        String failStr = String.format(metastoreQueryDefinition.getCheck().getFail(), combined.toArray());
                                        String failResult = (String) scriptEngine.eval(failStr);
                                        success.println(failResult);
//                                        sb.append(failResult).append("\n");
                                    }
                                }
                            } catch (ScriptException e) {
                                e.printStackTrace();
                                System.err.println("Issue with script eval: " + this.getDisplayName());
                            } catch (MissingFormatArgumentException mfa) {
                                mfa.printStackTrace();
                                System.err.println("Bad Argument Match up for PATH check rule: " + this.getDisplayName());
                            }
                        } else {
                            if (metastoreQueryDefinition.getResultMessageDetailTemplate() != null) {
                                String message = String.format(metastoreQueryDefinition.getResultMessageDetailTemplate(), record);
                                success.println(message);
                            }
                        }
                    }
                } else {
                    if (metastoreQueryDefinition.getResultMessageHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageHeader());
                    }
                    success.println("\n> **Results empty**\n");
                }
                incSuccess(1);
            } catch (SQLException e) {
                incError(1);
                error.println(metastoreQueryDefinition.getQuery());
                error.println("> Processing Issue: " + e.getMessage());
//                setStatus(ERROR);
//                throw new RuntimeException("Issue getting 'databases' to process.", e);
            } catch (RuntimeException rte) {
                incError(1);
                error.println(metastoreQueryDefinition.getQuery());
                error.println("> Processing Issue: " + rte.getMessage());
            }
            incProcessed(1);
        }
        setStatus(COMPLETED);
    }


    public List<MetastoreQuery> getMetastoreQueryDefinitions() {
        return metastoreQueryDefinitions;
    }

    public void setMetastoreQueryDefinitions(List<MetastoreQuery> metastoreQueryDefinitions) {
        this.metastoreQueryDefinitions = metastoreQueryDefinitions;
    }

    @Override
    public String toString() {
        return "MetastoreReportProcess{}";
    }
}
