package com.echen.androidcommon.filesystem;

import java.io.Serializable;

/**
 * Created by echen on 2015/2/12.
 */
public class FileSystemInfo implements Serializable {
    protected int id;
    protected String title;
    protected String displayName;
    protected String mimeType;
    protected String path;
    protected long size;
    protected Boolean isSelected = false;
    protected String dateAdded;
    protected String dateModified;

    public FileSystemInfo(){}

    public FileSystemInfo(int id, String title, String displayName, String mimeType,
                 String path, long size, String dateAdded, String dateModified) {
        super();
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Boolean getIsSelected() { return isSelected; }

    public void setIsSelected(Boolean isSelected) { this.isSelected = isSelected; }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
}
