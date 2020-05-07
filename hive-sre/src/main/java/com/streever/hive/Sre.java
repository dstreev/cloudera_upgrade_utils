package com.streever.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.perf.JDBCPerfTest;
import com.streever.hive.sre.ProcessContainer;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.URL;
import java.util.Arrays;

/**
 * Hello world!
 */
public class Sre {

    public static void main(String[] args) {
        Sre sre = new Sre();
        try {
            sre.init(args);
            System.exit(0);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
            System.exit(0);
        }
    }

    public void init(String[] args) throws Throwable {
        // Check first item in args.  It should be one of:
        // - perf
        // - sre
        // - u3
        String subApp = null;
        if (args.length > 0 && args[0].matches("perf|sre|u3")) {
            System.out.println("Launching: " + args[0]);
            subApp = args[0];
        } else {
            System.out.println("First element must be one of: perf,sre,u3");
            System.exit(-1);
        }

        // Remove the first item from the args and pass on to sub application
        String[] appArgs = Arrays.copyOfRange(args, 1, args.length);

        // Then choose which sub application to start
        SreSubApp sreApp = null;
        switch (subApp) {
            case "perf":
                sreApp = new JDBCPerfTest();
                break;
            case "sre":
                sreApp = new HiveStructureCheck();
                break;
            case "u3":
                sreApp = new UpgradeToHive3();
                break;
        }

        sreApp.init(appArgs);
        sreApp.start();

    }

}
