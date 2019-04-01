package com.echen.androidcommon.webdav;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import io.milton.config.HttpManagerBuilder;
import io.milton.http.HttpExtension;
import io.milton.http.LockManager;
import io.milton.http.ProtocolHandlers;
import io.milton.http.annotated.AnnotationResourceFactory;
import io.milton.http.fs.SimpleLockManager;
import io.milton.http.http11.Http11Protocol;
import io.milton.http.http11.MatchHelper;
import io.milton.http.http11.PartialGetHelper;
import io.milton.http.webdav.LockTokenValueWriter;
import io.milton.http.webdav.ResourceTypeHelper;
import io.milton.http.webdav.SupportedLockValueWriter;
import io.milton.http.webdav.WebDavResourceTypeHelper;
import io.milton.http.webdav.WebDavResponseHandler;
import io.milton.property.PropertySource;

/**
 * Created by echen on 1/11/2019
 */
public class CustomHttpManagerBuilder extends HttpManagerBuilder {
    private static final Logger log = LoggerFactory.getLogger(CustomHttpManagerBuilder.class);
    private WebDavExtensionProtocol webDavExtensionProtocol;
    private LockManager lockManager;

    @Override
    protected void afterInit() {
        super.afterInit();
        if (getMainResourceFactory() instanceof AnnotationResourceFactory) {
            AnnotationResourceFactory arf = (AnnotationResourceFactory) getMainResourceFactory();

            if (arf.getLockManager() == null) {
                if (lockManager == null) {
                    lockManager = new SimpleLockManager(getCacheManager());
                    log.info("Created lock manager: {} with cache manager: {}",lockManager, getCacheManager());
                } else {
                    log.info("Using configured cache manager: {}", lockManager);
                }

                arf.setLockManager(lockManager);
            } else {
                log.info("Using LockManager from AnnotationResourceFactory: {}", arf.getLockManager().getClass());
            }
        }
    }

    @Override
    protected void buildProtocolHandlers(WebDavResponseHandler webdavResponseHandler, ResourceTypeHelper resourceTypeHelper) {
        if (protocols == null) {
            protocols = new ArrayList<HttpExtension>();

            if (matchHelper == null) {
                matchHelper = new MatchHelper(eTagGenerator);
            }
            if (partialGetHelper == null) {
                partialGetHelper = new PartialGetHelper();
            }

            Http11Protocol http11Protocol = new Http11Protocol(webdavResponseHandler, handlerHelper, resourceHandlerHelper, enableOptionsAuth, matchHelper, partialGetHelper);
            protocols.add(http11Protocol);
            initDefaultPropertySources(resourceTypeHelper);
            if (extraPropertySources != null) {
                for (PropertySource ps : extraPropertySources) {
                    log.info("Add extra property source: {}", ps.getClass());
                    propertySources.add(ps);
                }
            }

            initWebdavProtocol();
            if (webDavProtocol != null) {
                protocols.add(webDavProtocol);
            }

            if (webDavExtensionProtocol == null) {
                webDavExtensionProtocol = new WebDavExtensionProtocol(handlerHelper, webdavResponseHandler, resourceHandlerHelper, userAgentHelper());
                valueWriters.getValueWriters().add(0, new SupportedLockValueWriter());
                valueWriters.getValueWriters().add(0, new LockTokenValueWriter());

                protocols.add(webDavExtensionProtocol);
                if (webDavProtocol != null) {
                    webDavProtocol.addPropertySource(webDavExtensionProtocol);
                }
            }
        }

        if (protocolHandlers == null) {
            protocolHandlers = new ProtocolHandlers(protocols);
        }
    }

    @Override
    protected void buildResourceTypeHelper() {
        WebDavResourceTypeHelper webDavResourceTypeHelper = new WebDavResourceTypeHelper();
        resourceTypeHelper = new WebDavExtensionResourceTypeHelper(webDavResourceTypeHelper);
        showLog("resourceTypeHelper", resourceTypeHelper);
    }
}
