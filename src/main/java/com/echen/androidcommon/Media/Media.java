package com.echen.androidcommon.Media;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.echen.androidcommon.FileSystem.File;

/**
 * Created by echen on 2016/5/27.
 */
public abstract class Media extends File {

    protected MediaCenter.MediaType mediaType;
    public MediaCenter.MediaType getMediaType() {
        return mediaType;
    }

    public Media(){}

    public Media(int id, String title, String displayName, String mimeType,
                 String path, long size)
    {
        super(id, title, displayName, mimeType, path, size);
    }

    public abstract Bitmap getThumbnail(Context context);
    public abstract Uri getThumbnailUrl(Context context);
}
