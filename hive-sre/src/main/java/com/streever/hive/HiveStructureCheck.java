package com.streever.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.sre.ProcessContainer;
import com.streever.hive.sre.SreProcessBase;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class HiveStructureCheck implements SreSubApp {

    private ProcessContainer processContainer;
    private String[] dbsOverride;

    public String[] getDbsOverride() {
        return dbsOverride;
    }

    public void setDbsOverride(String[] dbsOverride) {
        this.dbsOverride = dbsOverride;
    }

    public ProcessContainer getProcessContainer() {
        return processContainer;
    }

    public void setProcessContainer(ProcessContainer processContainer) {
        this.processContainer = processContainer;
    }

    public static void main(String[] args) {
        HiveStructureCheck sre = new HiveStructureCheck();
        try {
            sre.init(args);
            sre.start();
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void start() {
        getProcessContainer().start();
    }

    public void init(String[] args) {

        Options options = getOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Sre", options);
            System.exit(-1);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        if (cmd.hasOption("db")) {
            dbsOverride = cmd.getOptionValues("db");
        }

        // Load Hive Upgrade Stack.
//        String stackResource = "/h3_perf_procs.yaml";
        String stackResource = "/hive_sre_procs.yaml";

        try {
            URL configURL = this.getClass().getResource(stackResource);
            if (configURL == null) {
                throw new RuntimeException("Can build URL for Hive Upgrade Stack Resource: " + stackResource);
            }
            String yamlConfigDefinition = IOUtils.toString(configURL);
            ProcessContainer procContainer = mapper.readerFor(ProcessContainer.class).readValue(yamlConfigDefinition);

            if (cmd.hasOption("i")) {
                String[] includeIds = cmd.getOptionValues("i");
                List<String> includes = Arrays.asList(includeIds);
                // Disable all procs
                for (SreProcessBase proc: procContainer.getProcesses()) {
                    proc.setActive(false);
                }
                // Enable procs in 'include'
                for (SreProcessBase proc: procContainer.getProcesses()) {
                    if (includes.contains(proc.getId())) {
                        proc.setActive(true);
                    }
                }
            }

            setProcessContainer(procContainer);
            // Initialize with config and output directory.
            getProcessContainer().init(cmd.getOptionValue("cfg"), cmd.getOptionValue("o"), getDbsOverride());

        } catch (Exception e) {
            throw new RuntimeException("Missing resource file: " + stackResource, e);
        }
    }

    private Options getOptions() {

        // create Options object
        Options options = new Options();

        Option outputOption = new Option("o", "output-dir", true,
                "Output Directory to save results from Sre.");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        Option dbOption = new Option("db", "database", true,
                "Comma separated list of Databases.  Will override config. (upto 100)");
        dbOption.setValueSeparator(',');
        dbOption.setArgs(100);
        dbOption.setRequired(false);
        options.addOption(dbOption);

        Option includeOption = new Option("i", "include", true,
                "Comma separated list of process id's to run.  When not specified, ALL processes are run.");
        includeOption.setValueSeparator(',');
        includeOption.setArgs(100);
        includeOption.setRequired(false);
        options.addOption(includeOption);


        Option cfgOption = new Option("cfg", "config", true,
                "Config with details for the Sre Job.  Must match the either sre or u3 selection.");
        cfgOption.setRequired(true);
        options.addOption(cfgOption);

        return options;

    }

    @Override
    public String toString() {
        return "HiveStructureCheck{}";
    }
}
