package com.echen.androidcommon.webdav;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.xml.namespace.QName;

import io.milton.extention.ExtendedLockUtils;
import io.milton.http.webdav.ResourceTypeHelper;
import io.milton.resource.LockableResource;
import io.milton.resource.Resource;


public class WebDavExtensionResourceTypeHelper implements ResourceTypeHelper {

    private static final Logger log = LoggerFactory.getLogger(WebDavExtensionResourceTypeHelper.class);

    private final ResourceTypeHelper wrapped;

    public WebDavExtensionResourceTypeHelper(ResourceTypeHelper wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<QName> getResourceTypes(Resource r) {
        return wrapped.getResourceTypes(r);
    }

    //Need to create a ArrayList as Arrays.asList returns a fixed length list which
    //cannot be extended.
    @Override
    public List<String> getSupportedLevels(Resource r) {
        List<String> list = wrapped.getSupportedLevels(r);
        if (r instanceof LockableResource) {
            ExtendedLockUtils.add(list, "2");
        }
        return list;
    }
}