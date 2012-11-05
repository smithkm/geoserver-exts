    map = new OpenLayers.Map({
        div: "${markupId}",
        projection: new OpenLayers.Projection("EPSG:900913"),
        displayProjection: new OpenLayers.Projection("EPSG:4326"),
        units: "m",
        numZoomLevels: 18,
        maxResolution: 156543.0339,
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508.34),
        controls: [
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.Navigation({zoomWheelEnabled:false}),
            new OpenLayers.Control.PanZoomBar(),
            new OpenLayers.Control.KeyboardDefaults()
        ]
    });

        
    var osm = new OpenLayers.Layer.OSM();
    var wmsHeat = new OpenLayers.Layer.WMS(
        "Request Origins Heatmap", "../wms",
        {'layers': 'analytics:requests', 'styles': 'request_origin_heatmap', 'format':'image/png', 'transparent':true, 'viewparams':"query:${query}"},
        {
            'isBaseLayer': false,
            'singleTile': true
        }
    );
    var wms = new OpenLayers.Layer.WMS(
        "Request Origins", "../wms",
        {'layers': 'analytics:requests', 'styles': 'request_origin_pointstack', 'format':'image/png', 'transparent':true, 'viewparams':"query:${query}"},
        {
            'isBaseLayer': false,
            'singleTile': true
        }
    );
    var wmsAgg = new OpenLayers.Layer.WMS(
        "Requests Agg", "../wms",
        {'layers': 'analytics:requests_agg', 'format':'image/png', 'transparent':true, 'viewparams':"query:${query}"},
        {
            'isBaseLayer': false,
            'singleTile': true
        }
    );
    var wmsBox = new OpenLayers.Layer.WMS(
        "Request Bounds", "../wms",
        {'layers': 'analytics:requests_box', 'format':'image/png', 'transparent':true, 'viewparams':"query:${query}"},
        {
            'isBaseLayer': false,
            'singleTile': true
        }
    );
    
    map.addLayers([osm, wmsBox, wmsHeat, wmsAgg, wms]);
    map.zoomToMaxExtent();
    