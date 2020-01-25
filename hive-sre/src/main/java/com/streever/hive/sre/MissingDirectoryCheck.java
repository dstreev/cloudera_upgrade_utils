package com.streever.hive.sre;

import com.streever.hadoop.shell.command.CommandReturn;

public class MissingDirectoryCheck extends AbstractCommandReturnCheck {

    @Override
    public String getCommand() {
        return "lsp -f path -t";
    }

    @Override
    public String getCorrectiveActionCommand() {
        return "mkdir -p %s";
    }

    @Override
    public void onError(CommandReturn commandReturn) {
//        error.print("# " + commandReturn.getError());
//        String[] path = new String[1];
//        path[0] = commandReturn.getPath();
        String action = String.format(getCorrectiveActionCommand(), commandReturn.getPath());
        error.println(action);
//        for (List<String> record: commandReturn.getRecords()) {
//            String[] rec = new String[record.size()];
//            record.toArray(rec);
//            String action = String.format(getCorrectiveActionCommand(), rec);
//            error.println(action);
//        }
    }

    @Override
    public void onSuccess(CommandReturn commandReturn) {

    }
}
