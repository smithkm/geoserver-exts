package org.geoserver.cluster.hazelcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.geoserver.cluster.ClusterConfig;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;


public class HzCluster implements DisposableBean, InitializingBean {
    
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.cluster.hazelcast");
    
    static final String CONFIG_DIRECTORY = "cluster";
    static final String CONFIG_FILENAME = "cluster.properties";
    static final String HAZELCAST_FILENAME = "hazelcast.xml";
    
    HazelcastInstance hz;
    GeoServerResourceLoader rl;

    /**
     * Get a file from the cluster config directory. Create it by copying a template from the
     * classpath if it doesn't exist.
     * @param fileName Name of the file
     * @param scope Scope for looking up a default if the file doesn't exist.
     * @return
     * @throws IOException
     */
    public File getConfigFile(String fileName, Class<?> scope) throws IOException {
        File dir = rl.findOrCreateDirectory(CONFIG_DIRECTORY);
        File file = rl.find(dir, fileName);
        if (file == null) {
            
            file = rl.createFile(dir, fileName);
            
            rl.copyFromClassPath(fileName, file, scope);
        }
        return file;
    }
    
    /**
     * Is clustering enabled
     * @return
     */
    public boolean isEnabled() {
        return hz!=null;
    }
    
    /**
     * Get the HazelcastInstance being used for clustering
     * @return
     * @throws IllegalStateException if clustering is not enabled
     */
    public HazelcastInstance getHz() {
        if(!isEnabled()) throw new IllegalStateException("Hazelcast Clustering has not been enabled.");
        return hz;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(preInitCheckEnabled(rl))
            hz = Hazelcast.newHazelcastInstance(loadHazelcastConfig(rl));
    }

    @Override
    public void destroy() throws Exception {
        if(hz!=null) {
            hz.getLifecycleService().shutdown();
            hz=null;
        }
    }
    
    private boolean preInitCheckEnabled(GeoServerResourceLoader rl) throws IOException {
        File propFile = getConfigFile(CONFIG_FILENAME, HzCluster.class);
        ClusterConfig cfg;
        InputStream propIn = new FileInputStream(propFile);
        try {
            cfg = new ClusterConfig();
            cfg.load(propIn);
            return cfg.isEnabled();
        } finally {
            propIn.close();
        }
    }
    
    private Config loadHazelcastConfig(GeoServerResourceLoader rl) throws IOException{
        File hzf = getConfigFile(HAZELCAST_FILENAME, HzCluster.class);
        InputStream hzIn = new FileInputStream(hzf);
        try {
            return new XmlConfigBuilder(new FileInputStream(hzf)).build();
        } finally {
            hzIn.close();
        }
    }
    
    /**
     * For Spring initialisation, don't call otherwise.
     * @param dd
     * @throws IOException
     */
    public void setDataDirectory(GeoServerDataDirectory dd) throws IOException {
        rl=dd.getResourceLoader();
    }

}
