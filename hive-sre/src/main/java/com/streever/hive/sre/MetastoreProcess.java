package com.streever.hive.sre;

import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.FileNotFoundException;
import java.util.List;

import static com.streever.hive.reporting.ReportCounter.*;

public abstract class MetastoreProcess extends SreProcessBase implements Counter, Runnable {

    private ReportCounter counter = new ReportCounter();

    @Override
    public void init(ProcessContainer parent, String outputDirectory) throws FileNotFoundException {
        super.init(parent, outputDirectory);
        setParent(parent);
        if (outputDirectory == null) {
            throw new RuntimeException("Config File and Output Directory must be set before init.");
        }
        setOutputDirectory(outputDirectory);
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

//    @Override
//    public long getProcessed() {
//        return counter.getProcessed();
//    }
//
//    @Override
//    public void setProcessed(long processed) {
//        counter.setProcessed(processed);
//    }
//
//    @Override
//    public long getTotalCount() {
//        return counter.getTotalCount();
//    }

    @Override
    public void setTotalCount(long totalCount) {
        counter.setTotalCount(totalCount);
    }

    @Override
    public void incSuccess(int increment) {
        counter.incSuccess(increment);
    }

//    @Override
//    public long getSuccessStream() {
//        return counter.getSuccess();
//    }
//
//    @Override
//    public void setSuccessStream(long successStream) {
//        counter.setSuccess(successStream);
//    }

    @Override
    public void incError(int increment) {
        counter.incError(increment);
    }

//    @Override
//    public long getErrorStream() {
//        return counter.getError();
//    }
//
//    @Override
//    public void setErrorStream(long errorStream) {
//        counter.setError(errorStream);
//    }

    @Override
    public String toString() {
        return "MetastoreProcess{}";
    }
}
