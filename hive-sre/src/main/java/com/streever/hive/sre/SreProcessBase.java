package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.streever.hive.config.SreProcessesConfig;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

@JsonIgnoreProperties({"parent", "config", "dbsOverride", "outputDirectory"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DbSet.class, name = "dbSet")})
public abstract class SreProcessBase {
    private String name;
    private ProcessContainer parent;
    private String[] dbsOverride = null;
    private String errorFilename = null;
    private String successFilename = null;

    // Set after construction.
    private SreProcessesConfig config = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessContainer getParent() {
        return parent;
    }

    public void setParent(ProcessContainer parent) {
        this.parent = parent;
    }

    public SreProcessesConfig getConfig() {
        return config;
    }

    public void setConfig(SreProcessesConfig config) {
        this.config = config;
    }

    public String[] getDbsOverride() {
        return dbsOverride;
    }

    public void setDbsOverride(String[] dbsOverride) {
        this.dbsOverride = dbsOverride;
    }

    public String getErrorFilename() {
        return errorFilename;
    }

    public void setErrorFilename(String errorFilename) {
        this.errorFilename = errorFilename;
    }

    public String getSuccessFilename() {
        return successFilename;
    }

    public void setSuccessFilename(String successFilename) {
        this.successFilename = successFilename;
    }

    protected PrintStream outputFile(String name) throws FileNotFoundException {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
    }

    public abstract void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException;

}
