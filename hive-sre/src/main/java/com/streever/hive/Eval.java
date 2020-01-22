package com.streever.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.*;
import com.streever.hive.sre.ElementPaths;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;
import org.apache.commons.cli.*;
import org.apache.commons.dbcp2.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 */
public class Eval {
    private ClassLoader cl = getClass().getClassLoader();
    private HiveSRE config = null;
    private QueryDefinitions queryDefinitions = null;
    private PoolingDataSource<PoolableConnection> metastoreDirectDataSource = null;
    private PoolingDataSource<PoolableConnection> hs2DataSource = null;

    public void setConfig(HiveSRE config) {
        this.config = config;
    }

    public QueryDefinitions getQueryDefinitions() {
        return queryDefinitions;
    }

    public QueryDefinition getQueryOverride(String definitionName) {
        QueryDefinition rtn = null;
        rtn = config.getQuery(definitionName);
        return rtn;
    }

    public void setQueryDefinitions(QueryDefinitions queryDefinitions) {
        this.queryDefinitions = queryDefinitions;
    }

    public static void main(String[] args) throws Exception {
        Eval eval = new Eval();
        eval.init(args);

//        HadoopSession hs = HadoopSession.get("eval");
//        hs.start(new String[] {"-api"});

        System.out.println("Hello World!");
    }

    protected void init(String[] args) {
        Options options = getOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Eval", options);
            System.exit(-1);
        }

        getConfigs(cmd.getOptionValue("cfg"));

        constructEnvironment();
    }

    static void printMatrix(String[][] grid) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++)
                System.out.print(grid[r][c] + " ");
            System.out.println();
        }
    }

    public Connection getMetastoreConnection() throws SQLException {
        Connection conn = metastoreDirectDataSource.getConnection();
        return conn;
    }

    private void constructEnvironment() {

        // Build Metastore DB Pool
        initMetastoreDataSource();
        initHs2DataSource();
        String[] dbs = null;
//        Connection conn = null;
        try (Connection conn = getMetastoreConnection()) {
            String targetQueryDef = "db_tbl_count";
            QueryDefinition queryDefinition = getQueryDefinitions().getQueryDefinition(targetQueryDef);
            PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
            QueryDefinition queryOverride = getQueryOverride(targetQueryDef);
//            Properties overrides = new Properties();
//            overrides.setProperty("dbs", database);
            JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, queryOverride);

            ResultSet check = preparedStatement.executeQuery();
            ResultArray rarray = new ResultArray(check);
            dbs = rarray.getColumn("name");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        List<ElementPaths> mds = new ArrayList<ElementPaths>();//[dbs.length];
        for (String database : dbs) {
            ElementPaths md = new ElementPaths(database, this);
            mds.add(md);
        }

        // Create Thread Pool for work.
        ExecutorService pool = Executors.newFixedThreadPool(getConfig().getParallelism());
        for (ElementPaths md : mds) {
            pool.execute(md);
        }

        pool.shutdown();

//        System.out.println("Line 1 (first variant)");
        String resetToPreviousLine = "\33[1A\33[2K";
        String clearConsole = "\33[H\33[2J";
        System.out.print(clearConsole);


//        String[] dbs = {"priv_dstreev", "airline_perf"};
//        MissingDirectories md = new MissingDirectories("priv_dstreev", this);
//        md.run();
    }

    protected void getConfigs(String configFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Date start = new Date();

        try {
            File cfgFile = new File(configFile);
            String yamlCfgFile = FileUtils.readFileToString(cfgFile, Charset.forName("UTF-8"));

            this.config = mapper.readerFor(HiveSRE.class).readValue(yamlCfgFile);

            File queryFile = new File(cl.getResource("eval_support.yaml").getFile());
            String yamlQueryFile = FileUtils.readFileToString(queryFile, Charset.forName("UTF-8"));

            this.queryDefinitions = mapper.readerFor(QueryDefinitions.class).readValue(yamlQueryFile);

            System.out.println("Test");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Date end = new Date();
            long diff = end.getTime() - start.getTime();

            System.out.println("Time: " + diff);

        }

    }

    private Options getOptions() {
        // create Options object
        Options options = new Options();

        Option cfgOption = new Option("cfg", "config", true, "Configuration File");
        cfgOption.setRequired(true);
//        Option cfgOption = Option.builder("cfg").required(true)
//                .argName("config").desc("Configuration")
//                .longOpt("config")
//                .hasArg(true).numberOfArgs(1)
//                .build();
        options.addOption(cfgOption);

        return options;

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

    public HiveSRE getConfig() {
        return config;
    }

    public void initMetastoreDataSource() {
        // Metastore Direct
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
