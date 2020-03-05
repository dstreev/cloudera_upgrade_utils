package com.streever.hive.perf;

import com.streever.hive.reporting.ReportingConf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

public class CollectStatistics extends Thread {

    private JDBCRecordIterator jri;

    private DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private Long[] windows = {60000l, 180000l, 300000l, 600000l};
    private Map<Long, PerfWindow> perfWindows = new TreeMap<Long, PerfWindow>();

    public JDBCRecordIterator getJri() {
        return jri;
    }

    public Long[] getWindows() {
        return windows;
    }

    public void setWindows(Long[] windows) {
        this.windows = windows;
    }

    public CollectStatistics(JDBCRecordIterator jri) {
        this.jri = jri;
    }

    private void setUpWindows() {
        for (Long window : windows) {
            PerfWindow perfWindow = new PerfWindow(window);
            perfWindows.put(window, perfWindow);
        }
    }

    private void addToPerfWindows(Statistic statistic) {
        for (Long window : windows) {
            PerfWindow pw = this.perfWindows.get(window);
            pw.pushStat(statistic);
        }
    }

    private void updateStatus() {
        addToPerfWindows(getJri().getStat());
    }

    public void printStatus() {
        System.out.print(ReportingConf.CLEAR_CONSOLE);
        System.out.println(ReportingConf.ANSI_YELLOW + "=============================" + ReportingConf.ANSI_CYAN);
        for (Long window : windows) {
            PerfWindow pw = this.perfWindows.get(window);
            System.out.println("Window Length: " + window + "\t" + pw.toString());
        }
        getJri().printDelays();
        System.out.println(ReportingConf.ANSI_BLUE + "===========================");
        System.out.println(ReportingConf.ANSI_YELLOW + "Running for: " + (System.currentTimeMillis() -
                getJri().getStart().getTime()) + "ms\tStarted: " + dtf.format(getJri().getStart()) +
                "\tCount: " + getJri().getCount() + "\tSize: " + getJri().getSize().get() + ReportingConf.ANSI_RESET);
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        setUpWindows();
        try {
            while (true) {
                Thread.sleep(JDBCPerfTest.STATUS_INTERVAL);
                updateStatus();
            }
        } catch (InterruptedException ire) {
            // Done
        }
    }
}
