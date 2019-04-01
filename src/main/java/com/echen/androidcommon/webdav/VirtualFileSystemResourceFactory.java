package com.echen.androidcommon.webdav;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import io.milton.cache.LocalCacheManager;
import io.milton.http.HttpManager;
import io.milton.http.Request;
import io.milton.http.fs.FileSystemResourceFactory;
import io.milton.http.fs.FsDirectoryResource;
import io.milton.http.fs.FsFileResource;
import io.milton.http.fs.FsResource;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.fs.SimpleLockManager;
import io.milton.resource.Resource;
import io.milton.common.Path;

/**
 * Created by echen on 1/10/2019
 */
public class VirtualFileSystemResourceFactory extends FileSystemResourceFactory {
    private static final Logger log = LoggerFactory.getLogger(VirtualFileSystemResourceFactory.class);
    private WebDavFile mRoot;
    private boolean mRequestAllowed = true;
    public void setRequestAllowed(boolean requestAllowed){
        this.mRequestAllowed = requestAllowed;
        ((VirtualRootFile)mRoot).resetChildren();
    }

    public VirtualFileSystemResourceFactory(WebDavFile root, io.milton.http.SecurityManager securityManager, String contextPath) {
        super(root, securityManager, contextPath);
        this.mRoot = root;

        setLockManager(new SimpleLockManager(new LocalCacheManager()));
    }


    @Override
    public Resource getResource(String host, String url) {
        log.debug("getResource: host: " + host + " - url:" + url);
        Request request = HttpManager.request();
        url = stripContext(url);
        WebDavFile requested = resolvePathByUrl(mRoot, url);
        return resolveFile(host, requested);
    }

    public WebDavFile resolvePathByUrl(WebDavFile root, String url) {
        Path path = Path.path(url);
        String realPath = url;
        Pair<String, String> pair = null;
        WebDavFile concreteRootFile = ((VirtualRootFile) getRoot()).findRealFileByDisplayUrlStarted(realPath);
        if (null != concreteRootFile) {
            //realPath = StringUtility.getStringByReplacingTheFirstSpecifiedString(url, concreteRootFile.getDisplayPath(), concreteRootFile.getFile().getAbsolutePath());
            //return concreteRootFile;
            pair = new Pair<>(concreteRootFile.getName(), concreteRootFile.getRealFile().getPath());
        }

        WebDavFile f = (WebDavFile) root;
        StringBuilder fullPath = new StringBuilder("");
        int i = 0;
        for (String s : path.getParts()) {
            if (0 == i && null != pair && s.equalsIgnoreCase(pair.first.replace("/", "")))
                fullPath.append(pair.second);
            else {
                fullPath.append("/");
                fullPath.append(s);
            }
            f = new WebDavFile(f, s, fullPath.toString());
            i++;
//            f.setFile(new File(realPath));
        }
        return f;
    }

    public FsVirtualResource resolveFile(String host, WebDavFile file) {
        FsVirtualResource r;
        if (!file.exists()) {
            log.debug("file not found: " + file.getAbsolutePath());
            return null;
        } else if (file.isDirectory()) {
            r = new FsVirutalDirectoryResource(host, this, file, contentService);
        } else {
            if (file.getName().startsWith(".")) {
                return null;
            }
            r = new FsVirtualFileResource(host, this, file, contentService);
        }
        if (r != null) {
            r.setSsoPrefix(ssoPrefix);
        }
        return r;
    }

    /**
     *
     * @return - the caching time for files
     */
    public Long maxAgeSeconds(FsVirtualResource resource) {
        return maxAgeSeconds;
    }
}
