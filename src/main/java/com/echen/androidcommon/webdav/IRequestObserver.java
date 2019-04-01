package com.echen.androidcommon.webdav;


import com.echen.androidcommon.webdav.event.IRequestEvent;

/**
 * Created by echen on 1/24/2019
 */
public interface IRequestObserver {
    void onRequestEvent(IRequestEvent e);
}
