package com.echen.androidcommon.webdav;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import io.milton.http.Auth;
import io.milton.http.LockInfo;
import io.milton.http.LockResult;
import io.milton.http.LockTimeout;
import io.milton.http.LockToken;
import io.milton.http.Request;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.CopyableResource;
import io.milton.resource.DigestResource;
import io.milton.resource.LockableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.Resource;

/**
 * Created by echen on 1/10/2019
 */

public abstract class FsVirtualResource implements Resource, MoveableResource, CopyableResource, LockableResource, DigestResource {

    private static final Logger log = LoggerFactory.getLogger(io.milton.http.fs.FsResource.class);
    WebDavFile file;
    final VirtualFileSystemResourceFactory factory;
    final String host;
    String ssoPrefix;


    public void setSsoPrefix(String ssoPrefix) {
        this.ssoPrefix = ssoPrefix;
    }

    protected abstract void doCopy(WebDavFile dest);

    public FsVirtualResource(String host, VirtualFileSystemResourceFactory factory, WebDavFile file) {
        this.host = host;
        this.file = file;
        this.factory = factory;
    }

    public WebDavFile getFile() {
        return file;
    }

    @Override
    public String getUniqueId() {
        //String s = file.lastModified() + "_" + file.length() + "_" + file.getAbsolutePath();
        String s = "_" + file.getAbsolutePath() + "_";
        return s.hashCode() + "";
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public Object authenticate(String user, String password) {
        return factory.getSecurityManager().authenticate(user, password);
    }

    public Object authenticate(DigestResponse digestRequest) {
        return factory.getSecurityManager().authenticate(digestRequest);
    }

    public boolean isDigestAllowed() {
        return factory.isDigestAllowed();
    }

    @Override
    public boolean authorise(Request request, Request.Method method, Auth auth) {
        boolean b = factory.getSecurityManager().authorise(request, method, auth, this);
        if( log.isTraceEnabled()) {
            log.trace("authorise: result=" + b);
        }
        return b;
    }

    @Override
    public String getRealm() {
        String r = factory.getRealm(this.host);
        if( r == null ) {
            throw new NullPointerException("Got null realm from: " + factory.getClass() + " for host=" + this.host);
        }
        return r;
    }

    @Override
    public Date getModifiedDate() {
        return new Date(file.lastModified());
    }

    public Date getCreateDate() {
        return null;
    }

    public int compareTo(Resource o) {
        return this.getName().compareTo(o.getName());
    }

    public void moveTo(CollectionResource newParent, String newName) {
        if (newParent instanceof FsVirutalDirectoryResource) {
            FsVirutalDirectoryResource newFsParent = (FsVirutalDirectoryResource) newParent;
            WebDavFile dest = new WebDavFile(newFsParent.getFile(), newName, newFsParent.getFile().getChildRealPath(newName));
            boolean ok = this.file.renameTo(dest);
            if (!ok) {
                throw new RuntimeException("Failed to move to: " + dest.getAbsolutePath());
            }
            this.file = dest;
        } else {
            throw new RuntimeException("Destination is an unknown type. Must be a FsDirectoryResource, is a: " + newParent.getClass());
        }
    }

    public void copyTo(CollectionResource newParent, String newName) {
        if (newParent instanceof FsVirutalDirectoryResource) {
            FsVirutalDirectoryResource newFsParent = (FsVirutalDirectoryResource) newParent;
            WebDavFile dest = new WebDavFile(newFsParent.getFile(), newName,newFsParent.getFile().getChildRealPath(newName));
            doCopy(dest);
        } else {
            throw new RuntimeException("Destination is an unknown type. Must be a FsDirectoryResource, is a: " + newParent.getClass());
        }
    }

    public void delete() {
        boolean ok = file.delete();
        if (!ok) {
            throw new RuntimeException("Failed to delete");
        } else {
            //FileUtility.notifyMedieStore(file.getRealFile(), WifiSyncApplication.getInstance());
        }
    }

    public LockResult lock(LockTimeout timeout, LockInfo lockInfo) throws NotAuthorizedException {
        return factory.getLockManager().lock(timeout, lockInfo, this);
    }

    public LockResult refreshLock(String token) throws NotAuthorizedException {
        return factory.getLockManager().refresh(token, this);
    }

    public void unlock(String tokenId) throws NotAuthorizedException {
        factory.getLockManager().unlock(tokenId, this);
    }

    public LockToken getCurrentLock() {
        if (factory.getLockManager() != null) {
            return factory.getLockManager().getCurrentToken(this);
        } else {
            log.warn("getCurrentLock called, but no lock manager: file: " + file.getAbsolutePath());
            return null;
        }
    }
}