package com.echen.androidcommon.webdav;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.milton.http.Auth;
import io.milton.http.DateUtils;
import io.milton.http.LockInfo;
import io.milton.http.LockResult;
import io.milton.http.LockTimeout;
import io.milton.http.LockToken;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.http.XmlWriter;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.fs.FileContentService;
import io.milton.http.fs.FileSystemResourceFactory;
import io.milton.http.fs.FsDirectoryResource;
import io.milton.http.fs.FsFileResource;
import io.milton.http.fs.FsResource;
import io.milton.resource.CollectionResource;
import io.milton.resource.CopyableResource;
import io.milton.resource.DeletableResource;
import io.milton.resource.GetableResource;
import io.milton.resource.LockingCollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.PutableResource;
import io.milton.resource.Resource;

/**
 * Created by echen on 1/10/2019
 */
public class FsVirutalDirectoryResource extends FsVirtualResource implements MakeCollectionableResource, PutableResource, CopyableResource, DeletableResource, MoveableResource, PropFindableResource, LockingCollectionResource, GetableResource {
    private static final Logger log = LoggerFactory.getLogger(FsVirutalDirectoryResource.class);

    private final FileContentService contentService;

    public FsVirutalDirectoryResource(String host, VirtualFileSystemResourceFactory factory, WebDavFile dir, FileContentService contentService) {
        super(host, factory, dir);
        this.contentService = contentService;
        if (!dir.exists()) {
            throw new IllegalArgumentException("Directory does not exist: " + dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + dir.getAbsolutePath());
        }
    }



    @Override
    protected void doCopy(WebDavFile dest) {
        try {
            FileUtils.copyDirectory(this.getFile().getRealFile(), dest.getRealFile());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to copy to:" + dest.getRealFile().getAbsolutePath(), ex);
        }
    }

    @Override
    public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
        WebDavFile fchild = new WebDavFile(file, childName, file.getChildRealPath(childName));
        return factory.resolveFile(this.host, fchild);
    }

    @Override
    public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
        ArrayList<FsVirtualResource> list = new ArrayList<FsVirtualResource>();
        WebDavFile[] files = this.file.listWebDavFiles();
        if (files != null) {
            for (WebDavFile fchild : files) {
                FsVirtualResource res = factory.resolveFile(this.host, fchild);
                if (res != null) {
                    list.add(res);
                } else {
                    log.error("Couldnt resolve file {}", fchild.getAbsolutePath());
                }
            }
        }
        return list;
    }

    /**
     * Will generate a listing of the contents of this directory, unless the
     * factory's allowDirectoryBrowsing has been set to false.
     *
     * If so it will just output a message saying that access has been disabled.
     *
     * @param out
     * @param range
     * @param params
     * @param contentType
     * @throws IOException
     * @throws NotAuthorizedException
     */
    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException {
        String subpath = getFile().getCanonicalPath().substring(factory.getRoot().getCanonicalPath().length()).replace('\\', '/');
        String uri = subpath;
        //String uri = "/" + factory.getContextPath() + subpath;
        XmlWriter w = new XmlWriter(out);
        w.open("html");
        w.open("head");
        w.writeText(""
                + "<script type=\"text/javascript\" language=\"javascript1.1\">\n"
                + "    var fNewDoc = false;\n"
                + "  </script>\n"
                + "  <script LANGUAGE=\"VBSCRIPT\">\n"
                + "    On Error Resume Next\n"
                + "    Set EditDocumentButton = CreateObject(\"SharePoint.OpenDocuments.3\")\n"
                + "    fNewDoc = IsObject(EditDocumentButton)\n"
                + "  </script>\n"
                + "  <script type=\"text/javascript\" language=\"javascript1.1\">\n"
                + "    var L_EditDocumentError_Text = \"The edit feature requires a SharePoint-compatible application and Microsoft Internet Explorer 4.0 or greater.\";\n"
                + "    var L_EditDocumentRuntimeError_Text = \"Sorry, couldnt open the document.\";\n"
                + "    function editDocument(strDocument) {\n"
                + "      strDocument = 'http://192.168.1.2:8080' + strDocument; "
                + "      if (fNewDoc) {\n"
                + "        if (!EditDocumentButton.EditDocument(strDocument)) {\n"
                + "          alert(L_EditDocumentRuntimeError_Text + ' - ' + strDocument); \n"
                + "        }\n"
                + "      } else { \n"
                + "        alert(L_EditDocumentError_Text + ' - ' + strDocument); \n"
                + "      }\n"
                + "    }\n"
                + "  </script>\n");



        w.close("head");
        w.open("body");
        w.begin("h1").open().writeText(this.getName()).close();
        w.open("table");
        for (Resource r : getChildren()) {
            w.open("tr");

            w.open("td");
            String path = buildHref(uri, r.getName());
            w.begin("a").writeAtt("href", path).open().writeText(r.getName()).close();

            w.begin("a").writeAtt("href", "#").writeAtt("onclick", "editDocument('" + path + "')").open().writeText("(edit with office)").close();

            w.close("td");

            w.begin("td").open().writeText(r.getModifiedDate() + "").close();
            w.close("tr");
        }
        w.close("table");
        w.close("body");
        w.close("html");
        w.flush();
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    @Override
    public String getContentType(String accepts) {
        return "text/html";
    }

    @Override
    public Long getContentLength() {
        return null;
    }

    @Override
    public LockToken createAndLock(String name, LockTimeout timeout, LockInfo lockInfo) throws NotAuthorizedException {
        WebDavFile dest = new WebDavFile(this.getFile(), name, file.getChildRealPath(name));
        createEmptyFile(dest);
        FsVirtualFileResource newRes = new FsVirtualFileResource(host, factory, dest, contentService);
        LockResult res = newRes.lock(timeout, lockInfo);
        return res.getLockToken();
    }

    private void createEmptyFile(WebDavFile file) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file.getRealFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fout);
        }
    }

    @Override
    public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        WebDavFile fnew = new WebDavFile(file, newName, file.getChildRealPath(newName));
        boolean ok = fnew.mkdir();
        if (!ok) {
            throw new RuntimeException("Failed to create: " + fnew.getAbsolutePath());
        }
        return new FsVirutalDirectoryResource(host, factory, fnew, contentService);
    }

    @Override
    public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
        Resource resource = null;
        try {
           WebDavFile dest = new WebDavFile(file, newName, file.getChildRealPath(newName));
           contentService.setFileContent(dest.getRealFile(), inputStream);
            resource = factory.resolveFile(this.host, dest);
       }
       catch (Exception e){
           log.warn("createNew failed, " + e.getMessage());
       }
       return resource;
    }

    /**
     * Will redirect if a default page has been specified on the factory
     *
     * @param request
     * @return
     */
    @Override
    public String checkRedirect(Request request) {
        if (factory.getDefaultPage() != null) {
            return request.getAbsoluteUrl() + "/" + factory.getDefaultPage();
        } else {
            return null;
        }
    }

    private String buildHref(String uri, String name) {
        String abUrl = uri;

        if (!abUrl.endsWith("/")) {
            abUrl += "/";
        }
        if (ssoPrefix == null) {
            return abUrl + name;
        } else {
            // This is to match up with the prefix set on SimpleSSOSessionProvider in MyCompanyDavServlet
            String s = insertSsoPrefix(abUrl, ssoPrefix);
            return s += name;
        }
    }

    public static String insertSsoPrefix(String abUrl, String prefix) {
        // need to insert the ssoPrefix immediately after the host and port
        int pos = abUrl.indexOf("/", 8);
        String s = abUrl.substring(0, pos) + "/" + prefix;
        s += abUrl.substring(pos);
        return s;
    }
}
