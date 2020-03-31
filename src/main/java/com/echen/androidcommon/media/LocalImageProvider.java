package com.echen.androidcommon.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/27.
 */
public class LocalImageProvider implements IMediaProvider<Image> {
    private Context context = null;
    private String cacheThumbnailPath = "";

    public LocalImageProvider(Context context, String cacheThumbnailPath) {
        this.context = context;
        this.cacheThumbnailPath = cacheThumbnailPath;
    }

    @Override
    public List<Image> getList() {
        List<Image> list = new ArrayList<>();
        if (null == context)
            return list;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (null == cursor)
            return list;
        while (cursor.moveToNext()) {
            int id = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            String dateAdded = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            String dateModified = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
            Image image = new Image(id, title, displayName, mimeType,
                    path, size, dateAdded, dateModified);
//            Uri uri = Uri.parse(path);
            Uri thumbnailUri = image.tryToGetThumbnailUri(context, cacheThumbnailPath);
            if (null == thumbnailUri)
                thumbnailUri = Uri.fromFile(new File(path));
            image.setThumbnailUri(thumbnailUri);
            list.add(image);
        }
        cursor.close();
        return list;
    }


//
//    @Override
//    public List<?> getList() {
//        List<Image> list = new ArrayList<Image>();
//        if (null == context)
//            return list;
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null,null,null,null);
//        if (null == cursor)
//            return  list;
//        while (cursor.moveToNext())
//        {
//            int id = cursor
//                    .getInt(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//            String title = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
//            String path = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//            String displayName = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
//            String mimeType = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
//            long size = cursor
//                    .getLong(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
//            String dateAdded = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
//            String dateModified = cursor
//                    .getString(cursor
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
//            Image image = new Image(id, title, displayName, mimeType,
//                    path, size, dateAdded, dateModified);
////            Uri uri = Uri.parse(path);
//            Uri thumbnailUri = image.tryToGetThumbnailUri(context, cacheThumbnailPath);
//            image.setThumbnailUri(thumbnailUri);
//
//            list.add(image);
//        }
//        cursor.close();
//        return list;
//    }
}
