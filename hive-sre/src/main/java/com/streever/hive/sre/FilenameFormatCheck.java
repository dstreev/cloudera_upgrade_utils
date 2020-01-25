package com.streever.hive.sre;

import com.streever.hadoop.shell.command.CommandReturn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FilenameFormatCheck extends AbstractCommandReturnCheck {

    @Override
    public String getCommand() {
        return "lsp -R -F \"([0-9]+_[0-9]+)|([0-9]+_[0-9]+_copy_[0-9]+)\" -i -Fe file -v -f parent,file ";
    }

    @Override
    public String getCorrectiveActionCommand() {
        return "# TODO: mv %1 %2";
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
    public void onError(CommandReturn commandReturn) {

    }

    @Override
    public void onSuccess(CommandReturn commandReturn) {
        for (List<String> record: commandReturn.getRecords()) {
            String[] rec = new String[record.size()];
            record.toArray(rec);
            String fullRecord = StringUtils.join(rec, " ");
            error.println(fullRecord);
//            success.println(fullRecord);
        }
    }

}
