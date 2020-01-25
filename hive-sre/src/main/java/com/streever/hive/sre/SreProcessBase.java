package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hive.reporting.Reporter;

@JsonIgnoreProperties({"reporter"})
public abstract class SreProcessBase {
    private String name;
    private Reporter reporter = new Reporter();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        reporter.setName(name);
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public abstract void initConfig(String filename, String outputDirectory);

    public abstract void start();
}
