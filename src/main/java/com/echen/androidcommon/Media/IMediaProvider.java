package com.echen.androidcommon.media;

import java.util.List;

/**
 * Created by echen on 2015/1/27.
 */
public interface IMediaProvider<T extends Media> {
    List<T> getList();
}
