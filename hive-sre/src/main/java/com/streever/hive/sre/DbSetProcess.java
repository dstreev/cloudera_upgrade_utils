package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.streever.hive.reporting.ReportCounter.CONSTRUCTED;
import static com.streever.hive.reporting.ReportCounter.WAITING;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


@JsonIgnoreProperties({"parent", "config", "metastoreDirectDataSource", "h2DataSource",
        "outputDirectory", "dbPaths", "cliSession", "success", "error"})
public class DbSetProcess extends SreProcessBase {

    private List<DbPaths> dbPaths;
    private List<CommandReturnCheck> checks;

    private String dbListingQuery;
    private String[] listingColumns;
    private String pathsListingQuery;

    public List<CommandReturnCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<CommandReturnCheck> checks) {
        this.checks = checks;
    }

    public String getDbListingQuery() {
        return dbListingQuery;
    }

    public void setDbListingQuery(String dbListingQuery) {
        this.dbListingQuery = dbListingQuery;
    }

    public String[] getListingColumns() {
        return listingColumns;
    }

    public void setListingColumns(String[] listingColumns) {
        this.listingColumns = listingColumns;
    }

    public String getPathsListingQuery() {
        return pathsListingQuery;
    }

    public void setPathsListingQuery(String pathsListingQuery) {
        this.pathsListingQuery = pathsListingQuery;
    }

    public List<DbPaths> getDbPaths() {
        return dbPaths;
    }

    public void setDbPaths(List<DbPaths> dbPaths) {
        this.dbPaths = dbPaths;
    }

    @Override
    public void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException {
        setParent(parent);

        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        setOutputDirectory(outputDirectory);
        this.success.println(getHeader());

        String[] dbs = null;
        if (getDbsOverride() != null && getDbsOverride().length > 0) {
            dbs = getDbsOverride();
        } else {
            try (Connection conn = getParent().getConnectionPools().getMetastoreDirectConnection()) {
                String targetQueryDef = this.dbListingQuery;
                // build prepared statement for targetQueryDef
                QueryDefinition queryDefinition = getQueryDefinitions().getQueryDefinition(targetQueryDef);
                PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
                // apply any overrides from the user configuration.
                QueryDefinition queryOverride = getQueryOverride(targetQueryDef);
                JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, queryOverride);
                // Run
                ResultSet check = preparedStatement.executeQuery();
                // Convert Result to an array
                ResultArray rarray = new ResultArray(check);
                // Close ResultSet
                check.close();
                // build array of tables.
                dbs = rarray.getColumn("name");
                System.out.println("Found " + dbs.length + " databases to process.");
            } catch (SQLException e) {
                throw new RuntimeException("Issue getting 'databases' to process.", e);
            }
        }

        // Build an Element Path for each database.  This will be use to divide the work.
        List<SRERunnable> sres = new ArrayList<SRERunnable>();//[dbs.length];
        for (String database : dbs) {
            DbPaths paths = new DbPaths(database, this);
            paths.error = this.error;
            paths.success = this.success;
            if (paths.init()) {
                paths.setStatus(CONSTRUCTED);
                sres.add(paths);
            } else {
                throw new RuntimeException("Issue establishing a connection to HDFS.  " +
                        "Check credentials(kerberos), configs(/etc/hadoop/conf), " +
                        "and/or availability of the HDFS service. " +
                        "Can you run an 'hdfs' cli command successfully?");
//                return; // Go no further with processing.
            }
        }

        for (SRERunnable sre : sres) {
            sre.setStatus(WAITING);
            // Add Counter to Main Reporter
            getParent().getReporter().addCounter(getId() + ":" + getName(), sre.getCounter());
            // Add Runnable to Main ThreadPool
            getParent().getProcessThreads().add(getParent().getThreadPool().schedule(sre, 1, MILLISECONDS));
        }

    }

    @Override
    public String toString() {
        return "DbSet{" +
                "dbPaths=" + dbPaths +
                '}';
    }
}
