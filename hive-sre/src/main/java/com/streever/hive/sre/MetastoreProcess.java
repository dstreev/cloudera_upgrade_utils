package com.streever.hive.sre;

import com.streever.hive.reporting.CounterGroup;
import com.streever.hive.reporting.ReportCounter;

import java.io.FileNotFoundException;

public abstract class MetastoreProcess extends SreProcessBase {

    @Override
    public void init(ProcessContainer parent) throws FileNotFoundException {
        super.init(parent);
    }

    @Override
    public String toString() {
        return "MetastoreProcess{}";
    }
}
