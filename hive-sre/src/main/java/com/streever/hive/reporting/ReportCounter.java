package com.streever.hive.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

//@JsonIgnoreProperties({"totalCount", "processed", "success", "error"})
public class ReportCounter {
    public static final int CONSTRUCTED = 0;
    public static final int WAITING = 1;
    public static final int STARTED = 2;
    // Set after we know the scope of the processing. IE: Number of records to review.
    public static final int PROCESSING = 3;
    public static final int ERROR = 4;
    public static final int COMPLETED = 5;

    private int status = 0;

    // The full scope of records to process
    private AtomicLong totalCount = new AtomicLong(0);
    private AtomicLong processed = new AtomicLong(0);
    private  AtomicLong success = new AtomicLong(0);
    private  AtomicLong error = new AtomicLong(0);
    private String name = "unknown";

    private List<ReportCounter> children = new ArrayList<ReportCounter>();

    public List<ReportCounter> getChildren() {
        return children;
    }

    public boolean addChild(ReportCounter counter) {
        return children.add(counter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        String rtn = "unknown";
        switch (status) {
            case CONSTRUCTED:
                rtn = "Constructed";
                break;
            case WAITING:
                rtn = "Waiting";
                break;
            case STARTED:
                rtn = "Started";
                break;
            case PROCESSING:
                rtn = "Processing";
                break;
            case ERROR:
                rtn = "ERROR";
                break;
            case COMPLETED:
                rtn = "Completed";
        }
        return rtn;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void incProcessed(int increment) {
        processed.addAndGet(increment);
//        processed += increment;
    }

    public long getProcessed() {
        return processed.get();
    }

    public void setProcessed(long processed) {

        this.processed.set(processed);
    }

    public long getTotalCount() {
        return totalCount.get();
    }

    public void setTotalCount(long totalCount) {
        this.totalCount.set(totalCount);
    }

    public void incSuccess(int increment) {
        success.addAndGet(increment);
//        success += increment;
    }

    public long getSuccess() {
        return success.get();
    }

    public void setSuccess(long success) {

        this.success.set(success);
    }

    public void incError(int increment) {

        error.addAndGet(increment);
    }

    public long getError() {
        return error.get();
    }

    public void setError(long error) {
        this.error.set(error);
    }

//    public String getCounterStats() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Counter{" +
//                "status=" + status +
//                ", totalCount=" + totalCount +
//                ", processed=" + processed +
//                ", success=" + success +
//                ", error=" + error +
//                '}');
//        for (Counter child : children) {
//            sb.append("\n\t\t" + child.getCounterStats());
//        }
//        return sb.toString();
//    }

    @Override
    public String toString() {
        return "ReportCounter{" +
                "status=" + status +
                ", totalCount=" + totalCount +
                ", processed=" + processed +
                ", success=" + success +
                ", error=" + error +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
