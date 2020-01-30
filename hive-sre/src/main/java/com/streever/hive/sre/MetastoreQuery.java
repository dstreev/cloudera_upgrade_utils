package com.streever.hive.sre;

import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetastoreQuery extends SreProcessBase {

    private String metastoreQuery;
    private String[] listingColumns;
    private String resultMessage;

    @Override
    public void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException {
        setParent(parent);
        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        setOutputDirectory(outputDirectory);

        String[][] serdeRecords = null;
        try (Connection conn = getParent().getConnectionPools().getMetastoreDirectConnection()) {
            String targetQueryDef = this.metastoreQuery;
            // build prepared statement for targetQueryDef
            QueryDefinition queryDefinition = getQueryDefinitions().getQueryDefinition(targetQueryDef);
            PreparedStatement preparedStatement = JDBCUtils.getPreparedStatement(conn, queryDefinition);
            // apply any overrides from the user configuration.
            QueryDefinition queryOverride = getQueryOverride(targetQueryDef);
            JDBCUtils.setPreparedStatementParameters(preparedStatement, queryDefinition, queryOverride);
            // Run
            ResultSet check = preparedStatement.executeQuery();
            // Convert Result to an array
            ResultArray rarray = new ResultArray(check);
            // Close ResultSet
            check.close();
            // build array of columns
            serdeRecords = rarray.getColumns(listingColumns);
        } catch (SQLException e) {
            throw new RuntimeException("Issue getting 'databases' to process.", e);
        }

        success.println(" Questionable Serde's and References");
        if (serdeRecords[0].length > 0) {
            success.println("   Listed tables should be review to ensure the Serde is still available.");
            success.println("   Missing Serde's can disrupt a Hive Upgrade/Migration Process");

            for (int i = 0; i < serdeRecords[0].length; i++) {
                String[] record = {serdeRecords[0][i],serdeRecords[1][i],serdeRecords[2][i]};
                String message = String.format("Database/Table: %1$s.%2$s is using a non base serde '%3$s'", record);
                success.println(message);
            }
        } else {
            success.println("  No questionable Serde's found.");
        }

    }

    public String getMetastoreQuery() {
        return metastoreQuery;
    }

    public void setMetastoreQuery(String metastoreQuery) {
        this.metastoreQuery = metastoreQuery;
    }

    public String[] getListingColumns() {
        return listingColumns;
    }

    public void setListingColumns(String[] listingColumns) {
        this.listingColumns = listingColumns;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
