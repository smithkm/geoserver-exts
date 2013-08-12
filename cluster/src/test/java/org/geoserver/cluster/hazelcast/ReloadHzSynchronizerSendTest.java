package org.geoserver.cluster.hazelcast;

import java.util.concurrent.ScheduledExecutorService;

public class ReloadHzSynchronizerSendTest extends HzSynchronizerSendTest {

    @Override
    protected HzSynchronizer getSynchronizer() {
        return new ReloadHzSynchronizer(cluster, getGeoServer()) {

            @Override
            ScheduledExecutorService getNewExecutor() {
                return getMockExecutor();
            }
            
        };
    }

}
