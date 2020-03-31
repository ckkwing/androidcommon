package com.echen.androidcommon.media;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/27.
 */
public class LocalAudioProvider implements IMediaProvider<Audio> {
    private Context context = null;
    private String cacheThumbnailPath = "";

    public LocalAudioProvider(Context context, String cacheThumbnailPath) {
        this.context = context;
        this.cacheThumbnailPath = cacheThumbnailPath;
    }


    @Override
    public List<Audio> getList() {
        List<Audio> list = new ArrayList<Audio>();
        if (null == context)
            return list;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null);
        if (null == cursor)
            return  list;
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String album = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            long albumId =  cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            String artist = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            String dateAdded = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
            String dateModified = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED));
            Audio audio = new Audio(id, title, album, albumId, artist, path,
                    displayName, mimeType, duration, size, dateAdded, dateModified);

            //Uri thumbnailUri = audio.tryToGetThumbnailUri(context, cacheThumbnailPath);
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri imgUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            audio.setThumbnailUri(imgUri);
            list.add(audio);
        }
        cursor.close();
        return list;
    }
}
