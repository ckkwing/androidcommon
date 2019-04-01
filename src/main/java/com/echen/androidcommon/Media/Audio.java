package com.echen.androidcommon.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import com.echen.androidcommon.crypto.MD5Utility;
import com.echen.androidcommon.utility.ImageUtility;

/**
 * Created by echen on 2015/1/27.
 */
public class Audio extends Media {

//    private int id;
//    private String title;
    private String album;
    private String artist;
//    private String path;
//    private String displayName;
//    private String mimeType;
    private long duration;
//    private long size;

    /**
     *
     */
    public Audio() {
        super();
        this.mediaType = MediaCenter.MediaType.Audio;
    }

    /**
     * @param id
     * @param title
     * @param album
     * @param artist
     * @param path
     * @param displayName
     * @param mimeType
     * @param duration
     * @param size
     */
    public Audio(int id, String title, String album, String artist,
                 String path, String displayName, String mimeType, long duration,
                 long size, String dateAdded, String dateModified) {
        super(id, title, displayName, mimeType, path, size,dateAdded, dateModified);
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.mediaType = MediaCenter.MediaType.Audio;
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
        return null;
    }

    @Override
    public Uri tryToGetThumbnailUri(Context context, String cacheThumbnailPath) {
        Uri uri = null;
        try {
            String pathMD5 = MD5Utility.getMD5(getPath());
            String savedPath = cacheThumbnailPath + java.io.File.separator + pathMD5 + ".png";
            java.io.File cacheFile = new java.io.File(savedPath);
            if (cacheFile.exists()) {
                uri = Uri.fromFile(cacheFile);
            } else {
                Bitmap bitmap = createAlbumArt(getPath());
                if (ImageUtility.saveBitmapAsPng(bitmap, savedPath)) {
                    uri = Uri.fromFile(cacheFile);
                }
            }
        }
        catch (Exception e)
        {
            int i =0;
        }
        return uri;
    }

    public Bitmap createAlbumArt(final String filePath) {
        Bitmap bitmap = null;
        //能够获取多媒体文件元数据的类
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath); //设置数据源
            byte[] embedPic = retriever.getEmbeddedPicture(); //得到字节型数据
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //转换为图片
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }
}
