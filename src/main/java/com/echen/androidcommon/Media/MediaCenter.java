package com.echen.androidcommon.Media;

import android.content.Context;
import android.provider.MediaStore;

/**
 * Created by echen on 2015/2/11.
 */
public class MediaCenter {
    public enum MediaType
    {
        Image,
        Video,
        Audio
    }

    public static IMediaProvider CreateMediaProvider(Context context, MediaType mediaType, String cacheThumbnailPath)
    {
        IMediaProvider iMediaProvider = null;
        switch (mediaType)
        {
            case Image:
                iMediaProvider = new ImageProvider(context, cacheThumbnailPath);
                break;
            case Video:
                iMediaProvider = new VideoProvider(context, cacheThumbnailPath);
                break;
            case Audio:
                iMediaProvider = new AudioProvider(context, cacheThumbnailPath);
                break;
            default:
                break;
        }
        return iMediaProvider;
    }
}
