package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.hive.reporting.Reporter;

import java.util.concurrent.ScheduledExecutorService;

@JsonIgnoreProperties({"parent", "config"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DbSet.class, name = "dbSet")})
public abstract class SreProcessBase {
    private String name;
    private SreProcesses parent;

    // Set after construction.
    private SreProcessesConfig config = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SreProcesses getParent() {
        return parent;
    }

    public void setParent(SreProcesses parent) {
        this.parent = parent;
    }

    public SreProcessesConfig getConfig() {
        return config;
    }

    public void setConfig(SreProcessesConfig config) {
        this.config = config;
    }

    public abstract void init(SreProcesses parent, String outputDirectory);

}
