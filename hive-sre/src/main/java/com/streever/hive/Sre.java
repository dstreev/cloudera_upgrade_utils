package com.streever.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.DbSetConfig;
import com.streever.hive.config.QueryDefinitions;
import com.streever.hive.sre.DbSet;
import com.streever.hive.sre.SreProcessBase;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Hello world!
 */
public class Sre {

    private SreProcessBase sreProcess;
//    private String outputDirectory;
//    private String[] overrideDbs;

    public SreProcessBase getSreProcess() {
        return sreProcess;
    }

    public void setSreProcess(SreProcessBase sreProcess) {
        this.sreProcess = sreProcess;
    }

    public static void main(String[] args) {
        Sre sre = new Sre();
        try {
            sre.init(args);
            sre.getSreProcess().start();
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
            formatter.printHelp("Eval", options);
            System.exit(-1);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        if (cmd.hasOption("u3")) {
            // Load Hive Upgrade Stack.
            String stackResource = "/hive_upgrade_set.yaml";

            try {
                URL configURL = this.getClass().getResource(stackResource);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Hive Upgrade Stack Resource: " + stackResource);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setSreProcess(mapper.readerFor(DbSet.class).readValue(yamlConfigDefinition));
                // Initialize with config and output directory.
                getSreProcess().initConfig(cmd.getOptionValue("cfg"), cmd.getOptionValue("o"));

            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " + stackResource, e);
            }

        }

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

        // add i option
        Option sreOption = new Option("sre", "sre", false, "Run the SRE suite of checks.");
        sreOption.setRequired(false);
        // Commons-Cli v1.3+ (can use currently because of Hadoop Commons-cli version is at 1.2.
        //        Option initOption = Option.builder("i").required(false)
        //                .argName("init set").desc("Initialize with set")
        //                .longOpt("init")
        //                .hasArg(true).numberOfArgs(1)
        //                .build();
        options.addOption(sreOption);

        Option u3Option = new Option("u3", "upgrade3", false,
                "Upgrade to Hive3 Checks");
        u3Option.setRequired(false);
        options.addOption(u3Option);

        // add f option
        Option outputOption = new Option("o", "output-dir", true,
                "Output Directory to save results from Sre.");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        // TODO: Need to figure out way to pass in an array of dbs.
        Option dbOption = new Option("db", "database", true,
                "Comman separated list of Databases.  Will override config.");
        dbOption.setRequired(false);
        options.addOption(dbOption);

        Option cfgOption = new Option("cfg", "config", true,
                "Config with details for the Sre Job.  Must match the either sre or u3 selection.");
        cfgOption.setRequired(true);
        options.addOption(cfgOption);

        return options;

    }

}
