package com.streever.hive.perf;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PerfWindow {

    private Long windowLength;

    private Deque<Statistic> queue = new ConcurrentLinkedDeque<Statistic>();

    public void pushStat(Statistic stats) {
        queue.add(stats);
        checkQueue(stats.getTimestamp());
    }

    private void checkQueue(Date checkPoint) {
        while (true) {
            Statistic stats = queue.getFirst();
            if (stats.getTimestamp().getTime() < checkPoint.getTime() - windowLength) {
                queue.removeFirst();
            } else {
                break;
            }
        }
    }

    public PerfWindow(Long windowLength) {
        if (windowLength == null) {
            throw new RuntimeException("Need to specify Window Length (milliseconds)");
        }
        this.windowLength = windowLength;
    }

    private Long getAverage() {
        Statistic first = queue.getFirst();
        Statistic last = queue.getLast();
        Long total = last.getRecordCount() - first.getRecordCount();
        return total;
    }

    private Long getPerSec() {
        Statistic first = queue.getFirst();
        Statistic last = queue.getLast();
        Long total = last.getRecordCount() - first.getRecordCount();
        return total / queue.size();
    }

    private Long getSizePerSec() {
        Statistic first = queue.getFirst();
        Statistic last = queue.getLast();
        Long total = last.getSize() - first.getSize();
        return total / queue.size();
    }

    @Override
    public String toString() {
        Long perInterval = getAverage();
        Long perSecond = getPerSec();
        StringBuilder sb = new StringBuilder();
        sb.append("WindowAverage: ").append(getAverage());
        sb.append("\tPer/Sec: ").append(getPerSec());
        sb.append("\tSize/Sec: ").append(getSizePerSec());
        return sb.toString();
    }
}
