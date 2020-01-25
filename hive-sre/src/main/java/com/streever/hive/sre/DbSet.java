package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.DbSetConfig;
import com.streever.hive.config.Metastore;
import com.streever.hive.config.QueryDefinitions;

import static com.streever.hive.reporting.ReportCounter.*;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;
import org.apache.commons.dbcp2.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@JsonIgnoreProperties({"cl", "config", "queryDefinitions", "metastoreDirectDataSource", "h2DataSource",
        "outputDirectory", "dbPaths"})
public class DbSet extends SreProcessBase {

    // Set after construction.
    private DbSetConfig config = null;
    // Build after construction
    private QueryDefinitions queryDefinitions = null;
    private PoolingDataSource<PoolableConnection> metastoreDirectDataSource = null;
    private PoolingDataSource<PoolableConnection> hs2DataSource = null;

    // Set during init.
    private String outputDirectory = null;
    // Set by parent, when specified.
    private String[] dbCmdlineOverride = null;

    private List<DbPaths> dbPaths;
    private List<CommandReturnCheck> checks;

    private String dbListingQuery;
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
    public void initConfig(String filename, String outputDirectory) {
        if (filename == null || outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            File cfgFile = new File(filename);
            if (!cfgFile.exists()) {
                throw new RuntimeException("Missing configuration file: " + filename);
            }
            String yamlCfgFile = FileUtils.readFileToString(cfgFile, Charset.forName("UTF-8"));

            setConfig(mapper.readerFor(DbSetConfig.class).readValue(yamlCfgFile));

            String CONFIG_RESOURCE = "/query_definitions.yaml";
            try {
                URL configURL = this.getClass().getResource(CONFIG_RESOURCE);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Resource: " + CONFIG_RESOURCE);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setQueryDefinitions(mapper.readerFor(QueryDefinitions.class).readValue(yamlConfigDefinition));
            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " + CONFIG_RESOURCE, e);
            }

            initMetastoreDataSource();
            initHs2DataSource();

        } catch (Exception e) {
            throw new RuntimeException("Issue getting configs", e);
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
        rtn = config.getQuery(definitionName);
        return rtn;
    }

    public Connection getMetastoreConnection() throws SQLException {
        Connection conn = getMetastoreDirectDataSource().getConnection();
        return conn;
    }

    public void start() {
        // Identify which Database(s) to run process(s) against.

        String[] dbs = null;
        if (this.dbCmdlineOverride != null) {
            dbs = this.dbCmdlineOverride;
        } else {
            try (Connection conn = getMetastoreConnection()) {
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

        List<ScheduledFuture> myThreads = new ArrayList<ScheduledFuture>();

        // Create Thread Pool for work.
//        ExecutorService pool = Executors.newFixedThreadPool(getConfig().getParallelism());
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(getConfig().getParallelism());
        myThreads.add(pool.schedule(getReporter(), 1, MILLISECONDS));
//        pool.execute(getReporter());
        for (SRERunnable sre : sres) {
            sre.setStatus(WAITING);
            myThreads.add(pool.schedule(sre, 1, MILLISECONDS));
//            pool.execute(sre);
        }

        while (true) {
            boolean check = true;
            for (ScheduledFuture sf: myThreads) {
                if (!sf.isDone()) {
                    check = false;
                    break;
                }
            }
            if (check)
                break;
        }
        pool.shutdown();
//        try {
//            pool.awaitTermination(1, HOURS);
//            pool.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public DataSource getMetastoreDirectDataSource() {
        if (metastoreDirectDataSource == null) {
            initMetastoreDataSource();
        }
        return metastoreDirectDataSource;
    }

    public DataSource getHs2DataSource() {
        if (hs2DataSource == null) {
            initHs2DataSource();
        }
        return hs2DataSource;
    }

    public DbSetConfig getConfig() {
        return config;
    }

    protected void setConfig(DbSetConfig config) {
        this.config = config;
    }

    public void initMetastoreDataSource() {
        // Metastore Direct
        if (getConfig().getMetastoreDirect() == null) {
            throw new RuntimeException("Missing configuration to connect to Metastore RDBMS");
        }
        Metastore msdb = getConfig().getMetastoreDirect();
        ConnectionFactory msconnectionFactory =
                new DriverManagerConnectionFactory(msdb.getUri(), msdb.getConnectionProperties());

        PoolableConnectionFactory mspoolableConnectionFactory =
                new PoolableConnectionFactory(msconnectionFactory, null);

        ObjectPool<PoolableConnection> msconnectionPool =
                new GenericObjectPool<>(mspoolableConnectionFactory);

        mspoolableConnectionFactory.setPool(msconnectionPool);

        this.metastoreDirectDataSource =
                new PoolingDataSource<>(msconnectionPool);

    }

    public void initHs2DataSource() {
        // this is optional.
        if (getConfig().getHs2() != null) {
            Metastore hs2db = getConfig().getHs2();
            ConnectionFactory hs2connectionFactory =
                    new DriverManagerConnectionFactory(hs2db.getUri(), hs2db.getConnectionProperties());

            PoolableConnectionFactory hs2poolableConnectionFactory =
                    new PoolableConnectionFactory(hs2connectionFactory, null);

            ObjectPool<PoolableConnection> hs2connectionPool =
                    new GenericObjectPool<>(hs2poolableConnectionFactory);

            hs2poolableConnectionFactory.setPool(hs2connectionPool);

            this.hs2DataSource =
                    new PoolingDataSource<>(hs2connectionPool);
        }
    }

}
