package org.geoserver.cluster.hazelcast;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.geoserver.cluster.ClusterConfig;
import org.geoserver.cluster.ClusterConfigWatcher;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInitializer;
import org.geotools.util.logging.Logging;

import com.hazelcast.core.HazelcastInstance;

public class HzSynchronizerInitializer implements GeoServerInitializer {
    
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.cluster.hazelcast");
    
    HzCluster cluster;
    
    public HzSynchronizerInitializer() {
    }
    
    public void setCluster(HzCluster cluster) {
        this.cluster = cluster;
    }
    
    
    @Override
    public void initialize(GeoServer geoServer) throws Exception {
        ClusterConfigWatcher configWatcher = loadConfig();
        ClusterConfig config = configWatcher.get();
        
        
        if (!cluster.isEnabled()) {
            LOGGER.info("Hazelcast synchronization disabled");
            return;
        }
        
        HazelcastInstance hz = cluster.getHz();
        
        HzSynchronizer syncher = null;
        
        String method = config.getSyncMethod();
        if ("event".equalsIgnoreCase(method)) {
            syncher = new EventHzSynchronizer(cluster, geoServer);
        }
        else {
            method = "reload"; 
            syncher = new ReloadHzSynchronizer(cluster, geoServer);
        }
        
        syncher.initialize(configWatcher);
        LOGGER.info("Hazelcast synchronizer method is " + method);
    }
    
    ClusterConfigWatcher loadConfig() throws IOException {
        File f = cluster.getConfigFile(HzCluster.CONFIG_FILENAME, HzCluster.class);
        
        return new ClusterConfigWatcher(f);
    }

}
