package com.echen.androidcommon.Media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by echen on 2015/1/27.
 */
public class Video extends Media {

    private String album;
    private String artist;
    private long duration;
    private String[] projection = new String[]{
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID,
    };

    public static Uri getVideoThumbnailContentUri()
    {
        return MediaStore.Video.Thumbnails.getContentUri("external");
    }

    private String[] projection_thumbnail = new String[] {

            MediaStore.Video.Thumbnails._ID, // 0
            MediaStore.Video.Thumbnails.DATA, // 1 from android.provider.MediaStore.Video
            MediaStore.Video.Thumbnails.VIDEO_ID,
            MediaStore.Video.Thumbnails.KIND,
            MediaStore.Video.Thumbnails.WIDTH,
            MediaStore.Video.Thumbnails.HEIGHT
    };

    private String selection_thumbnail = MediaStore.Video.Thumbnails.VIDEO_ID+ "=?";

    /**
     *
     */
    public Video() {
        super();
        this.mediaType = MediaCenter.MediaType.Video;
    }

    /**
     * @param id
     * @param title
     * @param album
     * @param artist
     * @param displayName
     * @param mimeType
     * @param data
     * @param size
     * @param duration
     */
    public Video(int id, String title, String album, String artist,
                 String displayName, String mimeType, String path, long size,
                 long duration) {
        super(id, title, displayName, mimeType, path, size);
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.mediaType = MediaCenter.MediaType.Video;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public Bitmap getThumbnail(Context context) {
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                context.getContentResolver(), this.id,
                MediaStore.Video.Thumbnails.MINI_KIND,
                null);
        return bitmap;
    }

    @Override
    public Uri getThumbnailUrl(Context context) {
        Uri uri = null;
        Uri videoUri = getVideoThumbnailContentUri();
        Cursor cursor = context.getContentResolver().query(videoUri, projection_thumbnail, selection_thumbnail, new String[] {String.valueOf(id)},null);

        if ((null != cursor) && cursor.moveToFirst())
        {
            String id = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Thumbnails._ID));
            String thumbnailPath = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
            String video_id = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Thumbnails.VIDEO_ID));

            if (!thumbnailPath.isEmpty())
                uri = Uri.fromFile(new File(thumbnailPath));
            cursor.close();
        }
        return uri;
    }
}
