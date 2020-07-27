package com.streever.hive.config;


import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Properties;

public class Metastore {
    public enum DB_TYPE { MYSQL, POSTGRES, ORACLE, MSSQL };
    @NotNull(message = "Missing Metastore JDBC URI")
    private String uri;
    @NotNull(message = "Need to specify one of: MYSQL, ORACLE, POSTGRES, MSSQL")
    private DB_TYPE type;
    @NotNull
    private Properties connectionProperties;
    @NotNull
    private ConnectionPool connectionPool;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public DB_TYPE getType() {
        return type;
    }

    public void setType(DB_TYPE type) {
        this.type = type;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
