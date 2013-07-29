package org.geoserver.cluster.hazelcast;

public class ReloadHzSynchronizerSendTest extends HzSynchronizerSendTest {

    @Override
    protected HzSynchronizer getSynchronizer() {
        return new ReloadHzSynchronizer(hz, getGeoServer());
    }

}
