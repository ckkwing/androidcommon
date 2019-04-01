/*
 * Copyright 2012 McEvoy Software Ltd.
 */
package io.milton.http.webdav;

import io.milton.extention.ExtendedLockUtils;
import io.milton.http.LockInfo;
import io.milton.http.LockToken;
import io.milton.http.XmlWriter;
import io.milton.http.XmlWriter.Element;
import io.milton.http.values.ValueWriter;
import io.milton.http.webdav.WebDavProtocol;

import java.util.Map;

public class LockTokenValueWriter implements ValueWriter {

    @Override
    public boolean supports(String nsUri, String localName, Class c) {
        return LockToken.class.isAssignableFrom(c);
    }

    @Override
    public void writeValue(XmlWriter writer, String nsUri, String prefix, String localName, Object val, String href, Map<String, String> nsPrefixes) {
        LockToken token = (LockToken) val;
        String d = WebDavProtocol.DAV_PREFIX;
        Element lockDiscovery = writer.begin(d + ":lockdiscovery").open();
        if (token != null) {
            Element activeLock = writer.begin(d + ":activelock").open();
            LockInfo info = token.info;
            ExtendedLockUtils.appendType(writer, info.type);
            ExtendedLockUtils.appendScope(writer, info.scope);
            ExtendedLockUtils.appendDepth(writer, info.depth);
            ExtendedLockUtils.appendOwner(writer, info.lockedByUser);
            ExtendedLockUtils.appendTimeout(writer, token.timeout.getSeconds());
            ExtendedLockUtils.appendTokenId(writer, token.tokenId);
            ExtendedLockUtils.appendRoot(writer, href);
            activeLock.close();
        }
        lockDiscovery.close();
    }

    @Override
    public Object parse(String namespaceURI, String localPart, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
