package com.streever.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.streever.sql.QueryDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryDefinitions {

    @JsonProperty("query_definitions")
    private Map<String, QueryDefinition> queryDefinitions = new LinkedHashMap<String, QueryDefinition>();

    public QueryDefinition getQueryDefinition(String name) {
        return queryDefinitions.get(name);
    }

    //    private String selectDbs;
//
//    public String getSelectDbs() {
//        return selectDbs;
//    }
//
//    public void setSelectDbs(String selectDbs) {
//        this.selectDbs = selectDbs;
//    }
}
