package org.geoserver.cluster.hazelcast;

import java.io.File;

import org.geoserver.cluster.ClusterConfig;
import org.geoserver.cluster.ClusterConfigWatcher;

public class HzClusterConfigWatcher extends ClusterConfigWatcher {

    public HzClusterConfigWatcher(File file) {
        super(file);
    }

    @Override
    protected ClusterConfig getNewClusterConfig() {
        return new HzClusterConfig();
    }
}
