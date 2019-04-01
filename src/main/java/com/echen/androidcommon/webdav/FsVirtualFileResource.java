package com.echen.androidcommon.webdav;



import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import io.milton.common.ContentTypeUtils;
import io.milton.common.RangeUtils;
import io.milton.common.ReadingException;
import io.milton.common.WritingException;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.fs.FileContentService;
import io.milton.resource.CopyableResource;
import io.milton.resource.DeletableResource;
import io.milton.resource.GetableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.ReplaceableResource;

/**
 * Created by echen on 1/10/2019
 */

public class FsVirtualFileResource extends FsVirtualResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

    private static final Logger log = LoggerFactory.getLogger(FsVirtualFileResource.class);

    private final FileContentService contentService;

    /**
     *
     * @param host - the requested host. E.g. www.mycompany.com
     * @param factory
     * @param file
     */
    public FsVirtualFileResource(String host, VirtualFileSystemResourceFactory factory, WebDavFile file, FileContentService contentService) {
        super(host, factory, file);
        this.contentService = contentService;
    }

    @Override
    public Long getContentLength() {
        return file.length();
    }

    @Override
    public String getContentType(String preferredList) {
        String mime = ContentTypeUtils.findContentTypes(this.file.getRealFile());
        String s = ContentTypeUtils.findAcceptableContentType(mime, preferredList);
        if (log.isTraceEnabled()) {
            log.trace("getContentType: preferred: {} mime: {} selected: {}", new Object[]{preferredList, mime, s});
        }
        return s;
    }

    @Override
    public String checkRedirect(Request arg0) {
        return null;
    }

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotFoundException {
        InputStream in = null;
        try {
            in = contentService.getFileContent(file.getRealFile());
            if (range != null) {
                log.debug("sendContent: ranged content: " + file.getAbsolutePath());
                RangeUtils.writeRange(in, range, out);
            } else {
                log.debug("sendContent: send whole file " + file.getAbsolutePath());
                IOUtils.copy(in, out);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Couldnt locate content");
        } catch (ReadingException e) {
            throw new IOException(e);
        } catch (WritingException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return factory.maxAgeSeconds(this);
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    protected void doCopy(WebDavFile dest) {
        try {
            FileUtils.copyFile(file.getRealFile(), dest.getRealFile());
        } catch (IOException ex) {
            throw new RuntimeException("Failed doing copy to: " + dest.getRealFile().getAbsolutePath(), ex);
        }
    }



    @Override
    public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        try {
            contentService.setFileContent(file.getRealFile(), in);
            //FileUtility.notifyMedieStore(file.getRealFile(), WifiSyncApplication.getInstance());
        } catch (IOException ex) {
            throw new BadRequestException("Couldnt write to: " + file.getRealFile().getAbsolutePath(), ex);
        }
    }
}