package org.geotools.ysld.parse;


import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.ysld.YamlMap;
import org.geotools.ysld.YamlObject;

public abstract class FillParser extends YsldParseHandler {
    Fill fill;

    protected FillParser(Factory factory) {
        super(factory);
        fill = factory.style.createFill(null);
    }

    @Override
    public void handle(YamlObject<?> obj, YamlParseContext context) {
        fill(fill);
        YamlMap map = obj.map();
        if (map.has("fill-color")) {
            fill.setColor(Util.color(map.str("fill-color"), factory));
        }
        if (map.has("fill-opacity")) {
            fill.setOpacity(Util.expression(map.str("fill-opacity"), factory));
        }
        context.push("fill-graphic", new GraphicParser(factory) {
            @Override
            protected void graphic(Graphic g) {
                fill.setGraphicFill(g);
            }
        });
    }

    protected abstract void fill(Fill fill);
}