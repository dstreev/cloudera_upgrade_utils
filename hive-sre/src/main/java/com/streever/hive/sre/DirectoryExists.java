package com.streever.hive.sre;

public class DirectoryExists implements ElementCheck {
    @Override
    public String getCommand() {
        return "test -e";
    }

    @Override
    public String getFullCommand(String[] args) {
        StringBuilder sb = new StringBuilder(getCommand());
        for (int i = 0; i<args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
    }
}
