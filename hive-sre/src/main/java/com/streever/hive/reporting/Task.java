package com.streever.hive.reporting;

public interface Task {

    void setState(TaskState state);
    TaskState getState();

}
