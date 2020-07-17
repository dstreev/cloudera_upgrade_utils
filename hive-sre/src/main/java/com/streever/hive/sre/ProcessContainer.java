package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.Reporter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@JsonIgnoreProperties({"config", "reporter", "threadPool", "processThreads", "connectionPools", "outputDirectory"})
public class ProcessContainer {
    private SreProcessesConfig config;
    private Reporter reporter = new Reporter();
    private ScheduledExecutorService threadPool;
    private List<ScheduledFuture> processThreads;
    private ConnectionPools connectionPools;
    private String outputDirectory;
    private List<Integer> includes = new ArrayList<Integer>();

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

    public void addInclude(Integer include) {
        includes.add(include);
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

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
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
        for (SreProcessBase process: getProcesses()) {
            if (process.isActive()) {
                System.out.println(process.getId() + ":" + process.getName());
                System.out.println(process.getErrorDescription() + " -> " + process.getOutputDirectory() + System.getProperty("file.separator") +
                        process.getErrorFilename());
                System.out.println(process.getSuccessDescription() + " -> " + process.getOutputDirectory() + System.getProperty("file.separator") +
                        process.getSuccessFilename());
            }
        }
    }

    public void init(String config, String outputDirectory, String[] dbsOverride) {
        String job_run_dir = null;
        if (config == null || outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        } else {
            Date now = new Date();
            DateFormat df = new SimpleDateFormat("YY-MM-dd_HH-mm-ss");
            job_run_dir = outputDirectory + System.getProperty("file.separator") + df.format(now);
            File jobDir = new File(job_run_dir);
            if (!jobDir.exists()) {
                jobDir.mkdirs();
            }
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

            getProcessThreads().add(getThreadPool().schedule(getReporter(), 100, MILLISECONDS));

            for (SreProcessBase process: getProcesses()) {
                if (process.isActive()) {
                    process.setDbsOverride(dbsOverride);
                    process.init(this, job_run_dir);
                    if (process instanceof Runnable && process instanceof Counter) {
                        getReporter().addCounter(process.getName(), ((Counter)process).getCounter());
                        getProcessThreads().add(getThreadPool().schedule((Runnable)process, 100, MILLISECONDS));
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException("Issue getting configs", e);
        }
    }

    @Override
    public String toString() {
        return "ProcessContainer{}";
    }
}
