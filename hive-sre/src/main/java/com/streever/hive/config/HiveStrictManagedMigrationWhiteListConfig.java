package com.streever.hive.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HiveStrictManagedMigrationWhiteListConfig {

    private static HiveStrictManagedMigrationWhiteListConfig single_instance = null;

    private HiveStrictManagedMigrationWhiteListConfig() {

    }

    public static HiveStrictManagedMigrationWhiteListConfig getInstance()
    {
        if (single_instance == null)
            single_instance = new HiveStrictManagedMigrationWhiteListConfig();
        return single_instance;
    }

    private Map<String, List<String>> databaseWhitelists = new TreeMap<String, List<String>>();

    public Map<String, List<String>> getDatabaseWhitelists() {
        return databaseWhitelists;
    }

    public void setDatabaseWhitelists(Map<String, List<String>> databaseWhitelists) {
        this.databaseWhitelists = databaseWhitelists;
    }

    public void addTable(String database, String table) {
        List<String> tables = databaseWhitelists.get(database);
        if (tables == null) {
            tables = new ArrayList<String>();
            databaseWhitelists.put(database, tables);
        }
        tables.add(table);
    }
}
