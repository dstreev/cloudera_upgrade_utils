package com.streever.hive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.reporting.ReportingConf;
import com.streever.hive.sre.ProcessContainer;
import com.streever.hive.sre.SreProcessBase;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class HiveFrameworkCheck implements SreSubApp {
    private String stackResource;
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

    public String getStackResource() {
        return stackResource;
    }

    public void setStackResource(String stackResource) {
        this.stackResource = stackResource;
    }

    public HiveFrameworkCheck(String stackResource) {
        this.stackResource = stackResource;
    }

    public HiveFrameworkCheck() {
    }

    //    public static void main(String[] args) {
//        HiveFrameWorkCheck sre = new HiveFrameWorkCheck();
//        try {
//            sre.init(args);
//            sre.start();
//            System.exit(0);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//
//    }

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

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println(ReportingConf.substituteVariables("v.${Implementation-Version}"));
            formatter.printHelp("Sre", options);
            System.exit(-1);
        }

        if (getStackResource() == null) {
            if (cmd.hasOption("hfw")) {
                setStackResource(cmd.getOptionValue("hfw"));
            }
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        if (cmd.hasOption("db")) {
            dbsOverride = cmd.getOptionValues("db");
        }

        // Should be set by now. If 'cust' option used, then it's set above with the -hfw option.  If that wasn't
        // present, this gets triggered.
        if (getStackResource() == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("'cust' requires `-hfw` <framework_resource>", options);
            System.exit(-1);
        }

        ProcessContainer procContainer = null;
        // First look for it as a Resource (in classpath)
        URL configURL = this.getClass().getResource(getStackResource());
        if (configURL != null) {
            String yamlConfigDefinition = null;
            try {
                yamlConfigDefinition = IOUtils.toString(configURL);
            } catch (IOException e) {
                throw new RuntimeException("Issue converting config: " + getStackResource(), e);
            }
            try {
                procContainer = mapper.readerFor(ProcessContainer.class).readValue(yamlConfigDefinition);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Issue deserializing config: " + getStackResource(), e);
            }
        } else {
            throw new RuntimeException("Couldn't locate 'HiveFramework Configuration File': " +
                    configURL.toString());
        }

        if (cmd.hasOption("i")) {
            String[] includeIds = cmd.getOptionValues("i");
            List<String> includes = Arrays.asList(includeIds);
            // Disable all procs
            for (SreProcessBase proc : procContainer.getProcesses()) {
                proc.setActive(false);
            }
            // Enable procs in 'include'
            for (SreProcessBase proc : procContainer.getProcesses()) {
                if (includes.contains(proc.getId())) {
                    proc.setActive(true);
                }
            }
        }
        setProcessContainer(procContainer);
        // Initialize with config and output directory.
        String configFile = null;
        if (cmd.hasOption("cfg")) {
            configFile = cmd.getOptionValue("cfg");
        } else {
            configFile = System.getProperty("user.home") + System.getProperty("file.separator") + ".hive-sre/cfg/default.yaml";
        }
        getProcessContainer().init(configFile, cmd.getOptionValue("o"), getDbsOverride());

    }

    private Options getOptions() {

        // create Options object
        Options options = new Options();

        Option helpOption = new Option("h", "help", false,
                "Help");
        helpOption.setRequired(false);
        options.addOption(helpOption);

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
                "Config with details for the Sre Job.  Must match the either sre or u3 selection. Default: $HOME/.hive-sre/cfg/default.yaml");
        cfgOption.setRequired(false);
        options.addOption(cfgOption);

        Option hfwOption = new Option("hfw", "hive-framework", true,
                "The custom HiveFramework check configuration. Needs to be in the 'Classpath'.");
        hfwOption.setRequired(false);
        options.addOption(hfwOption);

        return options;

    }

    @Override
    public String toString() {
        return "HiveFrameworkCheck{}";
    }
}
