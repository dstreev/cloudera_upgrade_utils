package com.streever.hive.sre;

import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.PrintStream;
import java.util.List;

public abstract class SRERunnable implements Counter, Runnable {

    private String displayName;
    private ReportCounter counter = new ReportCounter();

    /**
     * allows stdout to be captured if necessary
     */
    public PrintStream success = System.out;
    /**
     * allows stderr to be captured if necessary
     */
    public PrintStream error = System.err;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        counter.setName(displayName);
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
    public void setTotalCount(long totalCount) {
        counter.setTotalCount(totalCount);
    }

    @Override
    public void incSuccess(int increment) {
        counter.incSuccess(increment);
    }

    @Override
    public void incError(int increment) {
        counter.incError(increment);
    }

    public abstract Boolean init();

}
