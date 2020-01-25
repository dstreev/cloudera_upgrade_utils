package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.streever.hive.reporting.Counter;
import com.streever.hive.reporting.ReportCounter;

import java.io.PrintStream;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MissingDirectoryCheck.class, name = "missing.directory"),
        @JsonSubTypes.Type(value = SmallFiles.class, name = "small.files"),
        @JsonSubTypes.Type(value = FilenameFormatCheck.class, name = "filename.format"),
        @JsonSubTypes.Type(value = DirectoryExistsCheck.class, name = "directory.exists")
})
@JsonIgnoreProperties({"success", "error", "counter"})
public abstract class AbstractCommandReturnCheck implements Counter, CommandReturnCheck, Cloneable {

    private String name;

    /**
     * allows stdout to be captured if necessary
     */
    public PrintStream success = System.out;
    /**
     * allows stderr to be captured if necessary
     */
    public PrintStream error = System.err;

    public ReportCounter counter = new ReportCounter();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
//        counter = new ReportCounter();
//        counter.setName(name);
    }

    @Override
    public String getFullCommand(String[] args) {
        StringBuilder sb = new StringBuilder(getCommand());
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
        clone.setCounter(new ReportCounter());
        clone.getCounter().setName(this.name);
        return clone;

    }

}
