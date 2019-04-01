package com.echen.androidcommon.filesystem;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by echen on 2015/3/5.
 */
public abstract class File extends FileSystemInfo {

    public File(){
        super();
    }

    public File(int id, String title, String displayName, String mimeType,
                String path, long size, String dateAdded, String dateModified)
    {
        super(id, title, displayName, mimeType, path, size, dateAdded, dateModified);
    }

//    public abstract Bitmap getThumbnail(Context context);

}
