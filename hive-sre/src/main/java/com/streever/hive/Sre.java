package com.streever.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.sre.ProcessContainer;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.URL;

/**
 * Hello world!
 */
public class Sre {

    private ProcessContainer processContainer;
//    private String outputDirectory;
    private String[] dbsOverride;

//    public String getOutputDirectory() {
//        return outputDirectory;
//    }
//
//    public void setOutputDirectory(String outputDirectory) {
//        this.outputDirectory = outputDirectory;
//    }

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
        Sre sre = new Sre();
//        ScriptEngineManager sem = new ScriptEngineManager();
//        ScriptEngine nashorn = sem.getEngineByName("nashorn");
//
//        String name = "David";
//        Integer result = null;
//
//        try {
//            nashorn.eval("print('" + name + "')");
//            result = (Integer) nashorn.eval("10 + 2");
//
//        } catch (ScriptException se) {
//
//        }
        try {
            sre.init(args);
//            if (sre.getDbsOverride() != null && sre.getDbsOverride().length > 0) {
//                sre.getProcessContainer().set;
//            }
//            sre.getProcessContainer().setOutputDirectory(sre.getOutputDirectory());
            sre.getProcessContainer().start();
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void init(String[] args) throws Throwable {
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
//        if (cmd.hasOption("o")) {
//            this.outputDirectory = cmd.getOptionValue("o");
//        }

        if (cmd.hasOption("u3")) {
            // Load Hive Upgrade Stack.
            String stackResource = "/h3_upg_procs.yaml";

            try {
                URL configURL = this.getClass().getResource(stackResource);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Hive Upgrade Stack Resource: " + stackResource);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setProcessContainer(mapper.readerFor(ProcessContainer.class).readValue(yamlConfigDefinition));
                // Initialize with config and output directory.
                getProcessContainer().init(cmd.getOptionValue("cfg"), cmd.getOptionValue("o"), getDbsOverride());

            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " + stackResource, e);
            }
        }
        if (cmd.hasOption("sre")) {
            // Load Hive Upgrade Stack.
            String stackResource = "/h3_perf_procs.yaml";

            try {
                URL configURL = this.getClass().getResource(stackResource);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Hive Upgrade Stack Resource: " + stackResource);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setProcessContainer(mapper.readerFor(ProcessContainer.class).readValue(yamlConfigDefinition));

                //                if (getOverrideDbs() != null && getOverrideDbs().length > 0) {
                //                    for (SreProcesses processes: getProcesses()) {
                //
                //                    }
                //                }
                // Initialize with config and output directory.

                getProcessContainer().init(cmd.getOptionValue("cfg"), cmd.getOptionValue("o"), getDbsOverride());

            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " + stackResource, e);
            }
        }

//        System.out.println(dbsOpt);

    }

    private Options getOptions() {

        // Options..
        //   sre - perform sre functions for hive
        //   u3 - upgrade to 3 checks
        //   o - output directory to use for base file output
        //   db - override the db to check, default will pick the one in the cfg
        //   cfg - config file to use.  Will need to match sre or u3 run types.

        // create Options object
        Options options = new Options();
        OptionGroup procGroup = new OptionGroup();

        // add i option
        Option sreOption = new Option("sre", "sre", false, "Run the SRE suite of checks.");
        sreOption.setRequired(true);
        // Commons-Cli v1.3+ (can use currently because of Hadoop Commons-cli version is at 1.2.
        //        Option initOption = Option.builder("i").required(false)
        //                .argName("init set").desc("Initialize with set")
        //                .longOpt("init")
        //                .hasArg(true).numberOfArgs(1)
        //                .build();
        procGroup.addOption(sreOption);
//        options.addOption(sreOption);


        Option u3Option = new Option("u3", "upgrade3", false,
                "Upgrade to Hive3 Checks");
        u3Option.setRequired(true);
        procGroup.addOption(u3Option);
//        options.addOption(u3Option);

        procGroup.setRequired(true);
        options.addOptionGroup(procGroup);

        // add f option
        Option outputOption = new Option("o", "output-dir", true,
                "Output Directory to save results from Sre.");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        // TODO: Need to figure out way to pass in an array of dbs.
        Option dbOption = new Option("db", "database", true,
                "Comma separated list of Databases.  Will override config. (upto 100)");
        dbOption.setValueSeparator(',');
        dbOption.setArgs(100);
        dbOption.setRequired(false);
        options.addOption(dbOption);

        Option cfgOption = new Option("cfg", "config", true,
                "Config with details for the Sre Job.  Must match the either sre or u3 selection.");
        cfgOption.setRequired(true);
        options.addOption(cfgOption);

        return options;

    }

}
