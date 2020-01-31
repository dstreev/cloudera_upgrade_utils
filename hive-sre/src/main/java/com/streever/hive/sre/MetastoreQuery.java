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
    private String resultMessageHeader;
    private String resultMessageDetailTemplate;

    @Override
    public void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException {
        setParent(parent);
        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }

        setOutputDirectory(outputDirectory);

        String[][] metastoreRecords = null;
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
            metastoreRecords = rarray.getColumns(listingColumns);
        } catch (SQLException e) {
            throw new RuntimeException("Issue getting 'databases' to process.", e);
        }

        if (metastoreRecords[0].length > 0) {
            success.println(this.getName());
            success.println(getResultMessageHeader());
            for (int i = 0; i < metastoreRecords[0].length; i++) {
                String[] record = new String[listingColumns.length];
                for (int j=0; j<listingColumns.length;j++) {
                    record[j] = metastoreRecords[j][i];
//                        serdeRecords[0][i], serdeRecords[1][i], serdeRecords[2][i]
                }
                String message = String.format(getResultMessageDetailTemplate(), record);
                success.println(message);
            }
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

    public String getResultMessageHeader() {
        return resultMessageHeader;
    }

    public void setResultMessageHeader(String resultMessageHeader) {
        this.resultMessageHeader = resultMessageHeader;
    }

    public String getResultMessageDetailTemplate() {
        return resultMessageDetailTemplate;
    }

    public void setResultMessageDetailTemplate(String resultMessageDetailTemplate) {
        this.resultMessageDetailTemplate = resultMessageDetailTemplate;
    }
}
