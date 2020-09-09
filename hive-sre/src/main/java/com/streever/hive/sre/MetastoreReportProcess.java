package com.streever.hive.sre;

import com.streever.hive.config.Metastore;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.streever.hive.reporting.ReportCounter.*;

public class MetastoreReportProcess extends MetastoreProcess {

    private List<MetastoreQuery> metastoreQueryDefinitions = new ArrayList<MetastoreQuery>();

    @Override
    public void run() {
        setStatus(PROCESSING);
        success.println(this.getHeader());
        this.setTotalCount(getMetastoreQueryDefinitions().size());
        for (MetastoreQuery metastoreQueryDefinition: getMetastoreQueryDefinitions()) {
            String[][] metastoreRecords = null;
            try (Connection conn = getParent().getConnectionPools().getMetastoreDirectConnection()) {
                String targetQueryDef = metastoreQueryDefinition.getQuery();
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
                metastoreRecords = rarray.getColumns(metastoreQueryDefinition.getListingColumns());

                if (metastoreRecords[0] != null && metastoreRecords[0].length > 0) {
                    if (metastoreQueryDefinition.getResultMessageHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageHeader());
                    }
                    if (metastoreQueryDefinition.getResultMessageDetailHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageDetailHeader());
                    }
                    for (int i = 0; i < metastoreRecords[0].length; i++) {
//                    incSuccess(1);
                        String[] record = new String[metastoreQueryDefinition.getListingColumns().length];
                        for (int j = 0; j < metastoreQueryDefinition.getListingColumns().length; j++) {
                            record[j] = metastoreRecords[j][i];
//                        serdeRecords[0][i], serdeRecords[1][i], serdeRecords[2][i]
                        }
                        String message = String.format(metastoreQueryDefinition.getResultMessageDetailTemplate(), record);
                        success.println(message);
                    }
                } else {
                    if (metastoreQueryDefinition.getResultMessageHeader() != null) {
                        success.println(metastoreQueryDefinition.getResultMessageHeader());
                    }
                    success.println("\n> **Results empty**\n");
                }
                incSuccess(1);
            } catch (SQLException e) {
                incError(1);
                error.println(metastoreQueryDefinition.getQuery());
                error.println("> Processing Issue: " + e.getMessage());
//                setStatus(ERROR);
//                throw new RuntimeException("Issue getting 'databases' to process.", e);
            }
            incProcessed(1);
        }
        setStatus(COMPLETED);
    }


    public List<MetastoreQuery> getMetastoreQueryDefinitions() {
        return metastoreQueryDefinitions;
    }

    public void setMetastoreQueryDefinitions(List<MetastoreQuery> metastoreQueryDefinitions) {
        this.metastoreQueryDefinitions = metastoreQueryDefinitions;
    }

    @Override
    public String toString() {
        return "MetastoreReportProcess{}";
    }
}
