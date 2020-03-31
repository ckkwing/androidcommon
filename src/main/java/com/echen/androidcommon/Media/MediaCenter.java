package com.echen.androidcommon.media;

import android.content.Context;

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
                iMediaProvider = new LocalImageProvider(context, cacheThumbnailPath);
                break;
            case Video:
                iMediaProvider = new LocalVideoProvider(context, cacheThumbnailPath);
                break;
            case Audio:
                iMediaProvider = new LocalAudioProvider(context, cacheThumbnailPath);
                break;
            default:
                break;
        }
        return iMediaProvider;
    }
}
