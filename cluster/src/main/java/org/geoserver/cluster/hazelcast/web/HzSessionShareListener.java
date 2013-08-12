package org.geoserver.cluster.hazelcast.web;

import javax.servlet.http.HttpSessionEvent;

import org.geoserver.cluster.hazelcast.HzCluster;

import com.hazelcast.web.SessionListener;

/**
 * 
 * @author smithkm
 *
 */
public class HzSessionShareListener extends SessionListener {
    HzCluster cluster;
    
    public void setCluster(HzCluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        if(cluster.isEnabled()) super.sessionCreated(httpSessionEvent);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        if(cluster.isEnabled()) super.sessionDestroyed(httpSessionEvent);
    }
    
}
