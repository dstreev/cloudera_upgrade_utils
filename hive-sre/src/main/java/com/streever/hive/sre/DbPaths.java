package com.streever.hive.sre;

import com.streever.hadoop.HadoopSession;
import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.config.HiveStrictManagedMigrationElements;
import com.streever.hive.config.HiveStrictManagedMigrationIncludeListConfig;
import com.streever.hive.reporting.CounterGroup;
import com.streever.hive.reporting.TaskState;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DbPaths extends SRERunnable {
    private static Logger LOG = LogManager.getLogger(DbPaths.class);

    private DbSetProcess parent;
    private CounterGroup counterGroup;

    private List<CommandReturnCheck> commandChecks = new ArrayList<CommandReturnCheck>();
    private CheckCalculation calculationCheck = null;

    public DbSetProcess getParent() {
        return parent;
    }

    public void setParent(DbSetProcess parent) {
        this.parent = parent;
    }

    public void setCounterGroup(CounterGroup counterGroup) {
        this.counterGroup = counterGroup;
    }

    public List<CommandReturnCheck> getCommandChecks() {
        return commandChecks;
    }

    public void setCommandChecks(List<CommandReturnCheck> commandChecks) {
        this.commandChecks = commandChecks;
    }

    public CheckCalculation getCalculationCheck() {
        return calculationCheck;
    }

    public void setCalculationCheck(CheckCalculation calculationCheck) {
        this.calculationCheck = calculationCheck;
    }

    public DbPaths(String name, DbSetProcess dbSet) {
        setDisplayName(name);
        setParent(dbSet);
    }

    @Override
    public Boolean init() {
        Boolean rtn = Boolean.FALSE;
        return Boolean.TRUE;
    }


    protected void doIt() {

        QueryDefinition queryDefinition = null;
        HadoopSession cli = null;

        try (Connection conn = getParent().getParent().getConnectionPools().
                getMetastoreDirectConnection()) {

            queryDefinition = getParent().getQueryDefinitions().
                    getQueryDefinition(getParent().getPathsListingQuery());
            PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);

            Properties overrides = new Properties();
            overrides.setProperty("dbs", getDisplayName());
            JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, overrides);

            ResultSet epRs = preparedStatement.executeQuery();
            ResultArray rarray = new ResultArray(epRs);
            epRs.close();

            String[] columns = getParent().getListingColumns();

            String[][] columnsArray = rarray.getColumns(columns);

            cli = getParent().getParent().getCliPool().borrow();

            Integer[] hsmmElementLoc = null;
            HiveStrictManagedMigrationElements hsmmElements = getParent().getHsmmElements();
            // If we found an hsmmelement attribute, populate the location parts
            // so we can add the reference for the hsmm processing config.
            if (hsmmElements != null) {
                hsmmElementLoc = new Integer[2];
                // Align the locations in the array with the names
                for (int i = 0;i < columns.length;i++) {
                    if (columns[i].equals(hsmmElements.getDatabaseField())) {
                        hsmmElementLoc[0] = i;
                    }
                    if (columns[i].equals(hsmmElements.getTableField())) {
                        hsmmElementLoc[1] = i;
                    }
                }
                // If we didn't find both, then set to null.
                if (hsmmElementLoc[0] == null || hsmmElementLoc[1] == null) {
                    // TODO: Need to throw config exception in this condition.
                    hsmmElementLoc = null;
                }
            }
            // Loop through the paths
            if (columnsArray[0] != null && columnsArray[0].length > 0) {
                for (int i = 0; i < columnsArray[0].length; i++) { //String path : columnArray) {
                    String[] args = new String[columnsArray.length];
                    for (int a = 0; a < columnsArray.length; a++) {
                        if (columnsArray[a][i] != null)
                            args[a] = columnsArray[a][i];
                        else
                            args[a] = " "; // Prevent null in array.  Messes up String.format when array has nulls.
                    }
                    if (hsmmElementLoc != null) {
                        // When defined, add elements to hsmm.
                        HiveStrictManagedMigrationIncludeListConfig hsmmwcfg =
                                HiveStrictManagedMigrationIncludeListConfig.getInstance();
                        hsmmwcfg.addTable(args[hsmmElementLoc[0]], args[hsmmElementLoc[1]]);
                    }
                    if (getCommandChecks() != null) {
                        for (CommandReturnCheck lclCheck : getCommandChecks()) {
                            try {
                                LOG.info(getParent().getDisplayName() + ":" + lclCheck.getDisplayName() + " " + Arrays.toString(args));
                                String rcmd = lclCheck.getFullCommand(args);
                                if (rcmd != null) {
                                    CommandReturn cr = cli.processInput(rcmd);
//                                    lclCheck.getCounter().incCount(TaskState.PROCESSED, 1);
                                    if (!cr.isError() || (lclCheck.getInvertCheck() && cr.isError())) {
                                        lclCheck.onSuccess(cr,args);
                                        lclCheck.getCounter().incCount(TaskState.SUCCESS, 1);
                                    } else {
                                        lclCheck.onError(cr,args);
                                        lclCheck.getCounter().incCount(TaskState.ERROR, 1);
                                    }
                                }
                            } catch (RuntimeException t) {
                                // Malformed cli request.  Input is missing an element required to complete call.
                                // Unusual, but not an expection.
                            }
                        }
                    }

//                    if (calculationCheck != null) {
////                        for (int j = 0; j < metastoreQueryDefinition.getListingColumns().length; j++) {
////                            record[j] = metastoreRecords[j][i];
////                        }
//
//                        List combined = new LinkedList(Arrays.asList(args));
//
//                        // Configured Params
////                        if (metastoreQueryDefinition.getCheck().getParams() != null)
////                            combined.addAll(Arrays.asList(metastoreQueryDefinition.getCheck().getParams()));
//                        try {
//                            String testStr = String.format(calculationCheck.getTest(), combined.toArray());
//                            Boolean checkTest = null;
//                            checkTest = (Boolean) scriptEngine.eval(testStr);
//                            if (checkTest) {
//                                if (calculationCheck.getPass() != null) {
//                                    String passStr = String.format(calculationCheck.getPass(), combined.toArray());
//                                    String passResult = (String) scriptEngine.eval(passStr);
//                                    success.println(passResult);
//                                }
//
//                            } else {
//                                if (calculationCheck.getFail() != null) {
//                                    String failStr = String.format(calculationCheck.getFail(), combined.toArray());
//                                    String failResult = (String) scriptEngine.eval(failStr);
//                                    success.println(failResult);
//                                }
//                            }
//                            incSuccess(1);
//
//                            incProcessed(1);
//                        } catch (ScriptException e) {
//                            e.printStackTrace(error);
//                            System.err.println("Issue with script eval: " + this.getDisplayName());
//                        } catch (MissingFormatArgumentException mfa) {
//                            mfa.printStackTrace(error);
//                            System.err.println("Bad Argument Match up for PATH check rule: " + this.getDisplayName());
//                        }
//                    }
                }
            }
        } catch (SQLException e) {
            if (getCommandChecks().size() > 0) {
                getCommandChecks().get(0).errorStream.println((queryDefinition != null) ? queryDefinition.getStatement() : "Unknown");
                getCommandChecks().get(0).errorStream.println("Failure in DbPaths" + e.getMessage());
            } else {
                error.println((queryDefinition != null) ? queryDefinition.getStatement() : "Unknown");
                error.println("Failure in DbPaths" + e.getMessage());
            }
            e.printStackTrace(error);
        } catch (Throwable t) {
            System.err.println("Failure in DbPaths");
            t.printStackTrace(error);
        } finally {
            getParent().getParent().getCliPool().returnSession(cli);
            // When completed, increment the processed value.
            counterGroup.addAndGetTaskState(TaskState.PROCESSED, 1);
        }
    }

    @Override
    public String call() throws Exception {
        doIt();
        return "done";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbPaths dbPaths = (DbPaths) o;

        if (parent != null ? !parent.equals(dbPaths.parent) : dbPaths.parent != null) return false;
        if (counterGroup != null ? !counterGroup.equals(dbPaths.counterGroup) : dbPaths.counterGroup != null)
            return false;
        if (commandChecks != null ? !commandChecks.equals(dbPaths.commandChecks) : dbPaths.commandChecks != null)
            return false;
        return calculationCheck != null ? calculationCheck.equals(dbPaths.calculationCheck) : dbPaths.calculationCheck == null;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (counterGroup != null ? counterGroup.hashCode() : 0);
        result = 31 * result + (commandChecks != null ? commandChecks.hashCode() : 0);
        result = 31 * result + (calculationCheck != null ? calculationCheck.hashCode() : 0);
        return result;
    }
}
