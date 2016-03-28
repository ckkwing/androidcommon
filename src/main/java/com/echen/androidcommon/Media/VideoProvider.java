package com.echen.androidcommon.Media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/29.
 */
public class VideoProvider implements IMediaProvider {
    private Context context = null;

    public VideoProvider(Context context){
        this.context = context;
    }

    @Override
    public List<?> getList() {
        List<Video> list = new ArrayList<Video>();
        if (null == context)
            return list;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null);
        if (null == cursor)
            return  list;
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String album = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
            String artist = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            Video video = new Video(id, title, album, artist, displayName, mimeType, path, size, duration);
            list.add(video);
        }
        cursor.close();
        return list;
    }
}
