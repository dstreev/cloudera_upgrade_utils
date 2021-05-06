package com.streever.hive.sre;

import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;
import com.streever.hive.reporting.ReportingConf;
import com.streever.sql.JDBCUtils;
import com.streever.sql.QueryDefinition;
import com.streever.sql.ResultArray;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import static com.streever.hive.reporting.ReportCounter.*;

public class MetastoreQueryProcess extends MetastoreProcess {

    private MetastoreQuery metastoreQueryDefinition;

    @Override
    public void run() {
        setStatus(PROCESSING);
        String[][] metastoreRecords = null;
        this.setTotalCount(1);
        try (Connection conn = getParent().getConnectionPools().getMetastoreDirectConnection()) {
            String targetQueryDef = this.getMetastoreQueryDefinition().getQuery();
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
            metastoreRecords = rarray.getColumns(getMetastoreQueryDefinition().getListingColumns());
        } catch (SQLException e) {
            incError(1);
            error.println(metastoreQueryDefinition.getQuery());
            error.println("> Processing Issue: " + e.getMessage());
            setStatus(ERROR);
            return;
        }

        if (metastoreRecords[0] != null && metastoreRecords[0].length > 0) {
            if (getTitle() != null)
                success.println(ReportingConf.substituteVariables(getTitle()));
            if (getHeader() != null)
                success.println(this.getHeader());
            if (getNote() != null)
                success.println(this.getNote());

            if (getMetastoreQueryDefinition().getResultMessageHeader() != null) {
                success.println(getMetastoreQueryDefinition().getResultMessageHeader());
            }
            if (getMetastoreQueryDefinition().getResultMessageDetailHeader() != null) {
                success.println(getMetastoreQueryDefinition().getResultMessageDetailHeader());
            }
            for (int i = 0; i < metastoreRecords[0].length; i++) {
                incSuccess(1);
                String[] record = new String[getMetastoreQueryDefinition().getListingColumns().length];
                for (int j = 0; j< getMetastoreQueryDefinition().getListingColumns().length; j++) {
                    record[j] = metastoreRecords[j][i];
//                        serdeRecords[0][i], serdeRecords[1][i], serdeRecords[2][i]
                }
                String message = String.format(getMetastoreQueryDefinition().getResultMessageDetailTemplate(), record);
                success.println(message);
            }
            incSuccess(1);
            incProcessed(1);
        } else {
            success.println(getMetastoreQueryDefinition().getResultMessageHeader());
            success.println("\n > **Results empty**\n");
        }
        setStatus(COMPLETED);
        setActive(false);
    }

    public MetastoreQuery getMetastoreQueryDefinition() {
        return metastoreQueryDefinition;
    }

    public void setMetastoreQueryDefinition(MetastoreQuery metastoreQueryDefinition) {
        this.metastoreQueryDefinition = metastoreQueryDefinition;
    }

    @Override
    public String toString() {
        return "MetastoreQueryProcess{}";
    }
}
