package com.streever.hive.sre;

import com.streever.hadoop.shell.command.CommandReturn;

public class DirectoryExistsCheck extends AbstractCommandReturnCheck {

    @Override
    public String getCommand() {
        return "lsp -R -F \"([0-9]+_[0-9]+)|([0-9]+_[0-9]_copy_[0-9]+)\" -i -Fe file -v -f parent,file ";
    }

    @Override
    public String getCorrectiveActionCommand() {
        return "mkdir -p %1";
    }

    @Override
    public String getFullCommand(String[] args) {
        StringBuilder sb = new StringBuilder(getCommand());
        for (int i = 0; i<args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
    }

    @Override
    public void onSuccess(CommandReturn commandReturn) {

    }

    @Override
    public void onError(CommandReturn commandReturn) {

    }

    @Override
    public String getName() {
        return "directoryExists";
    }
}
