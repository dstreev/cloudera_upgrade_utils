package com.streever.hive.sre;

import com.streever.hadoop.shell.command.CommandReturn;
import com.streever.hive.reporting.Counter;

public interface CommandReturnCheck extends Counter {

    void setName(String name);
    String getName();

    String getCommand();

    String getFullCommand(String[] args);

    String getCorrectiveActionCommand();

    void onError(CommandReturn commandReturn);

    void onSuccess(CommandReturn commandReturn);

}
