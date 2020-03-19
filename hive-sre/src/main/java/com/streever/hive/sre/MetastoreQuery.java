package com.streever.hive.sre;

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
import java.util.List;
import static com.streever.hive.reporting.ReportCounter.*;

public class MetastoreQuery extends SreProcessBase implements Counter, Runnable {

    private String metastoreQuery;
    private String[] listingColumns;
    private String resultMessageHeader;
    private String resultMessageDetailTemplate;

    private ReportCounter counter = new ReportCounter();

    @Override
    public void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException {
        setParent(parent);
        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }
        setOutputDirectory(outputDirectory);
        setStatus(WAITING);
    }

    @Override
    public void run() {
        setStatus(PROCESSING);
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

        if (metastoreRecords[0] != null && metastoreRecords[0].length > 0) {
            success.println(this.getName());
            success.println(getResultMessageHeader());
            for (int i = 0; i < metastoreRecords[0].length; i++) {
                incSuccess(1);
                String[] record = new String[listingColumns.length];
                for (int j=0; j<listingColumns.length;j++) {
                    record[j] = metastoreRecords[j][i];
//                        serdeRecords[0][i], serdeRecords[1][i], serdeRecords[2][i]
                }
                String message = String.format(getResultMessageDetailTemplate(), record);
                success.println(message);
            }
        }
        setStatus(COMPLETED);
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

    @Override
    public void setName(String name) {
        super.setName(name);
        this.getCounter().setName(name);
    }

    public ReportCounter getCounter() {
        return counter;
    }

    public void setCounter(ReportCounter counter) {
        this.counter = counter;
    }

    @Override
    public int getStatus() {
        return counter.getStatus();
    }

    @Override
    public String getStatusStr() {
        return counter.getStatusStr();
    }

    @Override
    public List<ReportCounter> getCounterChildren() {
        return counter.getChildren();
    }

    @Override
    public void setStatus(int status) {
        counter.setStatus(status);
    }

    @Override
    public void incProcessed(int increment) {
        counter.incProcessed(increment);
    }

    @Override
    public long getProcessed() {
        return counter.getProcessed();
    }

    @Override
    public void setProcessed(long processed) {
        counter.setProcessed(processed);
    }

    @Override
    public long getTotalCount() {
        return counter.getTotalCount();
    }

    @Override
    public void setTotalCount(long totalCount) {
        counter.setTotalCount(totalCount);
    }

    @Override
    public void incSuccess(int increment) {
        counter.incSuccess(increment);
    }

    @Override
    public long getSuccess() {
        return counter.getSuccess();
    }

    @Override
    public void setSuccess(long success) {
        counter.setSuccess(success);
    }

    @Override
    public void incError(int increment) {
        counter.incError(increment);
    }

    @Override
    public long getError() {
        return counter.getError();
    }

    @Override
    public void setError(long error) {
        counter.setError(error);
    }

    @Override
    public String toString() {
        return "MetastoreQuery{}";
    }
}
