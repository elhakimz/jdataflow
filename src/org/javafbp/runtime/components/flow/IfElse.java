package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.*;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("Accept a IN stream if true")
@InPorts({@InPort("IN")
        , @InPort("IN2")
        , @InPort(value = "CONDITION", type = String.class, description = " expression with $IN as parameter")
})
@OutPort("OUT")
@InPortWidgets({@InPortWidget(value = "CONDITION", widget = "groovy-edit")}) //TODO
public class IfElse extends Component {
    InputPort inIn;
    InputPort inIn2;
    InputPort inCond;
    OutputPort outYes;

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive();
        Packet p12 = inIn2.receive();
        Packet p2 = inCond.receive();
        Object obj = p1.getContent();
        Object obj2 = p12.getContent();
        String cond = (String) p2.getContent();

        Binding binding = new Binding();
        binding.setVariable("IN", obj);
        binding.setVariable("IN2", obj2);

        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate(cond);

        if (value == true) {
            outYes.send(create(obj));
        } else {
            outYes.send(create(obj2));
        }

        inIn.close();
        inCond.close();
        drop(p1);
        drop(p2);
    }

    @Override
    protected void openPorts() {
        inIn = openInput("IN");
        inIn2 = openInput("IN2");
        inCond = openInput("CONDITION");
        outYes = openOutput("YES");

    }
}
