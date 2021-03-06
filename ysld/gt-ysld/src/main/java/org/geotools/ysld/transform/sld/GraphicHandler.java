package org.geotools.ysld.transform.sld;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

public class GraphicHandler extends SldTransformHandler  {
    boolean inSymbols = false;

    @Override
    public void element(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
        String name = xml.getLocalName();
        if ("Graphic".equals(name)) {
            context.mapping();
        }
        else if ("Mark".equals(name) || "ExternalGraphic".equals(name)) {
            context.scalar("symbols").sequence().push(new SymbolsHandler());
        }
        else if ("Size".equals(name) ||
            "Opacity".equals(name) ||
            "Rotation".equals(name)) {
            context.scalar(name.toLowerCase()).push(new ExpressionHandler());
        }
        else if ("Displacment".equals(name) || "AnchorPoint".equals(name)) {
            //context.scalar(name.toLowerCase()).push(new TupleHandler());
        }
    }

    @Override
    public void endElement(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
        String name = xml.getLocalName();
        if ("Graphic".equals(name)) {
            context.endMapping().pop();
        }

    }

    static class SymbolsHandler extends SldTransformHandler {
        @Override
        public void element(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
            String name = xml.getLocalName();
            if ("Mark".equals(name)) {
                context.push(new MarkHandler());
            }
            else if ("ExternalGraphic".equals(name)) {
                context.push(new ExternalHandler());
            }
            else {
                context.endSequence().pop();
            }
        }
    }

    static class MarkHandler extends SldTransformHandler {
        @Override
        public void element(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
            String name = xml.getLocalName();
            if ("Mark".equals(name)) {
                context.mapping().scalar("mark").mapping();
            }
            else if ("WellKnownName".equals(name)) {
                context.scalar("shape").push(new ExpressionHandler());
            }
            else if ("Stroke".equals(name)) {
                context.push(new StrokeHandler());
            }
            else if ("Fill".equals(name)) {
                context.push(new FillHandler());
            }
            //TODO: se:OnlineResource
            //TODO: se:InlineContent
        }

        @Override
        public void endElement(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
            if ("Mark".equals(xml.getLocalName())) {
                context.endMapping().endMapping().pop();
            }
        }
    }

    static class ExternalHandler extends SldTransformHandler {
        @Override
        public void element(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
            String name = xml.getLocalName();
            if ("ExternalGraphic".equals(name)) {
                context.mapping().scalar("external").mapping();
            }
            else if ("OnlineResource".equals(name)) {
                context.scalar("url").scalar(xml.getAttributeValue(null, "href"));
            }
            else if ("Format".equals(name)) {
                context.scalar("format").scalar(xml.getElementText());
            }
        }

        @Override
        public void endElement(XMLStreamReader xml, SldTransformContext context) throws XMLStreamException, IOException {
            if ("ExternalGraphic".equals(xml.getLocalName())) {
                context.endMapping().endMapping().pop();
            }
        }
    }
}
