package com.streever.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.streever.sql.QueryDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class SreProcessesConfig {
    @JsonProperty(value = "metastore_direct")
    private Metastore metastoreDirect;
    private Metastore hs2;
    private int parallelism = 2;

    @JsonProperty("queries")
    private Map<String, QueryDefinition> queries = new LinkedHashMap<String, QueryDefinition>();

    public QueryDefinition getQuery(String name) {
        return queries.get(name);
    }

    public Metastore getMetastoreDirect() {
        return metastoreDirect;
    }

    public void setMetastoreDirect(Metastore metastoreDirect) {
        this.metastoreDirect = metastoreDirect;
    }

    public Metastore getHs2() {
        return hs2;
    }

    public void setHs2(Metastore hs2) {
        this.hs2 = hs2;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }
}
