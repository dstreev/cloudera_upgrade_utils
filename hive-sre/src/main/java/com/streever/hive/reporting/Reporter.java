package com.streever.hive.reporting;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.streever.hive.reporting.ReportCounter.*;

public class Reporter implements Runnable {

    //    public static final String clearConsole = "\33[H\33[2J";
//    public static final String resetToPreviousLine = "\33[1A\33[2K";
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";
//    public static final String ANSI_WHITE = "\u001B[37m";
//    private final int ANSI_SIZE = 5;
    private int WIDTH = 100;
    private int linePos = 0;
    private boolean tictoc = false;

    private String name;
    private Date startTime = new Date();

    private Map<String, List<ReportCounter>> counterGroups = new HashMap<String, List<ReportCounter>>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCounter(String groupName, ReportCounter counter) {
        if (counterGroups.containsKey(groupName)) {
            counterGroups.get(groupName).add(counter);
        } else {
            List<ReportCounter> counters = new ArrayList<ReportCounter>();
            counters.add(counter);
            counterGroups.put(groupName, counters);
        }
    }

    private Wrap getCounterDisplay(String prefix, ReportCounter counter) {
        List<String> displayLines = new LinkedList<String>();
        long total = counter.getTotalCount();
        long processed = counter.getProcessed();
        double percentProcessed = (double) processed / (double) total;
        int percentProcessWidth = (int) (percentProcessed * (WIDTH - (prefix.length())));

        displayLines.add(prefix + ReportingConf.ANSI_YELLOW + counter.getName() + " : " + counter.getStatusStr() + ReportingConf.ANSI_RESET);
        String processedStr = StringUtils.rightPad("|", percentProcessWidth, "=");
        String remainingStr = StringUtils.leftPad("|", WIDTH - prefix.length() - percentProcessWidth, ".");
        displayLines.add(prefix + ReportingConf.ANSI_GREEN + processedStr + ReportingConf.ANSI_RED + remainingStr + ReportingConf.ANSI_RESET);
        displayLines.add(prefix + ReportingConf.ANSI_BLUE + "[" + ReportingConf.ANSI_GREEN + counter.getSuccess() + ReportingConf.ANSI_BLUE +
                "/" + ReportingConf.ANSI_RED + counter.getError() + ReportingConf.ANSI_BLUE + "/" +
                ReportingConf.ANSI_GREEN + total + ReportingConf.ANSI_BLUE + "]" + ReportingConf.ANSI_RESET);

        for (ReportCounter child : counter.getChildren()) {
            displayLines.addAll(Arrays.asList(getShortCounterDisplay(prefix, child)));
        }
        Wrap rtn = new Wrap();
        rtn.status = counter.getStatus();
        rtn.details = displayLines;
        return rtn;
    }

    private String[] getShortCounterDisplay(String prefix, ReportCounter counter) {
        String[] cStatusOutput = new String[1];
//        long total = counter.getTotalCount();
        long processed = counter.getProcessed();
        long errors = counter.getError();
        long successes = counter.getSuccess();
        String line = StringUtils.leftPad(prefix + counter.getName() + ReportingConf.ANSI_YELLOW + " -> " +
                ReportingConf.ANSI_BLUE + "[" + ReportingConf.ANSI_GREEN + successes + ReportingConf.ANSI_BLUE + "/" +
                ReportingConf.ANSI_RED + errors + ReportingConf.ANSI_BLUE + "/" + ReportingConf.ANSI_GREEN + processed +
                ReportingConf.ANSI_BLUE + "]" + ReportingConf.ANSI_RESET, WIDTH + (9 * ReportingConf.ANSI_SIZE), " ");
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
//        for (int i = 0; i < linePos; i++) {
//            System.out.print(ReportingConf.RESET_TO_PREVIOUS_LINE);
//        }
        System.out.print(ReportingConf.CLEAR_CONSOLE);
        linePos = 0;
    }

    // Return true when not completed.
    public boolean refresh() {
        int ERROR = 0;
        int SUCCESS = 1;
        boolean rtn = false;
        String INDENT = "    ";

        if (counterGroups.size() == 0)
            return true;
        resetLines();
        String version = ReportingConf.substituteVariables("v.${Implementation-Version}");
        if (tictoc) {
            pushLine(version);
        } else {
            pushLine(version + " *");
        }
        for (String groupName : this.counterGroups.keySet()) {
            List<ReportCounter> counters = counterGroups.get(groupName);

            List<String> currentProcessing = new ArrayList<String>();
            pushLine(StringUtils.rightPad("=", WIDTH, "="));
            pushLine(StringUtils.center(groupName, WIDTH));
            pushLine(StringUtils.rightPad("=", WIDTH, "="));

            Map<Integer, AtomicLong> totals = new TreeMap<Integer, AtomicLong>();
            totals.put(ERROR, new AtomicLong(0));
            totals.put(SUCCESS, new AtomicLong(0));
            Map<Integer, AtomicLong> progress = new TreeMap<Integer, AtomicLong>();
            for (ReportCounter ctr : counters) {
                switch (ctr.getStatus()) {
                    case ReportCounter.CONSTRUCTED:
                    case ReportCounter.WAITING:
                    case ReportCounter.STARTED:
                    case ReportCounter.PROCESSING:
                        if (progress.containsKey(ctr.getStatus())) {
                            progress.get(ctr.getStatus()).addAndGet(1);
                        } else {
                            progress.put(ctr.getStatus(), new AtomicLong(1));
                        }
                        if (ctr.getStatus() == PROCESSING) {
                                Wrap cd = getCounterDisplay(INDENT, ctr);
                                currentProcessing.addAll(cd.details);
                        }
                        for (ReportCounter child : ctr.getChildren()) {
                            totals.get(ERROR).addAndGet(child.getError());
                            totals.get(SUCCESS).addAndGet(child.getSuccess());
                        }
                        break;
                    case ReportCounter.ERROR:
                    case ReportCounter.COMPLETED:
                        break;
                }
            }
            for (String line : currentProcessing) {
                pushLine(line);
            }
            StringBuilder overallStatusSb = new StringBuilder();
            overallStatusSb.append(ReportingConf.ANSI_BLUE).append("[").append(ReportingConf.ANSI_GREEN);
            if (counters.size() > 0) {
                for (int i = 0; i <= COMPLETED; i++) {
                    if (i > 0)
                        overallStatusSb.append(ReportingConf.ANSI_BLUE).append("/").append(ReportingConf.ANSI_GREEN);
                    AtomicLong value = progress.get(i);
                    if (value != null) {
                        overallStatusSb.append(value.toString());
                        // Use this to flag that there are processed that
                        // have not completed.
                        if (i < ReportCounter.ERROR && value.get() > 0) {
                            rtn = true;
                        }
                    } else
                        overallStatusSb.append("0");
                }
            } else {
                rtn = true;
            }
            overallStatusSb.append(ReportingConf.ANSI_BLUE).append("]").append(ReportingConf.ANSI_RESET);

            String summary = overallStatusSb.toString();

            // TODO: Add duration output.

            StringBuilder overallTotalsSb = new StringBuilder();
            overallTotalsSb.append(ReportingConf.ANSI_BLUE).append("[").append(ReportingConf.ANSI_GREEN);
            overallTotalsSb.append(totals.get(ERROR).get()).append(ReportingConf.ANSI_BLUE).append("/").append(ReportingConf.ANSI_GREEN);
            overallTotalsSb.append(totals.get(SUCCESS).get()).append(ReportingConf.ANSI_BLUE).append("]").append(ReportingConf.ANSI_RESET);
            String totalsSummary = overallTotalsSb.toString();
            totalsSummary = StringUtils.leftPad(totalsSummary, WIDTH - (summary.length() + 1) + (20 * 5), " ");
//        summary = StringUtils.leftPad(summary, WIDTH + (8 * 5), " ");
            pushLine(StringUtils.leftPad(" ", WIDTH, "="));
            pushLine(summary + totalsSummary);
        }
        tictoc = !tictoc;
        return rtn;
    }

    @Override
    public void run() {
        while (refresh()) {
            try {
                Thread.sleep(200);
//                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        refresh();
    }

    @Override
    public String toString() {
        return "Reporter{" +
                "name='" + name + '\'' +
                '}';
    }
}
