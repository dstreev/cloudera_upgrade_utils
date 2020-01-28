package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = PathCheck.class, name = "path.check"),
//        @JsonSubTypes.Type(value = SmallFiles.class, name = "small.files"),
//        @JsonSubTypes.Type(value = FilenameFormatCheck.class, name = "filename.format"),
//        @JsonSubTypes.Type(value = DirectoryExistsCheck.class, name = "directory.exists")
//})
@JsonIgnoreProperties({"counter", "properties"})
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

    private void internalOnError(CommandReturn commandReturn) {
        if (getReportOnPath() && getOnErrorPathCommand() != null) {
            String action = null;
            try {
                action = String.format(getOnErrorPathCommand(), getCurrentArgs());
            } catch (Throwable t) {
                throw new RuntimeException("Bad string format in 'errorPath' action command of CommandReturnCheck", t);
            }
            if (action != null)
            error.println(action);
        }

        if (getReportOnResults() && getOnErrorRecordCommand() != null) {
            for (List<String> record : commandReturn.getRecords()) {
                String action = null;
                try {
                    action = String.format(getOnErrorRecordCommand(), record.toArray());
                } catch (Throwable t) {
                    throw new RuntimeException("Bad string format in 'errorRecord' action command of CommandReturnCheck", t);
                }
                if (action != null)
                    error.println(action);
            }
        }
    }

    private void internalOnSuccess(CommandReturn commandReturn) {
        // TODO: Report on Path
        if (getReportOnPath() && getOnSuccessPathCommand() != null) {
            String action = null;
            try {
                action = String.format(getOnSuccessPathCommand(), getCurrentArgs());
            } catch (Throwable t) {
                throw new RuntimeException("Bad string format in 'successPath' action command of CommandReturnCheck", t);
            }
            if (action != null)
            error.println(action);
        }
        // TODO: Report on Record
        if (getReportOnResults() && getOnSuccessRecordCommand() != null) {
            for (List<String> record : commandReturn.getRecords()) {
                Object[] rec = new Object[record.size()];
                for (int i=0;i<record.size();i++) {
                    rec[i] = record.get(i);
                }
                String action = null;
                try {
                    action = String.format(getOnSuccessRecordCommand(), rec);
                } catch (Throwable t) {
                    throw new RuntimeException("Bad string format in 'successRecord' action command of CommandReturnCheck", t);
                }
                if (action != null)
                    error.println(action);
            }
        }

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
        for (int i = 0; i<currentArgs.length;i++) {
            if (currentArgs[i] != null && currentArgs[i].contains(" ")) {
                this.currentArgs[i] = "\"" + currentArgs[i] + "\"";
            } else {
                this.currentArgs[i] = currentArgs[i];
            }
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
        return clone;

    }

}
