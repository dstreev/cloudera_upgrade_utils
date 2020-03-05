package com.streever.hive.perf;

import com.streever.hive.reporting.ReportingConf;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import static java.sql.Types.*;
import static java.sql.Types.TIMESTAMP;
//import org.apache.hive.jdbc.HiveDriver;

public class JDBCRecordIterator implements Runnable {

    private String jdbcUrl;
    private String username;
    private String password;
    private String query;
    private Integer batchSize = 10000;
    private Integer delayWarning = 1000;
    private Boolean lite = Boolean.FALSE;
    private AtomicLong count = new AtomicLong(0);
    private AtomicLong size = new AtomicLong(0);
    private Date start;
//    private Delay[] delays = new Delay[10];
    private Deque<Delay> delays = new ConcurrentLinkedDeque<Delay>();

    public AtomicLong getCount() {
        return count;
    }

    public AtomicLong getSize() {
        return size;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getDelayWarning() {
        return delayWarning;
    }

    public void setDelayWarning(Integer delayWarning) {
        this.delayWarning = delayWarning;
    }

    public Boolean getLite() {
        return lite;
    }

    public void setLite(Boolean lite) {
        this.lite = lite;
    }

    public Date getStart() {
        return start;
    }

    public Statistic getStat() {
        Statistic stat = Statistic.build(count.get(), size.get());
        return stat;
    }

    public void pushDelay(Delay delay) {
        delays.add(delay);
        if (delays.size() > 10)
            delays.removeFirst();
    }

    public void printDelays() {
        if (delays.size() > 0) {
            System.out.println(ReportingConf.ANSI_GREEN + "-----------------------------------" + ReportingConf.ANSI_RED);
            for (Iterator iter = delays.iterator(); iter.hasNext(); ) {
                Delay delay = (Delay) iter.next();
                System.out.println(delay);
            }
            System.out.println(ReportingConf.ANSI_GREEN + "-----------------------------------" + ReportingConf.ANSI_RESET);
        }
    }

    @Override
    public void run() {
        start = new Date();

        try  {
            Connection conn = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
            Statement stmt = conn.createStatement();
            stmt.setFetchSize(this.batchSize);
            ResultSet rs = stmt.executeQuery(this.query);
            int[] columnTypes = null;
            if (!lite) {
                columnTypes = new int[rs.getMetaData().getColumnCount()];
                for (int i=0;i<rs.getMetaData().getColumnCount();i++) {
                    columnTypes[i] = rs.getMetaData().getColumnType(i+1);
                }
            }
            long marker = System.currentTimeMillis();
            while (rs.next()) {
                if (System.currentTimeMillis() - marker > delayWarning) {
                    Delay delay = new Delay(System.currentTimeMillis() - marker, count.get());//                    delays.addLast(delay);
                    pushDelay(delay);
                }
                // reset marker
                marker = System.currentTimeMillis();
                long currentCount = count.getAndIncrement();
                if (!lite) {
                    for (int i = 0; i < columnTypes.length; i++) {
                        switch (columnTypes[i]) {
                            case BIT:
                                size.getAndAdd(1);//rs.getByte(i+1));
                                break;
                            case TINYINT:
                                size.getAndAdd(1);
                                break;
                            case SMALLINT:
                                size.getAndAdd(2);
                                break;
                            case INTEGER:
                                size.getAndAdd(4);
                                break;
                            case BIGINT:
                                size.getAndAdd(8);
                                break;
                            case FLOAT:
                                size.getAndAdd(4);
                                break;
                            case REAL:
                            case DOUBLE:
                                size.getAndAdd(8);
                                break;
                            case NUMERIC:
                                size.getAndAdd(8);
                                break;
                            case DECIMAL:
                                size.getAndAdd(8);
                                break;
                            case CHAR:
                            case VARCHAR:
                            case LONGVARCHAR:
                                String check = rs.getString(i+1);
                                if (check != null)
                                    size.getAndAdd(check.length());
                                break;
                            case DATE:
                            case TIME:
                            case TIMESTAMP:
                                size.getAndAdd(8);
                                break;
                        }
                    }
                }
            }
            stmt.close();
            rs.close();
        } catch (SQLException se) {
            System.out.println("Error" + se.getMessage());
//        } catch (ClassNotFoundException cnfe) {
//            System.out.println("Error" + cnfe.getMessage());
        }
    }

}