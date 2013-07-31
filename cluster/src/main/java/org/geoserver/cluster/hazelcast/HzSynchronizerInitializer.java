package org.geoserver.cluster.hazelcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.geoserver.cluster.ClusterConfig;
import org.geoserver.cluster.ClusterConfigWatcher;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerInitializer;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geotools.util.logging.Logging;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HzSynchronizerInitializer implements GeoServerInitializer {

    protected static Logger LOGGER = Logging.getLogger("org.geoserver.cluster.hazelcast");

    static final String CONFIG_DIRECTORY = "cluster";
    static final String CONFIG_FILENAME = "cluster.properties";
    static final String HAZELCAST_FILENAME = "hazelcast.xml";

    /** hazelcast */
    HazelcastInstance hz;

    public HzSynchronizerInitializer() {
    }
    
    // Get a file from the cluster config directory, create it if it's not there. 
    // Optionally copy in a file of the same name from the classpath.
    private File getConfigFile(String fileName, GeoServerResourceLoader rl, boolean copyFromClassPath) throws IOException {
        File dir = rl.findOrCreateDirectory(CONFIG_DIRECTORY);
        File file = rl.find(dir, fileName);
        if (file == null) {
            
            file = rl.createFile(dir, fileName);
            
            if(copyFromClassPath) rl.copyFromClassPath(fileName, file, HzSynchronizerInitializer.class);
        }
        return file;
    }
    private boolean preInitCheckEnabled(GeoServerResourceLoader rl) throws IOException {
        File propFile = getConfigFile(CONFIG_FILENAME, rl, false);
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
    /**
     * For Spring initialisation, don't call otherwise.
     * @param dd
     * @throws IOException
     */
    public void setDataDirectory(GeoServerDataDirectory dd) throws IOException {
        // Hazelcast needs to be initialised while Spring is setting up the context.
        // Can't wait for #initialize(GeoServer) to be called.
        
        // Check if the module is enabled and start Hazelcast if so
        
        GeoServerResourceLoader rl = dd.getResourceLoader();
        if(preInitCheckEnabled(rl))
            hz = Hazelcast.newHazelcastInstance(loadHazelcastConfig(rl));
    }
    
    // Need to shut down Hazelcast.
    public void dispose() {
        if(hz!=null) {
            hz.getLifecycleService().shutdown();
            hz=null;
        }
    }

    @Override
    public void initialize(GeoServer geoServer) throws Exception {
        ClusterConfigWatcher configWatcher = loadConfig(geoServer.getCatalog().getResourceLoader());
        ClusterConfig config = configWatcher.get();
        

        if (!config.isEnabled()) {
            LOGGER.info("Hazelcast synchronization disabled");
            return;
        }
        
        // Throws an exception if Hazelcast was not initialised earlier.
        getHzInstance();
        
        HzSynchronizer syncher = null;

        String method = config.getSyncMethod();
        if ("event".equalsIgnoreCase(method)) {
            syncher = new EventHzSynchronizer(hz, geoServer);
        }
        else {
            method = "reload"; 
            syncher = new ReloadHzSynchronizer(hz, geoServer);
        }

        syncher.initialize(configWatcher);
        LOGGER.info("Hazelcast synchronizer method is " + method);
    }

    public HazelcastInstance getHzInstance() {
        if(hz==null) throw new IllegalStateException("No Hazelcast instance available.");
        
        return hz;
    }
    
    Config loadHazelcastConfig(GeoServerResourceLoader rl) throws IOException{
        File hzf = getConfigFile(HAZELCAST_FILENAME, rl, true);
        InputStream hzIn = new FileInputStream(hzf);
        try {
            return new XmlConfigBuilder(new FileInputStream(hzf)).build();
        } finally {
            hzIn.close();
        }
    }
    
    ClusterConfigWatcher loadConfig(GeoServerResourceLoader rl) throws IOException {
        File f = getConfigFile(CONFIG_FILENAME, rl, false);
        
        return new ClusterConfigWatcher(f);
    }
}
