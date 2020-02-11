package com.streever.hive.reporting;

import java.util.List;

public interface Counter {

    String getName();

    ReportCounter getCounter();
    void setCounter(ReportCounter reportCounter);

    int getStatus();

    String getStatusStr();

    List<ReportCounter> getCounterChildren();

    void setStatus(int status);

    void incProcessed(int increment);

    long getProcessed();

    void setProcessed(long processed);

    long getTotalCount();

    void setTotalCount(long totalCount);

    void incSuccess(int increment);

    long getSuccess();

    void setSuccess(long success);

    void incError(int increment);

    long getError();

    void setError(long error);

}
