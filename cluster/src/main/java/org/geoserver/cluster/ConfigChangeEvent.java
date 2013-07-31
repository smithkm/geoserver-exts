package org.geoserver.cluster;

import java.util.HashMap;
import java.util.Map;

import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.AttributionInfo;
import org.geoserver.catalog.AuthorityURLInfo;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.Info;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerIdentifierInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.AttributeTypeInfoImpl;
import org.geoserver.catalog.impl.AttributionInfoImpl;
import org.geoserver.catalog.impl.AuthorityURL;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.CoverageDimensionImpl;
import org.geoserver.catalog.impl.CoverageInfoImpl;
import org.geoserver.catalog.impl.CoverageStoreInfoImpl;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.LayerGroupInfoImpl;
import org.geoserver.catalog.impl.LayerIdentifier;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.catalog.impl.MetadataLinkInfoImpl;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.StyleInfoImpl;
import org.geoserver.catalog.impl.WMSLayerInfoImpl;
import org.geoserver.catalog.impl.WMSStoreInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.CoverageAccessInfo;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.JAIInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.config.impl.ContactInfoImpl;
import org.geoserver.config.impl.CoverageAccessInfoImpl;
import org.geoserver.config.impl.GeoServerInfoImpl;
import org.geoserver.config.impl.JAIInfoImpl;
import org.geoserver.config.impl.LoggingInfoImpl;
import org.geoserver.config.impl.SettingsInfoImpl;

/**
 * Event for 
 * @author Justin Deoliveira, OpenGeo
 *
 */
public class ConfigChangeEvent<T extends Info> extends Event {

    static Map<Class,Class> INTERFACES = new HashMap<Class,Class>();
    static {
        INTERFACES.put(GeoServerInfoImpl.class, GeoServerInfo.class);
        INTERFACES.put(SettingsInfoImpl.class, SettingsInfo.class);
        INTERFACES.put(LoggingInfoImpl.class, LoggingInfo.class);
        INTERFACES.put(JAIInfoImpl.class, JAIInfo.class);
        INTERFACES.put(CoverageAccessInfoImpl.class, CoverageAccessInfo.class);
        INTERFACES.put(ContactInfoImpl.class, ContactInfo.class);
        INTERFACES.put(AttributionInfoImpl.class, AttributionInfo.class);
        
        //catalog
        INTERFACES.put(CatalogImpl.class, Catalog.class);
        INTERFACES.put(NamespaceInfoImpl.class, NamespaceInfo.class);
        INTERFACES.put(WorkspaceInfoImpl.class, WorkspaceInfo.class);
        INTERFACES.put(DataStoreInfoImpl.class, DataStoreInfo.class);
        INTERFACES.put(WMSStoreInfoImpl.class, WMSStoreInfo.class);
        INTERFACES.put(CoverageStoreInfoImpl.class, CoverageStoreInfo.class);
        INTERFACES.put(StyleInfoImpl.class, StyleInfo.class);
        INTERFACES.put(FeatureTypeInfoImpl.class, FeatureTypeInfo.class );
        INTERFACES.put(CoverageInfoImpl.class, CoverageInfo.class);
        INTERFACES.put(WMSLayerInfoImpl.class, WMSLayerInfo.class);
        INTERFACES.put(CoverageDimensionImpl.class, CoverageDimensionInfo.class);
        INTERFACES.put(MetadataLinkInfoImpl.class, MetadataLinkInfo.class);
        INTERFACES.put(AttributeTypeInfoImpl.class, AttributeTypeInfo.class );
        INTERFACES.put(LayerInfoImpl.class, LayerInfo.class);
        INTERFACES.put(LayerGroupInfoImpl.class, LayerGroupInfo.class );
        INTERFACES.put(LayerIdentifier.class, LayerIdentifierInfo.class );
        INTERFACES.put(AuthorityURL.class, AuthorityURLInfo.class );
    }
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    public enum Type {
        ADD, REMOVE, MODIFY
    }

    /**
     * id of object
     */
    String id;

    /**
     * name of object
     */
    String name;

    /**
     * name of workspace qualifying the object
     */
    String workspaceId;

    /**
     * class of object
     */
    Class<T> clazz;

    /**
     * type of config change
     */
    Type type;

    public ConfigChangeEvent(String id, String name, Class<T> clazz, Type type) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
        this.type = type;
    }

    public String getObjectId() {
        return id;
    }

    public String getObjectName() {
        return name;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Class<T> getObjectClass() {
        return clazz;
    }

    public Class<T> getObjectInterface() {
        Class clazz = INTERFACES.get(getObjectClass());
        
        // Fall back, mostly here to support EasyMock test objects in unit tests.
        if(clazz==null) {
            for(Class realClazz: INTERFACES.values()) {
                if(realClazz.isAssignableFrom(getObjectClass())) {
                    clazz=realClazz;
                    break;
                }
            }
        }
        
        return clazz;
    }

    public Type getChangeType() {
        return type;
    }
}
