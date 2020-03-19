package com.streever.hive.perf;

import com.streever.hive.SreSubApp;
import org.apache.commons.cli.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class JDBCPerfTest implements SreSubApp {

    public static final Integer STATUS_INTERVAL = 1000;
    public static final Integer REFRESH_INTERVAL = 1000;
    public static final Integer DISPLAY_REFRESH = 3500;

    private JDBCRecordIterator jri = new JDBCRecordIterator();
    private CollectStatistics stats = null;


    private ScheduledExecutorService threadPool;
    private List<ScheduledFuture> processThreads;

    public ScheduledExecutorService getThreadPool() {
        if (threadPool == null) {
            threadPool = Executors.newScheduledThreadPool(2);
        }
        return threadPool;
    }

    public List<ScheduledFuture> getProcessThreads() {
        if (processThreads == null) {
            processThreads = new ArrayList<ScheduledFuture>();
        }
        return processThreads;
    }

    public JDBCRecordIterator getJri() {
        return jri;
    }

    public CollectStatistics getStats() {
        return stats;
    }

    public Options getOptions() {
        Options options = new Options();

        Option url = new Option("u", "url", true,
                "Database URL");
        url.setRequired(true);
        options.addOption(url);

        Option username = new Option("n", "username", true, "Username");
        username.setRequired(false);
        options.addOption(username);

        Option password = new Option("p", "password", true, "Password");
        password.setRequired(false);
        options.addOption(password);

        Option query = new Option("e", "query", true, "Query");
        query.setRequired(true);
        options.addOption(query);

        Option lite = new Option("l", "lite", false, "Don't open record.  Reduce client overhead (loose some stats)");
        lite.setRequired(false);
        options.addOption(lite);

        Option batchsize = new Option("b", "batch-size", true, "Client Batch Fetch Size");
        batchsize.setArgs(1);
        batchsize.setRequired(false);
        options.addOption(batchsize);

        Option delayWarning = new Option("d", "delay-warning", true,
                "Warn when delay while iterating over records is greater than this.");
        delayWarning.setArgs(1);
        delayWarning.setRequired(false);
        options.addOption(delayWarning);

        // TBD WIP
//        Option execFile = Option.builder("f").required(false)
//                .argName("exec file").desc("Execute File")
//                .longOpt("exec")
//                .hasArg(true).numberOfArgs(1)
//                .build();
//        options.addOption(execFile);
//
//        Option batchSize = Option.builder("b").required(false)
//                .argName("batch").desc("Batch Size")
//                .longOpt("batch")
//                .hasArg(true).numberOfArgs(1)
//                .build();
//        options.addOption(batchSize);

        return options;
    }


    private void setOptions(Options options, String[] args) {

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(this.getClass().getName(), options);
            System.exit(-1);
        }

        if (cmd.hasOption("u")) {
            getJri().setJdbcUrl(cmd.getOptionValue("u"));
        }

        if (cmd.hasOption("n")) {
            getJri().setUsername(cmd.getOptionValue("n"));
        } else {
            getJri().setUsername(System.getProperty("user.name"));
        }

        if (cmd.hasOption("p")) {
            getJri().setPassword(cmd.getOptionValue("p"));
        }

        if (cmd.hasOption("e")) {
            getJri().setQuery(cmd.getOptionValue("e"));
        }

        if (cmd.hasOption("l")) {
            getJri().setLite(Boolean.TRUE);
        }

        if (cmd.hasOption("b")) {
            String value = cmd.getOptionValue("b");
            getJri().setBatchSize(Integer.valueOf(value));
        }

        if (cmd.hasOption("d")) {
            String value = cmd.getOptionValue("d");
            getJri().setDelayWarning(Integer.valueOf(value));
        }

    }

    public void init(String[] args) {
        Options options = getOptions();
        setOptions(options, args);
        stats = new CollectStatistics(getJri());
    }

    public void start() {
        getProcessThreads().add(getThreadPool().schedule(this.getJri(), 1, MILLISECONDS));
        getStats().start();

        while (true) {
            boolean check = true;
            for (ScheduledFuture sf : getProcessThreads()) {
                if (!sf.isDone()) {
                    check = false;
                    break;
                }
            }
            if (check)
                break;
            try {
                Thread.sleep(DISPLAY_REFRESH);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getStats().printStatus();
        }
        getThreadPool().shutdown();
        getStats().interrupt();

    }

    public static void main(String[] args) {
        JDBCPerfTest test = new JDBCPerfTest();
        test.init(args);
        test.start();
    }

}