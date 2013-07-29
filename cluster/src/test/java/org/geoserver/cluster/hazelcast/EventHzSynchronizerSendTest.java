package org.geoserver.cluster.hazelcast;

public class EventHzSynchronizerSendTest extends HzSynchronizerSendTest {

    @Override
    protected HzSynchronizer getSynchronizer() {
        return new EventHzSynchronizer(hz, getGeoServer());
    }

}
