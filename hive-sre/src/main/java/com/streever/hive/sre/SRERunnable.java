package com.streever.hive.sre;

public abstract class SRERunnable implements Runnable {

    public static int CONSTRUCTED = 0;
    public static int INITIALIZED = 1;
    public static int WAITING = 2;
    public static int RUNNING = 3;
    public static int COMPLETED = 4;

    private int status = 0;

}
