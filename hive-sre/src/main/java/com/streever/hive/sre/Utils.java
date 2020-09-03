package com.streever.hive.sre;

import org.apache.commons.lang.StringUtils;

public class Utils {

    public static String dirToPartitionSpec(String directoryPart) {
        String[] directories = directoryPart.split("\\/");
        String[] partitionSpecs = new String[directories.length];
        int loc = 0;
        for (String directory: directories) {
            String[] specParts = directory.split("=");
            partitionSpecs[loc++] = specParts[0] + "=\"" + specParts[1] + "\"";
        }
        StringBuilder rtn = new StringBuilder();
        rtn.append(StringUtils.join(partitionSpecs, ","));
        return rtn.toString();
    }
}
