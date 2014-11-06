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
        , @InPort(value = "CONDITION", type = String.class, description = " expression with $IN as parameter")})
@OutPort("YES")
@InPortWidgets({@InPortWidget(value = "CONDITION", widget = "groovy-edit")}) //TODO
public class IfTrue extends Component {

    InputPort inIn;
    InputPort inCond;
    OutputPort outYes;

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive();
        Packet p2 = inCond.receive();
        Object obj = p1.getContent();
        String cond = (String) p2.getContent();

        Binding binding = new Binding();
        binding.setVariable("IN", obj);
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate(cond);

        if (value == true) {
            outYes.send(create(obj));
        } else {
            outYes.close();
            // terminate("TERMINATED");
        }

        inIn.close();
        inCond.close();
        drop(p1);
        drop(p2);
    }

    @Override
    protected void openPorts() {
        inIn = openInput("IN");
        inCond = openInput("CONDITION");
        outYes = openOutput("YES");

    }
}
