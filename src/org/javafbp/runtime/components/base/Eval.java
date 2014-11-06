package org.javafbp.runtime.components.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jpmorrsn.fbp.engine.*;
import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;

import java.util.ArrayList;
import java.util.Map;


/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/19/14.
 */
@ComponentDescription("A component for evaluate groovy")
@InPorts({@InPort(value = "EXPR", description = "Groovy expression/String", type = GString.class)
        , @InPort(value = "PARAMS", description = "Object for parameter/JSON", type = String.class)})
@OutPort(value = "OUT", description = "Output for evaluation result")
@InPortWidgets({
        @InPortWidget(value = "EXPR", widget = InPortWidget.GROOVY_EDIT),
        @InPortWidget(value = "PARAMS", widget = InPortWidget.JS_EDIT)
})
public class Eval extends Component {

    final static Logger logger = Logger.getLogger(Eval.class);
    InputPort exprIn;
    InputPort paramsIn;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p1 = exprIn.receive();
        Packet p2 = paramsIn.receive();
        String expr = String.valueOf(p1.getContent());
        String s = String.valueOf(p2.getContent());
        Object obj;

        if ((s.trim().startsWith("{") && s.trim().endsWith("}"))) {

            obj = new Gson().fromJson(s, new TypeToken<Map<String, Object>>() {
            }.getType());

        } else if ((s.trim().startsWith("[") && s.trim().endsWith("]"))) {

            obj = new Gson().fromJson(s, new TypeToken<ArrayList<Object>>() {
            }.getType());

        } else {
            obj = s;
        }

        try {
            Binding binding = new Binding();
            binding.setVariable("PARAMS", obj);
            GroovyShell shell = new GroovyShell(binding);
            logger.info("Evaluating expression");
            Object value = shell.evaluate(expr);
            if (value == null) value = "NULL";
            outOut.send(create(value));
        } catch (CompilationFailedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            outOut.close();
        }
        exprIn.close();
        paramsIn.close();
        drop(p1);
        drop(p2);
    }


    @Override
    protected void openPorts() {
        exprIn = openInput("EXPR");
        paramsIn = openInput("PARAMS");
        outOut = openOutput("OUT");
    }

}
