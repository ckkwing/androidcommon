package com.echen.androidcommon.webdav;

import java.io.File;

/**
 * Created by echen on 1/25/2019
 */
public class VirtualEmptyFile extends VirtualRootFile{
    public VirtualEmptyFile(String pathname) {
        super(pathname);
    }

    @Override
    public File[] listFiles() {
        return null;
    }

    @Override
    public WebDavFile[] listWebDavFiles() {
        return null;
    }
}
