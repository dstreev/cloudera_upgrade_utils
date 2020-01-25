package com.streever.hive.reporting;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.streever.hive.reporting.ReportCounter.*;

public class Reporter implements Runnable {

    public static final String clearConsole = "\33[H\33[2J";
    public static final String resetToPreviousLine = "\33[1A\33[2K";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final int ANSI_SIZE = 5;
    private int WIDTH = 100;
    private int linePos = 0;

    private String name;
    private Map<String, ReportCounter> counters = new HashMap<String, ReportCounter>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCounter(String name, ReportCounter counter) {
        counters.put(name, counter);
    }

    private Wrap getCounterDisplay(ReportCounter counter) {
        List<String> displayLines = new LinkedList<String>();
        long total = counter.getTotalCount();
        long processed = counter.getProcessed();
        double percentProcessed = (double) processed / (double) total;
        int percentProcessWidth = (int) (percentProcessed * WIDTH);

        displayLines.add(ANSI_YELLOW + counter.getName() + " : " + counter.getStatusStr() + ANSI_RESET);
        String processedStr = StringUtils.rightPad("|", percentProcessWidth, "=");
        String remainingStr = StringUtils.leftPad("|", WIDTH - percentProcessWidth, ".");
        displayLines.add(ANSI_GREEN + processedStr + ANSI_RED + remainingStr + ANSI_RESET);
        displayLines.add(ANSI_BLUE + "[" + ANSI_GREEN + counter.getSuccess() + ANSI_BLUE +
                "/" + ANSI_RED + counter.getError() + ANSI_BLUE + "/" +
                ANSI_GREEN + total + ANSI_BLUE + "]" + ANSI_RESET);

        for (ReportCounter child : counter.getChildren()) {
            displayLines.addAll(Arrays.asList(getShortCounterDisplay(child)));
        }
        Wrap rtn = new Wrap();
        rtn.status = counter.getStatus();
        rtn.details = displayLines;
        return rtn;
    }

    private String[] getShortCounterDisplay(ReportCounter counter) {
        String[] cStatusOutput = new String[1];
//        long total = counter.getTotalCount();
        long processed = counter.getProcessed();
        long errors = counter.getError();
        long successes = counter.getSuccess();
        String line = StringUtils.leftPad(counter.getName() + ANSI_YELLOW + " -> " +
                ANSI_BLUE + "[" + ANSI_GREEN + successes + ANSI_BLUE + "/" +
                ANSI_RED + errors + ANSI_BLUE + "/" + ANSI_GREEN + processed +
                ANSI_BLUE + "]" + ANSI_RESET, WIDTH + (9 * ANSI_SIZE), " ");
        cStatusOutput[0] = line;
        return cStatusOutput;
    }

    class Wrap {
        int status;
        List<String> details;
    }

    private void pushLine(String line) {
        linePos += 1;
        System.out.println(line);

    }

    private void resetLines() {
        for (int i = 0; i < linePos; i++) {
            System.out.print(resetToPreviousLine);
        }
        linePos = 0;
    }

    public void refresh() {
        int ERROR = 0;
        int SUCCESS = 1;
        List<String> currentProcessing = new ArrayList<String>();
        resetLines();
        pushLine(StringUtils.leftPad("=", WIDTH, "="));
        pushLine(StringUtils.center(getName(), WIDTH));
        pushLine(StringUtils.leftPad("=", WIDTH, "="));
//        StringBuilder sb = new StringBuilder();
        Map<Integer, AtomicLong> totals = new TreeMap<Integer, AtomicLong>();
        totals.put(ERROR, new AtomicLong(0));
        totals.put(SUCCESS, new AtomicLong(0));
        Map<Integer, AtomicLong> progress = new TreeMap<Integer, AtomicLong>();
        for (String database : counters.keySet()) {
            ReportCounter ctr = counters.get(database);
            if (progress.containsKey(ctr.getStatus())) {
                progress.get(ctr.getStatus()).addAndGet(1);
            } else {
                progress.put(ctr.getStatus(), new AtomicLong(1));
            }
            switch (ctr.getStatus()) {
//                case CONSTRUCTED:
//                    break;
//                case WAITING:
//                    break;
//                case STARTED:
//                    break;
                case PROCESSING:
                    Wrap cd = getCounterDisplay(ctr);
                    currentProcessing.addAll(cd.details);
                    break;
//                case ERROR:
//                    break;
//                case COMPLETED:
//                    break;
            }
            for (ReportCounter child: ctr.getChildren()) {
                totals.get(ERROR).addAndGet(child.getError());
                totals.get(SUCCESS).addAndGet(child.getSuccess());
            }
        }
        for (String line : currentProcessing) {
            pushLine(line);
        }
        StringBuilder overallStatusSb = new StringBuilder();
        overallStatusSb.append(ANSI_BLUE).append("[").append(ANSI_GREEN);
        for (int i = 0; i <= COMPLETED; i++) {
            if (i > 0)
                overallStatusSb.append(ANSI_BLUE).append("/").append(ANSI_GREEN);
            AtomicLong value = progress.get(i);
            if (value != null)
                overallStatusSb.append(value.toString());
            else
                overallStatusSb.append("0");
        }
        overallStatusSb.append(ANSI_BLUE).append("]").append(ANSI_RESET);

        String summary = overallStatusSb.toString();

        StringBuilder overallTotalsSb = new StringBuilder();
        overallTotalsSb.append(ANSI_BLUE).append("[").append(ANSI_GREEN);
        overallTotalsSb.append(totals.get(ERROR).get()).append(ANSI_BLUE).append("/").append(ANSI_GREEN);
        overallTotalsSb.append(totals.get(SUCCESS).get()).append(ANSI_BLUE).append("]").append(ANSI_RESET);
        String totalsSummary = overallTotalsSb.toString();
        totalsSummary = StringUtils.leftPad(totalsSummary, WIDTH - (summary.length() + 1) + (20 * 5), " ");
//        summary = StringUtils.leftPad(summary, WIDTH + (8 * 5), " ");
        pushLine(summary + totalsSummary);
    }

    private boolean completed() {
        boolean completed = true;
        for (String database : counters.keySet()) {
            ReportCounter ctr = counters.get(database);
            if (ctr.getStatus() != COMPLETED) {
                completed = false;
                break;
            }
        }
        return completed;
    }

    @Override
    public void run() {
        while (true) {
            refresh();
            try {
                Thread.sleep(200);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 1 last time.
//        refresh();
    }
}
