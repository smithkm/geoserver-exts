package org.geotools.ysld.encode;

import static org.geotools.ysld.ProcessUtil.loadProcessFunctionFactory;
import static org.geotools.ysld.ProcessUtil.loadProcessInfo;
import static org.geotools.ysld.ProcessUtil.processName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.Parameter;
import org.geotools.process.function.ProcessFunction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

public class TransformEncoder extends YsldEncodeHandler<Expression> {
    boolean chained;
    
    public TransformEncoder(Expression tx) {
        this(tx, false);
    }
    
    public TransformEncoder(Expression tx, boolean chained) {
        super(tx);
        this.chained=chained;
    }
    
    @Override
    protected void encode(Expression tx) {
        if (loadProcessFunctionFactory() == null) {
            FeatureStyleEncoder.LOG.warning("Skipping transform, unable to load process factory, ensure process modules installed");
            return;
        }

        if (!(tx instanceof Function)) {
            FeatureStyleEncoder.LOG.warning("Skipping transform, expected a function but got: " + tx);
            return;
        }

        Function ftx = (Function)tx;
        Map<String,Parameter> paramInfo = loadProcessInfo(processName(ftx.getName()));

        if (paramInfo == null) {
            FeatureStyleEncoder.LOG.warning("Skipping transform, unable to locate process named: " + ftx.getName());
            return;
        }

        put("name", ftx.getName());

        Map<String,Object> simpleParams = new LinkedHashMap<String, Object>();
        String input=null;
        for (Expression expr : ftx.getParameters()) {
            if (!(expr instanceof Function)) {
                FeatureStyleEncoder.LOG.warning("Skipping parameter, expected a function but got: " + expr);
                continue;
            }
            
            Function fexpr = (Function) expr;
            if (fexpr.getParameters().size() < 1) {
                FeatureStyleEncoder.LOG.warning("Skipping parameter, must have at least one value");
                continue;
            }
            
            String paramName = fexpr.getParameters().get(0).evaluate(null, String.class);
            
            final Object paramValue;
            if (fexpr.getParameters().size()==1){
                // TODO: handle multiple input parameters.
                input = paramName;
                continue; // It's an input parameter so don't include it in the regular parameter list
            } else if(fexpr.getParameters().size()==2){
                paramValue = intermediateExpression(fexpr.getParameters().get(1));
            } else {
                List<Object> l = new ArrayList<Object>();
                for (int i = 1; i < fexpr.getParameters().size(); i++) {
                    l.add(intermediateExpression(fexpr.getParameters().get(i)));
                }
                paramValue = l;
            }
            
            simpleParams.put(paramName, paramValue);
        }
        
        if(input!=null && (chained || !input.equals("data"))) {
            put("input", input);
        }
        
        push("params").inline(simpleParams);
    }
    
    Object intermediateExpression(Expression e) {
        if(e instanceof ProcessFunction) {
            chained=true;
            TransformEncoder enc = new TransformEncoder(e, true);
            enc.reset();
            enc.encode(e);
            return enc.root();
        } else {
            return toObjOrNull(e);
        }
    }
    
}