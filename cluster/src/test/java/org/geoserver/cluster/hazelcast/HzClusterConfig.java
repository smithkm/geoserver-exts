package org.geoserver.cluster.hazelcast;

import org.geoserver.cluster.ClusterConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigBuilder;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Hazelcast;

public class HzClusterConfig extends ClusterConfig {

    public final static String CLUSTER_NAME_PROP = "hzClusterName"; 
    public final static String CLUSTER_NAME_DEFAULT = "gsCluster"; 
    public final static String CLUSTER_PASSWORD_PROP = "hzClusterPassword"; 
    public final static String CLUSTER_PASSWORD_DEFAULT = "geoserver"; 
    public final static String CLUSTER_INSTANCE_PROP = "hzInstance"; 
    public final static String CLUSTER_INSTANCE_DEFAULT = "gsCluster"; 
    
    ConfigBuilder builder;
    
    public Config getHzConfig() {
        final Config cfg = new Config();
        final String clusterName = getProperty(CLUSTER_NAME_PROP, CLUSTER_NAME_DEFAULT);
        final String clusterPassword = getProperty(CLUSTER_PASSWORD_PROP, CLUSTER_PASSWORD_DEFAULT);
        final String clusterInstance = getProperty(CLUSTER_INSTANCE_PROP, CLUSTER_INSTANCE_DEFAULT);
        
        final GroupConfig groupCfg = new GroupConfig();
        groupCfg.setName(clusterName);
        groupCfg.setPassword(clusterPassword);
        
        cfg.setGroupConfig(groupCfg);
        
        cfg.setInstanceName(clusterInstance);
        
        return cfg;
    }
}
