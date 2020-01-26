package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.hive.reporting.Reporter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@JsonIgnoreProperties({"config", "reporter", "threadPool", "processThreads", "connectionPools"})
public class SreProcesses {
    private SreProcessesConfig config;
    private Reporter reporter = new Reporter();
    private ScheduledExecutorService threadPool;
    private List<ScheduledFuture> processThreads;
    private ConnectionPools connectionPools;

    private List<SreProcessBase> processes = new ArrayList<SreProcessBase>();
    private int parallelism = 3;

    public SreProcessesConfig getConfig() {
        return config;
    }

    public void setConfig(SreProcessesConfig config) {
        this.config = config;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public ScheduledExecutorService getThreadPool() {
        if (threadPool == null) {
            threadPool = Executors.newScheduledThreadPool(getConfig().getParallelism());
        }
        return threadPool;
    }

    public List<ScheduledFuture> getProcessThreads() {
        if (processThreads == null) {
            processThreads = new ArrayList<ScheduledFuture>();
        }
        return processThreads;
    }

    public void setProcessThreads(List<ScheduledFuture> processThreads) {
        this.processThreads = processThreads;
    }

    public ConnectionPools getConnectionPools() {
        return connectionPools;
    }

    public void setConnectionPools(ConnectionPools connectionPools) {
        this.connectionPools = connectionPools;
    }

    public List<SreProcessBase> getProcesses() {
        return processes;
    }

    public void setProcesses(List<SreProcessBase> processes) {
        this.processes = processes;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public void start() {
        while (true) {
            boolean check = true;
            for (ScheduledFuture sf: getProcessThreads()) {
                if (!sf.isDone()) {
                    check = false;
                    break;
                }
            }
            if (check)
                break;
        }
        getThreadPool().shutdown();
    }

    public void init(String config, String outputDirectory) {
        if (config == null || outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            File cfgFile = new File(config);
            if (!cfgFile.exists()) {
                throw new RuntimeException("Missing configuration file: " + config);
            }
            String yamlCfgFile = FileUtils.readFileToString(cfgFile, Charset.forName("UTF-8"));

            setConfig(mapper.readerFor(SreProcessesConfig.class).readValue(yamlCfgFile));

            this.connectionPools = new ConnectionPools(getConfig());
            this.connectionPools.init();

            getProcessThreads().add(getThreadPool().schedule(getReporter(), 1, MILLISECONDS));

            for (SreProcessBase process: getProcesses()) {
                process.init(this, "need to set output directory");
            }


        } catch (Exception e) {
            throw new RuntimeException("Issue getting configs", e);
        }
    }

}
