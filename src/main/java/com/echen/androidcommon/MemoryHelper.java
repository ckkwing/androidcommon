package com.echen.androidcommon;

/**
 * Created by echen on 2016/4/7.
 */
public class MemoryHelper {

    public static int getMaxMemory()
    {
        return (int) (Runtime.getRuntime().maxMemory() / 1024);
    }
}
