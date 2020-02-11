package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.PrintStream;
import java.util.*;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = PathCheck.class, name = "path.check"),
//        @JsonSubTypes.Type(value = SmallFiles.class, name = "small.files"),
//        @JsonSubTypes.Type(value = FilenameFormatCheck.class, name = "filename.format"),
//        @JsonSubTypes.Type(value = DirectoryExistsCheck.class, name = "directory.exists")
//})
@JsonIgnoreProperties({"counter", "properties", "calculationResults", "scriptEngine"})
public class CommandReturnCheck implements Counter, Cloneable {

    private String name;
    private String pathCommand;
    // Most commands that run will not be an error, but are issues that need to
    // be put into the 'error' or action bucket.  Use this to control that direction.
    private Boolean invertCheck = true;
    // Determine if the on..Commands should be run against the "path" or
    //      each record in the CommandReturn.
    private Boolean reportOnResults = Boolean.FALSE;
    private Boolean reportOnPath = Boolean.TRUE;
    private Boolean processOnError = Boolean.TRUE;
    private Boolean processOnSuccess = Boolean.TRUE;
    private String onSuccessRecordCommand;
    private String onErrorRecordCommand;
    private String onSuccessPathCommand;
    private String onErrorPathCommand;
    private Map<String, Map<CheckSearch, CheckCalculation>> checkCalculations = null;
    private ScriptEngine scriptEngine = null;
    private Map<String, Object> calculationResults = null;
    private String[] currentArgs;
    /**
     * allows stdout to be captured if necessary
     */
    public PrintStream success = System.out;
    /**
     * allows stderr to be captured if necessary
     */
    public PrintStream error = System.err;

    public ReportCounter counter = new ReportCounter();

    public void onError(CommandReturn commandReturn) {
        if (!invertCheck) {
            internalOnError(commandReturn);
        } else {
            internalOnSuccess(commandReturn);
        }
    }

    public void onSuccess(CommandReturn commandReturn) {
        if (!invertCheck) {
            internalOnSuccess(commandReturn);
        } else {
            internalOnError(commandReturn);
        }
    }

    public String runCalculations(CommandReturn commandReturn) {
        String rtn = null;
        try {
            if (getCheckCalculations() != null && getCheckCalculations().size() > 0 && getScriptEngine() != null) {
                StringBuilder sb = new StringBuilder();
                for (String calcKey : checkCalculations.keySet()) {
                    Map<CheckSearch, CheckCalculation> checkSearchCalculation = checkCalculations.get(calcKey);
                    for (CheckSearch checkSearch : checkSearchCalculation.keySet()) {
                        CheckCalculation checkCalculation = checkSearchCalculation.get(checkSearch);
                        String testStr = null;
                        String failStr = null;
                        String passStr = null;
                        switch (checkSearch) {
                            case PATH:
                                if (checkCalculation.getTest() != null) {
                                    // Params
                                    List combined = new LinkedList(Arrays.asList(getCurrentArgs()));
                                    // Configured Params
                                    if (checkCalculation.getParams() != null)
                                        combined.addAll(Arrays.asList(checkCalculation.getParams()));
                                    try {
                                        testStr = String.format(checkCalculation.getTest(), combined.toArray());
                                        Boolean checkTest = null;
                                        checkTest = (Boolean) scriptEngine.eval(testStr);
                                        if (checkTest) {
                                            if (checkCalculation.getPass() != null) {
                                                passStr = String.format(checkCalculation.getPass(), combined.toArray());
                                                String passResult = (String) scriptEngine.eval(passStr);
                                                sb.append(passResult).append("\n");
                                            }

                                        } else {
                                            if (checkCalculation.getFail() != null) {
                                                failStr = String.format(checkCalculation.getFail(), combined.toArray());
                                                String failResult = (String) scriptEngine.eval(failStr);
                                                sb.append(failResult).append("\n");
                                            }
                                        }
                                    } catch (ScriptException e) {
                                        e.printStackTrace();
                                    } catch (MissingFormatArgumentException mfa) {
                                        mfa.printStackTrace();
                                        System.err.println("Bad Argument Match up for PATH check rule: " + this.getName() + ":" + calcKey);
                                    }
                                }
                                break;
                            case RECORDS:
                                // Loop Through Records.
                                if (checkCalculation.getTest() != null) {
                                    for (List<Object> record : commandReturn.getRecords()) {
                                        // Params
                                        List combined = new LinkedList(Arrays.asList(getCurrentArgs()));
                                        // Current Record
                                        combined.addAll(record);
                                        // Configured Params
                                        if (checkCalculation.getParams() != null)
                                            combined.addAll(Arrays.asList(checkCalculation.getParams()));
                                        try {
                                            testStr = String.format(checkCalculation.getTest(), combined.toArray());
                                            Boolean checkTest = null;
                                            checkTest = (Boolean) scriptEngine.eval(testStr);
                                            if (checkTest) {
                                                if (checkCalculation.getPass() != null) {
                                                    passStr = String.format(checkCalculation.getPass(), combined.toArray());
                                                    String passResult = (String) scriptEngine.eval(passStr);
                                                    sb.append(passResult).append("\n");
                                                }

                                            } else {
                                                if (checkCalculation.getFail() != null) {
                                                    failStr = String.format(checkCalculation.getFail(), combined.toArray());
                                                    String failResult = (String) scriptEngine.eval(failStr);
                                                    sb.append(failResult).append("\n");
                                                }
                                            }
                                        } catch (ScriptException e) {
                                            e.printStackTrace();
                                        } catch (MissingFormatArgumentException mfa) {
                                            mfa.printStackTrace();
                                            System.err.println("Bad Argument Match up for RECORDS check rule: " + this.getName() + ":" + calcKey);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
                rtn = sb.toString();
            }
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        return rtn;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    private void internalOnError(CommandReturn commandReturn) {
        StringBuilder sb = new StringBuilder();
        if (getReportOnPath() && getOnErrorPathCommand() != null) {
            String action = null;
            try {
                action = String.format(getOnErrorPathCommand(), getCurrentArgs());
            } catch (Throwable t) {
                throw new RuntimeException("Bad string format in 'errorPath' action command of CommandReturnCheck", t);
            }
            if (action != null)
                sb.append(action).append("\n");
        }

        if (getReportOnResults() && getOnErrorRecordCommand() != null) {
            for (List<Object> record : commandReturn.getRecords()) {
                String action = null;
                try {
                    action = String.format(getOnErrorRecordCommand(), record.toArray());
                } catch (Throwable t) {
                    throw new RuntimeException("Bad string format in 'errorRecord' action command of CommandReturnCheck", t);
                }
                if (action != null)
                    sb.append(action).append("\n");
            }
        }
        if (getProcessOnError()) {
            String checkCalcs = runCalculations(commandReturn);
            if (checkCalcs != null)
                sb.append(checkCalcs);
        }
        error.print(sb.toString());
    }

    private void internalOnSuccess(CommandReturn commandReturn) {
        StringBuilder sb = new StringBuilder();
        if (getReportOnPath() && getOnSuccessPathCommand() != null) {
            String action = null;
            try {
                action = String.format(getOnSuccessPathCommand(), getCurrentArgs());
            } catch (Throwable t) {
                throw new RuntimeException("Bad string format in 'successPath' action command of CommandReturnCheck", t);
            }
            if (action != null)
                sb.append(action).append("\n");
        }
        if (getReportOnResults() && getOnSuccessRecordCommand() != null) {
            for (List<Object> record : commandReturn.getRecords()) {
                Object[] rec = new Object[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    rec[i] = record.get(i);
                }
                String action = null;
                try {
                    action = String.format(getOnSuccessRecordCommand(), rec);
                } catch (Throwable t) {
                    throw new RuntimeException("Bad string format in 'successRecord' action command of CommandReturnCheck", t);
                }
                if (action != null)
                    sb.append(action).append("\n");
            }
        }
        if (getProcessOnSuccess()) {
            String checkCalcs = runCalculations(commandReturn);
            if (checkCalcs != null)
                sb.append(checkCalcs);
        }
        success.print(sb.toString());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInvertCheck() {
        return invertCheck;
    }

    public void setInvertCheck(Boolean invertCheck) {
        this.invertCheck = invertCheck;
    }

    public String getPathCommand() {
        return pathCommand;
    }

    public void setPathCommand(String pathCommand) {
        this.pathCommand = pathCommand;
    }

    public String getOnSuccessRecordCommand() {
        return onSuccessRecordCommand;
    }

    public void setOnSuccessRecordCommand(String onSuccessRecordCommand) {
        this.onSuccessRecordCommand = onSuccessRecordCommand;
    }

    public String getOnErrorRecordCommand() {
        return onErrorRecordCommand;
    }

    public void setOnErrorRecordCommand(String onErrorRecordCommand) {
        this.onErrorRecordCommand = onErrorRecordCommand;
    }

    public Boolean getReportOnResults() {
        return reportOnResults;
    }

    public void setReportOnResults(Boolean reportOnResults) {
        this.reportOnResults = reportOnResults;
    }

    public Boolean getReportOnPath() {
        return reportOnPath;
    }

    public void setReportOnPath(Boolean reportOnPath) {
        this.reportOnPath = reportOnPath;
    }

    public Boolean getProcessOnError() {
        return processOnError;
    }

    public void setProcessOnError(Boolean processOnError) {
        this.processOnError = processOnError;
    }

    public Boolean getProcessOnSuccess() {
        return processOnSuccess;
    }

    public void setProcessOnSuccess(Boolean processOnSuccess) {
        this.processOnSuccess = processOnSuccess;
    }

    public String getOnSuccessPathCommand() {
        return onSuccessPathCommand;
    }

    public void setOnSuccessPathCommand(String onSuccessPathCommand) {
        this.onSuccessPathCommand = onSuccessPathCommand;
    }

    public String getOnErrorPathCommand() {
        return onErrorPathCommand;
    }

    public void setOnErrorPathCommand(String onErrorPathCommand) {
        this.onErrorPathCommand = onErrorPathCommand;
    }

    public Map<String, Map<CheckSearch, CheckCalculation>> getCheckCalculations() {
        return checkCalculations;
    }

    public void setCheckCalculations(Map<String, Map<CheckSearch, CheckCalculation>> checkCalculations) {
        if (checkCalculations != null && checkCalculations.size() > 0) {
            ScriptEngineManager sem = new ScriptEngineManager();
            scriptEngine = sem.getEngineByName("nashorn");
        }
        this.checkCalculations = checkCalculations;
    }

    public String getFullCommand(String[] args) {
        setCurrentArgs(args);
        String action = String.format(getPathCommand(), getCurrentArgs());
//        StringBuilder sb = new StringBuilder(getPathCommand());
//        for (int i = 0; i < args.length; i++) {
//            sb.append(" ").append(args[i]);
//        }
//        return sb.toString();
        return action;
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
    public long getProcessed() {
        return counter.getProcessed();
    }

    @Override
    public void setProcessed(long processed) {
        counter.setProcessed(processed);
    }

    @Override
    public long getTotalCount() {
        return counter.getTotalCount();
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
    public long getSuccess() {
        return counter.getSuccess();
    }

    @Override
    public void setSuccess(long success) {
        counter.setSuccess(success);
    }

    @Override
    public void incError(int increment) {
        counter.incError(increment);
    }

    @Override
    public long getError() {
        return counter.getError();
    }

    @Override
    public void setError(long error) {
        counter.setError(error);
    }

    public String[] getCurrentArgs() {
        return currentArgs;
    }

    public void setCurrentArgs(String[] currentArgs) {
        this.currentArgs = new String[currentArgs.length];
        for (int i = 0; i < currentArgs.length; i++) {
//            if (currentArgs[i] != null && currentArgs[i].contains(" ")) {
//                this.currentArgs[i] = "\"" + currentArgs[i] + "\"";
//            } else {
            this.currentArgs[i] = currentArgs[i];
//            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        CommandReturnCheck clone = (CommandReturnCheck) super.clone();
        clone.setName(this.name);
        clone.setOnErrorRecordCommand(this.onErrorRecordCommand);
        clone.setOnSuccessRecordCommand(this.onSuccessRecordCommand);
        clone.setReportOnPath(this.reportOnPath);
        clone.setReportOnResults(this.reportOnResults);
        clone.setProcessOnError(this.processOnError);
        clone.setProcessOnSuccess(this.processOnSuccess);
        clone.setCounter(new ReportCounter());
        clone.getCounter().setName(this.name);
        clone.setCheckCalculations(this.checkCalculations);
        return clone;

    }

}
