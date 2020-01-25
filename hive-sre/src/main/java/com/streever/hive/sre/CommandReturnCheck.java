package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.PrintStream;
import java.util.List;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = PathCheck.class, name = "path.check"),
//        @JsonSubTypes.Type(value = SmallFiles.class, name = "small.files"),
//        @JsonSubTypes.Type(value = FilenameFormatCheck.class, name = "filename.format"),
//        @JsonSubTypes.Type(value = DirectoryExistsCheck.class, name = "directory.exists")
//})
@JsonIgnoreProperties({"counter"})
public class CommandReturnCheck implements Counter, Cloneable {

    private String name;
    private String pathCommand;
    // Determine if the on..Commands should be run against the "path" or
    //      each record in the CommandReturn.
    private Boolean reportOnResults = Boolean.FALSE;
    private Boolean reportOnPath = Boolean.TRUE;
    private Boolean processOnError = Boolean.TRUE;
    private Boolean processOnSuccess = Boolean.TRUE;
    private String onSuccessCommand;
    private String onErrorCommand;

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
        if (getReportOnPath()) {
            String action = String.format(getOnErrorCommand(), commandReturn.getPath());
            error.println(action);
        }
        // TODO: Report on Record

    }

    public void onSuccess(CommandReturn commandReturn) {
        // TODO: Report on Path

        // TODO: Report on Record

    }

    public String getPathCommand() {
        return pathCommand;
    }

    public void setPathCommand(String pathCommand) {
        this.pathCommand = pathCommand;
    }

    public String getOnSuccessCommand() {
        return onSuccessCommand;
    }

    public void setOnSuccessCommand(String onSuccessCommand) {
        this.onSuccessCommand = onSuccessCommand;
    }

    public String getOnErrorCommand() {
        return onErrorCommand;
    }

    public void setOnErrorCommand(String onErrorCommand) {
        this.onErrorCommand = onErrorCommand;
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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
//        counter = new ReportCounter();
//        counter.setName(name);
    }

    public String getFullCommand(String[] args) {
        StringBuilder sb = new StringBuilder(getPathCommand());
        for (int i = 0; i < args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        CommandReturnCheck clone = (CommandReturnCheck)super.clone();
        clone.setName(this.name);
        clone.setOnErrorCommand(this.onErrorCommand);
        clone.setOnSuccessCommand(this.onSuccessCommand);
        clone.setReportOnPath(this.reportOnPath);
        clone.setReportOnResults(this.reportOnResults);
        clone.setProcessOnError(this.processOnError);
        clone.setProcessOnSuccess(this.processOnSuccess);
        clone.setCounter(new ReportCounter());
        clone.getCounter().setName(this.name);
        return clone;

    }

}
