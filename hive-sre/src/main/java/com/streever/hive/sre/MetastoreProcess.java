package com.streever.hive.sre;

import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.FileNotFoundException;
import java.util.List;

import static com.streever.hive.reporting.ReportCounter.*;

public abstract class MetastoreProcess extends SreProcessBase implements Counter {

    private ReportCounter counter = new ReportCounter();

    @Override
    public void init(ProcessContainer parent) throws FileNotFoundException {
        super.init(parent);
        setStatus(WAITING);
    }

    @Override
    public void setDisplayName(String displayName) {
        super.setDisplayName(displayName);
        this.getCounter().setName(getUniqueName());
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

    @Override
    public String toString() {
        return "MetastoreProcess{}";
    }
}
