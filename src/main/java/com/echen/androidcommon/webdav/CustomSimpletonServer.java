package com.echen.androidcommon.webdav;


import com.echen.androidcommon.webdav.event.IRequestEvent;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import io.milton.http.HttpManager;
import io.milton.http.http11.Http11ResponseHandler;
import io.milton.simpleton.SimpletonServer;

/**
 * Created by echen on 1/24/2019
 */
public class CustomSimpletonServer extends SimpletonServer {
    private static final Logger log = LoggerFactory.getLogger(CustomSimpletonServer.class);
    private final List<RequestRegistration> registrations = new CopyOnWriteArrayList<RequestRegistration>();
//    private final Http11ResponseHandler responseHandler;
    private String mConnectedIPAddress = "";
    private HttpManager mHttpManager;
    private Map<String, Long> accessClients = new ConcurrentHashMap<>();

    public CustomSimpletonServer(HttpManager httpManager, Http11ResponseHandler responseHandler, int capacity, int numThreads) {
        super(httpManager, responseHandler, capacity, numThreads);
        this.mHttpManager = httpManager;
//        this.responseHandler = responseHandler;
    }

    @Override
    public void handle(Request request, Response response) {
        super.handle(request,response);
    }

    public void fireEvent(IRequestEvent e) {
        log.trace("fireEvent: {}", e.getClass());
        for (RequestRegistration r : registrations) {
            if (r.clazz.isAssignableFrom(e.getClass())) {
                long tm = System.currentTimeMillis();
                r.listener.onRequestEvent(e);

                if (log.isTraceEnabled()) {
                    log.trace("  fired on: {} completed in {}ms", r.listener.getClass(), (System.currentTimeMillis() - tm));
                }
            }
        }
    }

    public synchronized <T extends IRequestEvent> void registerEventListener(IRequestObserver l, Class<T> c) {
        log.info("registerEventListener: " + l.getClass().getCanonicalName() + " - " + c.getCanonicalName());
        RequestRegistration r = new RequestRegistration(l, c);
        registrations.add(r);
    }

    public synchronized <T extends IRequestEvent> void unregisterEventListener(IRequestObserver l, Class<T> c) {
        log.info("unregisterEventListener: " + l.getClass().getCanonicalName() + " - " + c.getCanonicalName());
        int i =0;
        int foundIndex = -1;
        for (RequestRegistration registration : registrations){
            if (registration.listener == l && c == registration.clazz) {
                foundIndex = i;
                break;
            }
            i++;
        }
        if (foundIndex >= 0 && foundIndex < registrations.size()){
            registrations.remove(foundIndex);
        }
    }




    public static class RequestRegistration {

        public IRequestObserver getListener() {
            return listener;
        }

        public Class<? extends IRequestEvent> getClazz() {
            return clazz;
        }

        private final IRequestObserver listener;
        private final Class<? extends IRequestEvent> clazz;

        public RequestRegistration(IRequestObserver listener, Class<? extends IRequestEvent> clazz) {
            this.listener = listener;
            this.clazz = clazz;
        }

    }

    private long getLastAccessTime(String hostName){
        long lastTM = 0;
        if (accessClients.containsKey(hostName)){
            for(Map.Entry<String, Long> entry : accessClients.entrySet())
            {
                if (entry.getKey().equalsIgnoreCase(hostName))
                {
                    lastTM = entry.getValue();
                    break;
                }
            }
        }
        return lastTM;
    }

    private void setLastAccessTime(String hostName, long tm){
        if (accessClients.containsKey(hostName)){
            for(Map.Entry<String, Long> entry : accessClients.entrySet())
            {
                if (entry.getKey().equalsIgnoreCase(hostName))
                {
                    entry.setValue(tm);
                    break;
                }
            }
        }
        else{
            accessClients.put(hostName, tm);
        }
    }
}
