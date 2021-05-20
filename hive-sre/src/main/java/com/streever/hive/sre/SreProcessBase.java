package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.config.Metastore;
import com.streever.hive.config.QueryDefinitions;
import com.streever.hive.config.SreProcessesConfig;
import com.streever.hive.reporting.CounterGroup;
import com.streever.sql.QueryDefinition;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.concurrent.Callable;

@JsonIgnoreProperties({"parent", "config", "queryDefinitions", "dbsOverride", "dbType", "outputDirectory", "success", "error", "counterGroup"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DbSetProcess.class, name = "dbSet"),
        @JsonSubTypes.Type(value = MetastoreQueryProcess.class, name = "metastore.query"),
        @JsonSubTypes.Type(value = MetastoreReportProcess.class, name = "metastore.report")})
public abstract class SreProcessBase implements Callable<String> {
    private String displayName = "not set";
    private String title = null;
    private String note = null;
    private String id = null;
    private Boolean skip = Boolean.FALSE;
    private Boolean active = Boolean.TRUE;
    protected Boolean initializing = Boolean.TRUE;

    private Metastore.DB_TYPE dbType = Metastore.DB_TYPE.MYSQL;

    private ProcessContainer parent;

    // Build after construction
    private QueryDefinitions queryDefinitions = null;
    private String queryDefinitionReference = null;

    private String[] dbsOverride = {};
    private String header = null;
    private String errorDescription = null;
    private String successDescription = null;
    private String errorFilename = null;
    private String successFilename = null;

    protected CounterGroup counterGroup;

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

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Boolean isInitializing() {
        return initializing;
    }

    public void setInitializing(Boolean initializing) {
        this.initializing = initializing;
    }

    public String getUniqueName() {
        if (id != null) {
            return id + ":" + displayName;
        } else {
            return displayName;
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public Metastore.DB_TYPE getDbType() {
        return dbType;
    }

    public void setDbType(Metastore.DB_TYPE dbType) {
        this.dbType = dbType;
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

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getSuccessDescription() {
        return successDescription;
    }

    public void setSuccessDescription(String successDescription) {
        this.successDescription = successDescription;
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
        if (getErrorFilename() != null) {
            error = outputFile(outputDirectory + System.getProperty("file.separator") + this.getErrorFilename());
        }
        if (getSuccessFilename() != null) {
            success = outputFile(outputDirectory + System.getProperty("file.separator") + this.getSuccessFilename());
        }
    }

    protected PrintStream outputFile(String name) throws FileNotFoundException {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
    }

    protected QueryDefinition getQueryOverride(String definitionName) {
        QueryDefinition rtn = null;
        rtn = getParent().getConfig().getQuery(definitionName);
        return rtn;
    }

    public String getOutputDetails() {
        StringBuilder sb = new StringBuilder();
        if (getSuccessFilename() != null) {
            sb.append("\t" + getSuccessDescription() + " -> " + getOutputDirectory() + System.getProperty("file.separator") +
                    getSuccessFilename() + "\n");
        }
        if (getErrorFilename() != null) {
            sb.append("\t" + getErrorDescription() + " -> " + getOutputDirectory() + System.getProperty("file.separator") +
                    getErrorFilename());
        }
        return sb.toString();
    }

    public void init(ProcessContainer parent) throws FileNotFoundException {
        setParent(parent);
        setInitializing(Boolean.TRUE);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String dbQueryDefReference = "/" + dbType.toString() + this.queryDefinitionReference;
            try {
                URL configURL = this.getClass().getResource(dbQueryDefReference);
                if (configURL == null) {
                    throw new RuntimeException("Can't build URL for Resource: " +
                            dbQueryDefReference);
                }
                String yamlConfigDefinition = IOUtils.toString(configURL);
                setQueryDefinitions(mapper.readerFor(QueryDefinitions.class).readValue(yamlConfigDefinition));
            } catch (Exception e) {
                throw new RuntimeException("Missing resource file: " +
                        dbQueryDefReference, e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Issue getting configs", e);
        }
    }

}
