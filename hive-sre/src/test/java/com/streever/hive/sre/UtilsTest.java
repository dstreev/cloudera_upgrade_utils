package com.streever.hive.sre;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void dirToPartitionSpec_001() {
        String[] testSet = {"st=GA A/update_dt=2020-09-03"};

        for (String test: testSet) {
            String spec = Utils.dirToPartitionSpec(test);
            System.out.println(spec);
        }
    }
}