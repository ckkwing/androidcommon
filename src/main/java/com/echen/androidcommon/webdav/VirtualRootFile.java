package com.echen.androidcommon.webdav;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.echen.androidcommon.utility.FileUtility;
import com.echen.androidcommon.utility.Utility;

/**
 * Created by echen on 1/9/2019
 */
public class VirtualRootFile extends WebDavFile {
    private Context mContext;
    private ArrayList<File> mChildren = null;

    public VirtualRootFile(@NonNull String pathname) {
        super(pathname);
    }

    public VirtualRootFile(@NonNull String pathname, Context context) {
        this(pathname);
        mContext = context;
    }

    public void resetChildren(){
        if (null != mChildren){
            mChildren.clear();
            mChildren = null;
        }
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @NonNull
    @Override
    public String getName() {
        String modelName = Utility.getSystemModel();
        if (!Utility.isNull(modelName))
            return modelName;
        return super.getName();
    }

    @Override
    public File[] listFiles() {
        if (Utility.isNull(mChildren))
        {
            mChildren = new ArrayList<>();
            File internalStorage = FileUtility.getInternalStorageFile();
            if (!Utility.isNull(internalStorage)) {
                WebDavFile extendedInternalFile = new WebDavFile(this, "/Internal Storage", internalStorage.getAbsolutePath());
                mChildren.add(extendedInternalFile);
            }

            File externalStorage = FileUtility.getSDCardFile(mContext);
            if (!Utility.isNull(externalStorage)) {
                WebDavFile extendedExternalFile = new WebDavFile(this, "/SD Card(read-only)", externalStorage.getAbsolutePath());
                mChildren.add(extendedExternalFile);
            }
        }

        File[] files = new File[mChildren.size()];
        return mChildren.toArray(files);
    }

    @Override
    public WebDavFile[] listWebDavFiles() {
        File[] list = listFiles();
        WebDavFile[] webDavFiles = new WebDavFile[list.length];
        for (int i =0; i< list.length; i++){
            webDavFiles[i] = (WebDavFile)list[i];
        }
        return webDavFiles;
    }

    public WebDavFile findRealFileByDisplayUrlStarted(String url) {
        if (TextUtils.isEmpty(url) || null == mChildren)
            return null;
        for (File child : mChildren) {
            WebDavFile concreteRootFile = (WebDavFile) child;
            if (!concreteRootFile.exists())
                continue;
            if (url.startsWith("/" + concreteRootFile.getName()))
                return concreteRootFile;
//            if (url.startsWith(concreteRootFile.getAbsolutePath()))
//                return concreteRootFile;
        }

        return null;
    }

//    @Override
//    public File[] listFiles() {
//        if (Utility.isNull(mChildren))
//        {
//            mChildren = new ArrayList<>();
//            File internalStorage = FileUtility.getInternalStorageFile();
//            if (!Utility.isNull(internalStorage)) {
//                ConcreteRootFile extendedInternalFile = new ConcreteRootFile(internalStorage.getAbsolutePath(), "Internal Storage");
//                mChildren.add(extendedInternalFile);
//            }
//
//            File externalStorage = FileUtility.getSDCardFile(mContext);
//            if (!Utility.isNull(externalStorage)) {
//                ConcreteRootFile extendedExternalFile = new ConcreteRootFile(externalStorage.getAbsolutePath(), "external Storage");
//                mChildren.add(extendedExternalFile);
//            }
//        }
//
//        File[] files = new File[mChildren.size()];
//        return mChildren.toArray(files);
//    }

//    public ConcreteRootFile findRealFileByDisplayUrl(String url){
//        if (TextUtils.isEmpty(url))
//            return null;
//        for(File child : mChildren){
//            ConcreteRootFile concreteRootFile = (ConcreteRootFile)child;
//            if (!concreteRootFile.exists())
//                continue;
//            if (url.equalsIgnoreCase(concreteRootFile.getDisplayPath()))
//                return concreteRootFile;
//            if (url.equalsIgnoreCase(concreteRootFile.getAbsolutePath()))
//                return concreteRootFile;
//        }
//
//        return null;
//    }
//
//    public ConcreteRootFile findRealFileByDisplayUrlStarted(String url){
//        if (TextUtils.isEmpty(url))
//            return null;
//        for(File child : mChildren){
//            ConcreteRootFile concreteRootFile = (ConcreteRootFile)child;
//            if (!concreteRootFile.exists())
//                continue;
//            if (url.startsWith(concreteRootFile.getDisplayPath()))
//                return concreteRootFile;
//            if (url.startsWith(concreteRootFile.getAbsolutePath()))
//                return concreteRootFile;
//        }
//
//        return null;
//    }
}
