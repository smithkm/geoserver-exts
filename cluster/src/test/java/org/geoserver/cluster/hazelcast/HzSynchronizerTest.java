package org.geoserver.cluster.hazelcast;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import org.easymock.Capture;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.cluster.ClusterConfigWatcher;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServer;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Before;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MessageListener;
import static org.easymock.EasyMock.*;
import static org.geoserver.cluster.ConfigChangeEventMatcher.configChangeEvent;

public abstract class HzSynchronizerTest {

protected HazelcastInstance hz;
    final static String TOPIC_NAME = "geoserver.config";
    //protected void setUpSpring(List<String> springContextLocations) {
        // We're going to set up the synchronizer manually so ignore the spring context.
    //}
    @Before
    public void setUp() throws Exception {
        hz = createMock(HazelcastInstance.class);
        topic = createMock(ITopic.class);
        configWatcher = createMock(ClusterConfigWatcher.class);
        captureTopicListener = new Capture<MessageListener<Object>>();

        address = new InetSocketAddress( localAddress(42) , 5000);
        
        Cluster cluster = createMock(Cluster.class);
        Member member = createMock(Member.class);

        expect(hz.getTopic(TOPIC_NAME)).andReturn(topic);
        topic.addMessageListener((MessageListener<Object>)capture(captureTopicListener)); expectLastCall();
        
        expect(cluster.getLocalMember()).andStubReturn(member);
        expect(member.getInetSocketAddress()).andStubReturn(address);
        
        replay(cluster, member);
        
        expect(hz.getCluster()).andStubReturn(cluster);
        
        catalog = createMock(Catalog.class);
        geoServer = createMock(GeoServer.class);
        
        resetGeoServer();
    }

    protected static InetAddress localAddress(int i) throws Exception {
        return InetAddress.getByAddress(new byte[]{(byte) 192,(byte) 168,0,(byte) i});
    }
    
    MessageListener<Object> getListener() {
        return captureTopicListener.getValue();
    }
    
    protected ITopic<Object> topic;
    protected Capture<MessageListener<Object>> captureTopicListener;
    protected ClusterConfigWatcher configWatcher;
    protected InetSocketAddress address;
    protected GeoServer geoServer;
    protected Catalog catalog;
    protected Capture<ConfigurationListener> gsListenerCapture;
    protected Capture<CatalogListener> catListenerCapture;
    
    public HzSynchronizerTest() {
        super();
    }
    
    protected abstract HzSynchronizer getSynchronizer();
    
    protected void initSynchronizer(HzSynchronizer sync) {
        sync.initialize(configWatcher);
    }

    protected GeoServer getGeoServer(){
        return geoServer;
    }
    protected Catalog getCatalog(){
        return catalog;
    }
    
    protected void initGeoServer() {
        expect(geoServer.getCatalog()).andStubReturn(catalog);
        
        gsListenerCapture = new Capture<ConfigurationListener>();
        geoServer.addListener(capture(gsListenerCapture));expectLastCall().atLeastOnce();
        
        catListenerCapture = new Capture<CatalogListener>();
        catalog.addListener(capture(catListenerCapture));expectLastCall().atLeastOnce();
    }
    
    protected void resetGeoServer() {
        reset(geoServer, catalog);
        initGeoServer();
        replay(geoServer, catalog);
    }
}