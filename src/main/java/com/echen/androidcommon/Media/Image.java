package com.echen.androidcommon.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by echen on 2015/1/27.
 */
public class Image extends Media {

    private String[] projection = new String[]{
            MediaStore.Images.Thumbnails.DATA,
            MediaStore.Images.Thumbnails.KIND
    };

    public Image() {
        super();
        this.mediaType = MediaCenter.MediaType.Image;
    }

    /**
     * @param id
     * @param title
     * @param displayName
     * @param mimeType
     * @param path
     * @param size
     */
    public Image(int id, String title, String displayName, String mimeType,
                 String path, long size, String dateAdded, String dateModified) {
        super(id, title, displayName, mimeType, path, size,dateAdded, dateModified);
        this.mediaType = MediaCenter.MediaType.Image;
    }

    @Override
    public Bitmap getThumbnail(Context context)
    {
        String uri = "";
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                context.getContentResolver(), this.id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            }
        if (null != cursor)
            cursor.close();
        if (uri.isEmpty())
            return null;
        return BitmapFactory.decodeFile(uri);
    }

    @Override
    public Uri tryToGetThumbnailUri(Context context, String cacheThumbnailPath) {
        Uri uri = null;
        try {
            String strUri = "";
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    context.getContentResolver(), this.id,
                    MediaStore.Images.Thumbnails.MICRO_KIND,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                strUri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                if (!strUri.isEmpty()) {
                    uri = Uri.parse(strUri);
                }
            }
            if (null != cursor)
                cursor.close();
        }
        catch (Exception e)
        {
            int i =0;

        }
        return uri;
    }
}