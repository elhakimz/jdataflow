package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.*;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.javafbp.runtime.pattern.InPortWidget;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("Accept a IN stream if true onto OUT also output to OUT-ELSE")
@InPorts({@InPort("IN"), @InPort(value = "CONDITION", type = String.class, description = "Groovy expression with $IN as parameter")})
@OutPorts({@OutPort(value = "YES", description = "Output of condition"), @OutPort(value = "NO", description = "Output of condition when no")})
@InPortWidget(value = "CONDITION", widget = "groovy-edit")
public class IfElse extends Component {
    InputPort inIn;
    InputPort inCond;
    OutputPort outYes;
    OutputPort outNo;

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
            outNo.close();
        } else {
            outNo.send(create(obj));
            outYes.close();
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
        outNo = openOutput("NO");
    }
}
