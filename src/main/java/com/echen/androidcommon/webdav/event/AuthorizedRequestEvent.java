package com.echen.androidcommon.webdav.event;

/**
 * Created by echen on 1/24/2019
 */
public class AuthorizedRequestEvent implements IRequestEvent {
    private boolean mIsAllowed = false;
    private String mHostAddress = "";

    public boolean getIsAllowed() {
        return mIsAllowed;
    }

    public void setIsAllowed(boolean mIsAllowed) {
        this.mIsAllowed = mIsAllowed;
    }

    public String getHostAddress() {
        return mHostAddress;
    }

    public AuthorizedRequestEvent(String hostAddress){
        this.mHostAddress = hostAddress;
    }

}
