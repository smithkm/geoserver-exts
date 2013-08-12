package org.geoserver.cluster.hazelcast.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.geoserver.cluster.hazelcast.HzCluster;
import org.geoserver.filters.GeoServerFilter;

import com.google.common.collect.Iterators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.web.WebFilter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

/**
 * Creates and delegates to a WebFilter if clustering is enabled.
 * @author Kevin Smith
 *
 */
public class HzSessionShareFilter implements GeoServerFilter, InitializingBean, ServletContextAware {
    WebFilter delegate;
    ServletContext srvCtx;
    HzCluster cluster;

    public void setCluster(final HzCluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if(delegate!=null){
            delegate.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        if(delegate!=null) delegate.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
        // Stop if clustering is not enabled
        if(!cluster.isEnabled()) return;
        
        // Create the delegate and override its getInstance method to use the cluster's instance
        delegate = new WebFilter(){

            @Override
            protected HazelcastInstance getInstance(Properties properties)
                    throws ServletException {
                return cluster.getHz();
            }
            
        };

        // Set up init-params for the delegate instance
        // TODO Maybe make these configurable in cluster.properties
        final Map<String, String> params = new HashMap<String,String>();
        params.put("map-name", "geoserver-sessions");
        params.put("sticky-session", "false");
        params.put("instance-name",  cluster.getHz().getConfig().getInstanceName());
        
        FilterConfig config = new FilterConfig() {

            @Override
            public String getFilterName() {
                return "hazelcast";
            }

            @Override
            public ServletContext getServletContext() {
                return srvCtx;
            }

            @Override
            public String getInitParameter(String name) {
                return params.get(name);
            }

            @Override
            public Enumeration getInitParameterNames() {
                return Iterators.asEnumeration(params.keySet().iterator());
            }
            
        };
        
        delegate.init(config);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        srvCtx = servletContext;
    }
}
