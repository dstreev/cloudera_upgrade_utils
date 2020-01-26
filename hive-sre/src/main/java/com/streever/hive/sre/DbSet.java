package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.QueryDefinitions;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.streever.hive.reporting.ReportCounter.CONSTRUCTED;
import static com.streever.hive.reporting.ReportCounter.WAITING;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


@JsonIgnoreProperties({"parent", "config", "queryDefinitions", "metastoreDirectDataSource", "h2DataSource",
        "outputDirectory", "dbPaths"})
public class DbSet extends SreProcessBase {

    // Build after construction
    private QueryDefinitions queryDefinitions = null;
    private String queryDefinitionReference = null;

    // Set during init.
    private String outputDirectory = null;
    // Set by parent, when specified.
    private String[] dbCmdlineOverride = null;

    private List<DbPaths> dbPaths;
    private List<CommandReturnCheck> checks;

    private String dbListingQuery;
    private String pathsListingQuery;

    public String getQueryDefinitionReference() {
        return queryDefinitionReference;
    }

    public void setQueryDefinitionReference(String queryDefinitionReference) {
        this.queryDefinitionReference = queryDefinitionReference;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            try {
                URL configURL = this.getClass().getResource(this.queryDefinitionReference);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Resource: " +
                            this.queryDefinitionReference);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setQueryDefinitions(mapper.readerFor(QueryDefinitions.class).readValue(yamlConfigDefinition));
            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " +
                        this.queryDefinitionReference, e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Issue getting configs", e);
        }

    }

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

    public String getPathsListingQuery() {
        return pathsListingQuery;
    }

    public void setPathsListingQuery(String pathsListingQuery) {
        this.pathsListingQuery = pathsListingQuery;
    }

    public String[] getDbCmdlineOverride() {
        return dbCmdlineOverride;
    }

    public void setDbCmdlineOverride(String[] dbCmdlineOverride) {
        this.dbCmdlineOverride = dbCmdlineOverride;
    }

    public List<DbPaths> getDbPaths() {
        return dbPaths;
    }

    public void setDbPaths(List<DbPaths> dbPaths) {
        this.dbPaths = dbPaths;
    }

    @Override
    public void init(SreProcesses parent, String outputDirectory) {
        setParent(parent);
        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        // TODO: Setup output paths.

        String[] dbs = null;
        if (this.dbCmdlineOverride != null) {
            dbs = this.dbCmdlineOverride;
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
            } catch (SQLException e) {
                throw new RuntimeException("Issue getting 'databases' to process.", e);
            }
        }

        // Build an Element Path for each database.  This will be use to divide the work.
        List<SRERunnable> sres = new ArrayList<SRERunnable>();//[dbs.length];
        for (String database : dbs) {
            DbPaths paths = new DbPaths(database, this);
            paths.setStatus(CONSTRUCTED);
            sres.add(paths);
        }

        for (SRERunnable sre : sres) {
            sre.setStatus(WAITING);
            // Add Counter to Main Reporter
            getParent().getReporter().addCounter(getName(), sre.getCounter());
            // Add Runnable to Main ThreadPool
            getParent().getProcessThreads().add(getParent().getThreadPool().schedule(sre, 1, MILLISECONDS));
        }

    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    protected QueryDefinitions getQueryDefinitions() {
        return queryDefinitions;
    }

    protected void setQueryDefinitions(QueryDefinitions queryDefinitions) {
        this.queryDefinitions = queryDefinitions;
    }

    protected QueryDefinition getQueryOverride(String definitionName) {
        QueryDefinition rtn = null;
        rtn = getParent().getConfig().getQuery(definitionName);
        return rtn;
    }

//    public void start() {
    // Identify which Database(s) to run process(s) against.

//        String[] dbs = null;
//        if (this.dbCmdlineOverride != null) {
//            dbs = this.dbCmdlineOverride;
//        } else {
//            try (Connection conn = getParent().getMetastoreConnection()) {
//                String targetQueryDef = this.dbListingQuery;
//                // build prepared statement for targetQueryDef
//                QueryDefinition queryDefinition = getQueryDefinitions().getQueryDefinition(targetQueryDef);
//                PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
//                // apply any overrides from the user configuration.
//                QueryDefinition queryOverride = getQueryOverride(targetQueryDef);
//                JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, queryOverride);
//                // Run
//                ResultSet check = preparedStatement.executeQuery();
//                // Convert Result to an array
//                ResultArray rarray = new ResultArray(check);
//                // Close ResultSet
//                check.close();
//                // build array of tables.
//                dbs = rarray.getColumn("name");
//            } catch (SQLException e) {
//                throw new RuntimeException("Issue getting 'databases' to process.", e);
//            }
//        }
//
//        // Build an Element Path for each database.  This will be use to divide the work.
//        List<SRERunnable> sres = new ArrayList<SRERunnable>();//[dbs.length];
//        for (String database : dbs) {
//            DbPaths paths = new DbPaths(database, this);
//            paths.setStatus(CONSTRUCTED);
//            sres.add(paths);
//        }
//
//        for (SRERunnable sre : sres) {
//            sre.setStatus(WAITING);
//            // Add Counter to Main Reporter
//            getParent().getReporter().addCounter(sre.getName(), sre.getCounter());
//            // Add Runnable to Main ThreadPool
//            getParent().getProcessThreads().add(getParent().getThreadPool().schedule(sre, 1, MILLISECONDS));
//        }
//    }

    public SreProcessesConfig getConfig() {
        return getParent().getConfig();
    }

}
