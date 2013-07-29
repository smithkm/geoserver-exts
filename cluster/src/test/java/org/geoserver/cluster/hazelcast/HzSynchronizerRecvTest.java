package org.geoserver.cluster.hazelcast;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.geoserver.cluster.ConfigChangeEventMatcher.configChangeEvent;
import static org.hamcrest.CoreMatchers.*;

import org.geoserver.catalog.LayerInfo;
import org.junit.Test;


public abstract class HzSynchronizerRecvTest extends HzSynchronizerTest {
@Test
public void testDisableLayer() throws Exception {
    LayerInfo info;
    final String layerName = "testLayer";
    final String layerId = "Layer-TEST";
    final String layerWorkspace = null; // LayerInfo doesn't have a workspace property
    
    reset(catalog, geoServer);
    {
        initGeoServer();
        info = createMock(LayerInfo.class);

        expect(info.getName()).andStubReturn(layerName);
        expect(info.getId()).andStubReturn(layerId);
        
    }
    replay(hz, topic, configWatcher, catalog, geoServer, info);
    {
        HzSynchronizer sync = getSynchronizer();
        

    }
    verify(hz, topic, configWatcher, info, catalog, geoServer);
}

}
