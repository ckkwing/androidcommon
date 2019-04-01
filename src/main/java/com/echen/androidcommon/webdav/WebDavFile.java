package com.echen.androidcommon.webdav;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by echen on 1/10/2019
 */
public class WebDavFile extends File {
    public File getRealFile() {
        return mRealFile;
    }

    private File mRealFile;
    private String mRealFilePathName;

    public WebDavFile(String pathname){
        super(pathname);
    }

    public WebDavFile(WebDavFile parent, @NonNull String child) {
        super(parent, child);
    }

    public WebDavFile(WebDavFile parent, @NonNull String child, String realPathname) {
        super(parent, child);
        mRealFilePathName = realPathname;
        if (!TextUtils.isEmpty(realPathname)) {
            mRealFile = new File(realPathname);
        }
    }

    public String getChildRealPath(String childName){
        return new File(this.getRealFile().getAbsolutePath(), childName).getAbsolutePath();
    }


//    public WebDavFile(@NonNull URI uri) {
//        super(uri);
//    }

//    public WebDavFile(@NonNull String pathname, String displayName){
//        super("/" + displayName);
//        this.mDisplayName = displayName;
//        mFile = new File(pathname);
//        if (!mFile.exists())
//            throw new NullPointerException(String.format("%s is not exists", pathname));
//    }

    @Override
    public boolean exists() {
        if (null != mRealFile)
            return mRealFile.exists();
        return super.exists();
    }

    @Override
    public boolean isFile() {
        if (null != mRealFile)
            return mRealFile.isFile();
        return super.isFile();
    }

    @Override
    public boolean isDirectory() {
        if (null != mRealFile)
            return mRealFile.isDirectory();
        return super.isDirectory();
    }

    @Override
    public File[] listFiles() {
        if (null != mRealFile)
            return mRealFile.listFiles();
        return super.listFiles();
    }

    public WebDavFile[] listWebDavFiles(){
        ArrayList<WebDavFile> list = new ArrayList<>();
        if (null != mRealFile)
        {
            for (File file : mRealFile.listFiles()){
                WebDavFile webDavFile = new WebDavFile(this, file.getName(), file.getAbsolutePath());
                list.add(webDavFile);
            }
        }
        WebDavFile[] fileList = new WebDavFile[list.size()];
        return list.toArray(fileList);
    }

    @Override
    public long lastModified() {
        if (null != mRealFile)
            return mRealFile.lastModified();
        return super.lastModified();
    }

    @Override
    public long length() {
        if (null != mRealFile)
            return mRealFile.length();
        return super.length();
    }

    @Override
    public boolean mkdir() {
        if (null != mRealFile)
            return mRealFile.mkdir();
        return super.mkdir();
    }

    public boolean renameTo(WebDavFile dest) {
        if (null != mRealFile)
            return mRealFile.renameTo(dest.getRealFile());

        return false;
    }

    @Override
    public boolean delete() {
        if (null != mRealFile)
            return mRealFile.delete();
        return super.delete();
    }
}
