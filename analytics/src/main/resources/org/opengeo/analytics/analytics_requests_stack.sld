<?xml version="1.0" encoding="ISO-8859-1"?>
  <StyledLayerDescriptor version="1.0.0"
   xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
   xmlns="http://www.opengis.net/sld"
   xmlns:ogc="http://www.opengis.net/ogc"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
      <Name>request_origin_pointstack</Name>
      <UserStyle>
      <!-- Styles can have names, titles and abstracts -->
        <Title>Stacked Point</Title>
        <Abstract></Abstract>
        <FeatureTypeStyle>
          <Transformation>
            <ogc:Function name="gs:PointStacker">
              <ogc:Function name="parameter">
                <ogc:Literal>data</ogc:Literal>
              </ogc:Function>
              <ogc:Function name="parameter">
                <ogc:Literal>cellSize</ogc:Literal>
                <ogc:Literal>10</ogc:Literal>
              </ogc:Function>
              <ogc:Function name="parameter">
                <ogc:Literal>stretch</ogc:Literal>
                <ogc:Literal>true</ogc:Literal>
              </ogc:Function>
              <ogc:Function name="parameter">
                <ogc:Literal>outputBBOX</ogc:Literal>
                <ogc:Function name="env">
               <ogc:Literal>wms_bbox</ogc:Literal>
                </ogc:Function>
              </ogc:Function>
              <ogc:Function name="parameter">
                <ogc:Literal>outputWidth</ogc:Literal>
                <ogc:Function name="env">
               <ogc:Literal>wms_width</ogc:Literal>
                </ogc:Function>
              </ogc:Function>
              <ogc:Function name="parameter">
                <ogc:Literal>outputHeight</ogc:Literal>
                <ogc:Function name="env">
                  <ogc:Literal>wms_height</ogc:Literal>
                </ogc:Function>
              </ogc:Function>
            </ogc:Function>
          </Transformation>
          
                  <Rule>
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Stroke>
                  <CssParameter name="stroke">#00AA55</CssParameter>
                  <CssParameter name="stroke-width">1.5</CssParameter>
                </Stroke>
                <Fill>
                  <CssParameter name="fill">#00EE77</CssParameter>
                  <CssParameter name="fill-opacity">0.75</CssParameter>
                </Fill>
              </Mark>
              <Size>
                <ogc:Add>
                  <ogc:Mul>
                    <ogc:PropertyName>proportion</ogc:PropertyName>
                    <ogc:Literal>10</ogc:Literal>
                  </ogc:Mul>
                  <ogc:Literal>5</ogc:Literal>
                </ogc:Add>
              </Size>
            </Graphic>
          </PointSymbolizer>
          </Rule>
       </FeatureTypeStyle>
      </UserStyle>
    </NamedLayer>
  </StyledLayerDescriptor>