package com.echen.androidcommon.webdav;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import io.milton.common.NameSpace;
import io.milton.http.Handler;
import io.milton.http.HandlerHelper;
import io.milton.http.HttpExtension;
import io.milton.http.HttpManager;
import io.milton.http.LockToken;
import io.milton.http.ResourceHandlerHelper;
import io.milton.http.http11.CustomPostHandler;
import io.milton.http.webdav.LockHandler;
import io.milton.http.webdav.PropertyMap;
import io.milton.http.webdav.SupportedLocks;
import io.milton.http.webdav.UnlockHandler;
import io.milton.http.webdav.UserAgentHelper;
import io.milton.http.webdav.WebDavProtocol;
import io.milton.http.webdav.WebDavResponseHandler;
import io.milton.property.PropertySource;
import io.milton.resource.LockableResource;
import io.milton.resource.PropFindableResource;
import io.milton.resource.Resource;

/**
 * Created by echen on 1/11/2019
 */
//public class WebDavExtensionProtocol implements HttpExtension {
//    private final Set<Handler> handlers;
//
//    private final HandlerHelper handlerHelper;
//
//    private List<CustomPostHandler> customPostHandlers;
//
//    public WebDavExtensionProtocol(WebDavResponseHandler responseHandler, HandlerHelper handlerHelper, ResourceHandlerHelper resourceHandlerHelper) {
//        this.handlers = new HashSet<Handler>();
//        this.handlerHelper = handlerHelper;
//        handlers.add(new LockHandler(responseHandler, handlerHelper));
//        handlers.add(new UnlockHandler(resourceHandlerHelper, responseHandler));
//    }
//
//    @Override
//    public Set<Handler> getHandlers() {
//        return handlers;
//    }
//
//    public HandlerHelper getHandlerHelper() {
//        return handlerHelper;
//    }
//
//    @Override
//    public List<CustomPostHandler> getCustomPostHandlers() {
//        return customPostHandlers;
//    }
//}

public class WebDavExtensionProtocol implements HttpExtension, PropertySource {

    private static final Logger log = LoggerFactory.getLogger(WebDavExtensionProtocol.class);
    public static final String DAV_URI = "DAV:";
    public static final String DAV_PREFIX = "d";
    public static final NameSpace NS_DAV = new NameSpace(DAV_URI, DAV_PREFIX);
    private final Set<Handler> handlers;
    private final PropertyMap propertyMap;
    private final UserAgentHelper userAgentHelper;
    private List<CustomPostHandler> customPostHandlers;

    public WebDavExtensionProtocol(HandlerHelper handlerHelper, WebDavResponseHandler responseHandler, ResourceHandlerHelper resourceHandlerHelper, UserAgentHelper userAgentHelper) {
        this.userAgentHelper = userAgentHelper;
        this.propertyMap = new PropertyMap(WebDavProtocol.NS_DAV.getName());
        propertyMap.add(new SupportedLockPropertyWriter());
        propertyMap.add(new LockDiscoveryPropertyWriter());

        handlers = new HashSet<Handler>();
        handlers.add(new LockHandler(responseHandler, handlerHelper));
        handlers.add(new UnlockHandler(resourceHandlerHelper, responseHandler));
    }

    @Override
    public List<CustomPostHandler> getCustomPostHandlers() {
        return customPostHandlers;
    }

    public void setCustomPostHandlers(List<CustomPostHandler> customPostHandlers) {
        this.customPostHandlers = customPostHandlers;
    }

    @Override
    public Set<Handler> getHandlers() {
        return Collections.unmodifiableSet(handlers);
    }

    @Override
    public Object getProperty(QName name, Resource r) {
        Object o = propertyMap.getProperty(name, r);
        return o;
    }

    @Override
    public void setProperty(QName name, Object value, Resource r) {
        throw new UnsupportedOperationException("Not supported. Standard webdav properties are not writable");
    }


    @Override
    public PropertyMetaData getPropertyMetaData(QName name, Resource r) {
        PropertyMetaData propertyMetaData = propertyMap.getPropertyMetaData(name, r);
        if (propertyMetaData != null) {
            // Nautilus (at least on Ubuntu 12) doesnt like empty properties
            if (userAgentHelper.isNautilus(HttpManager.request())) {
                Object v = getProperty(name, r);
                if (v == null) {
                    return PropertyMetaData.UNKNOWN;
                } else if (v instanceof String) {
                    String s = (String) v;
                    if (s.trim().length() == 0) {
                        return PropertyMetaData.UNKNOWN;
                    }
                }
            }
        }

        return propertyMetaData;
    }

    @Override
    public void clearProperty(QName name, Resource r) {
        throw new UnsupportedOperationException("Not supported. Standard webdav properties are not writable");
    }

    @Override
    public List<QName> getAllPropertyNames(Resource r) {
        return propertyMap.getAllPropertyNames(r);
    }


    //    <D:supportedlock/><D:lockdiscovery/>
    class LockDiscoveryPropertyWriter implements PropertyMap.StandardProperty<LockToken> {

        @Override
        public LockToken getValue(PropFindableResource res) {
            if (!(res instanceof LockableResource)) {
                return null;
            }
            LockableResource lr = (LockableResource) res;
            LockToken token = lr.getCurrentLock();
            return token;
        }

        @Override
        public String fieldName() {
            return "lockdiscovery";
        }

        @Override
        public Class getValueClass() {
            return LockToken.class;
        }
    }

    class SupportedLockPropertyWriter implements PropertyMap.StandardProperty<SupportedLocks> {

        @Override
        public SupportedLocks getValue(PropFindableResource res) {
            if (res instanceof LockableResource) {
                return new SupportedLocks(res);
            } else {
                return null;
            }
        }

        @Override
        public String fieldName() {
            return "supportedlock";
        }

        @Override
        public Class getValueClass() {
            return SupportedLocks.class;
        }
    }
}
