package com.streever.hive.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.streever.hive.sre.ProcessContainer;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HiveStrictManagedMigrationWhiteListConfigTest {

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void test01() {
        HiveStrictManagedMigrationWhiteListConfig hsmmCfg = HiveStrictManagedMigrationWhiteListConfig.getInstance();
        List<String> tables = new ArrayList<String>();
        tables.add("call_center");
        tables.add("customer");
        hsmmCfg.getDatabaseWhitelists().put("tpcds_bin_partitioned_x", tables);
        String hsmmStr = null;
        try {
            hsmmStr = mapper.writeValueAsString(hsmmCfg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(hsmmStr);
    }

    @Test
    public void test02() {
        String cfgResource = "/hsmm_cfg_01.yaml";
        URL configURL = this.getClass().getResource(cfgResource);
        String yamlConfig = null;
        HiveStrictManagedMigrationWhiteListConfig hsmmCfg = null;
        try {
            yamlConfig = IOUtils.toString(configURL);
        } catch (IOException e) {
            throw new RuntimeException("Issue converting config: " + cfgResource, e);
        }
        try {
            hsmmCfg = mapper.readerFor(HiveStrictManagedMigrationWhiteListConfig.class).readValue(yamlConfig);
            List<String> whitelist = hsmmCfg.getDatabaseWhitelists().get("tpcds_bin_partitioned_orc_10");
            assert(whitelist != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Issue deserializing config: " + cfgResource, e);
        }
    }
}