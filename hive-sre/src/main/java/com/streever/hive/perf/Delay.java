package com.streever.hive.perf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Delay {
    private static final DateFormat dtf;

    static {
        dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    private Date timestamp;
    private Long delay;
    private Long marker;

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Long getMarker() {
        return marker;
    }

    public void setMarker(Long marker) {
        this.marker = marker;
    }

    public Delay(Long delay, Long marker) {
        this.timestamp = new Date();
        this.delay = delay;
        this.marker = marker;
    }

    @Override
    public String toString() {
        return  " -> At " + dtf.format(timestamp) + " there was a delay of " + delay +
                " after " + marker + " records.";
    }
}
