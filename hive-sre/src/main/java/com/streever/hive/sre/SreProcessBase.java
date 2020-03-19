package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.QueryDefinitions;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.sql.QueryDefinition;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;

@JsonIgnoreProperties({"parent", "config", "queryDefinitions", "dbsOverride", "outputDirectory", "success", "error"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DbSet.class, name = "dbSet"),
        @JsonSubTypes.Type(value = MetastoreQuery.class, name = "metastore.query")})
public abstract class SreProcessBase {
    private String name = "not set";
    private Boolean active = Boolean.TRUE;

    private ProcessContainer parent;

    // Build after construction
    private QueryDefinitions queryDefinitions = null;
    private String queryDefinitionReference = null;

    private String[] dbsOverride = {};
    private String errorFilename = null;
    private String successFilename = null;

    /**
     * allows stdout to be captured if necessary
     */
    public PrintStream success = System.out;
    /**
     * allows stderr to be captured if necessary
     */
    public PrintStream error = System.err;

    // Set during init.
    private String outputDirectory = null;

    // Set after construction.
    private SreProcessesConfig config = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ProcessContainer getParent() {
        return parent;
    }

    public void setParent(ProcessContainer parent) {
        this.parent = parent;
    }

    public String getQueryDefinitionReference() {
        return queryDefinitionReference;
    }

    public void setQueryDefinitionReference(String queryDefinitionReference) {
        this.queryDefinitionReference = queryDefinitionReference;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            try {
                URL configURL = this.getClass().getResource(this.queryDefinitionReference);
                if (configURL == null) {
                    throw new RuntimeException("Can build URL for Resource: " +
                            this.queryDefinitionReference);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setQueryDefinitions(mapper.readerFor(QueryDefinitions.class).readValue(yamlConfigDefinition));
            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " +
                        this.queryDefinitionReference, e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Issue getting configs", e);
        }

    }

    protected QueryDefinitions getQueryDefinitions() {
        return queryDefinitions;
    }

    protected void setQueryDefinitions(QueryDefinitions queryDefinitions) {
        this.queryDefinitions = queryDefinitions;
    }

    public void setConfig(SreProcessesConfig config) {
        this.config = config;
    }

    public SreProcessesConfig getConfig() {
        return getParent().getConfig();
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

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) throws FileNotFoundException {
        this.outputDirectory = outputDirectory;
        error = outputFile(outputDirectory + System.getProperty("file.separator") + this.getErrorFilename());
        success = outputFile(outputDirectory + System.getProperty("file.separator") + this.getSuccessFilename());
    }

    protected PrintStream outputFile(String name) throws FileNotFoundException {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
    }

    protected QueryDefinition getQueryOverride(String definitionName) {
        QueryDefinition rtn = null;
        rtn = getParent().getConfig().getQuery(definitionName);
        return rtn;
    }

    public abstract void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException;

}
