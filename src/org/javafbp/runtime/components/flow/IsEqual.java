package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("IN1 and IN2 component will be compared, IN1 OUT if success")
@InPorts({@InPort("IN1"),@InPort("IN2")})
@OutPort(value = "OUT")
public class IsEqual extends Component {

    InputPort inIn1;
    InputPort inIn2;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p1= inIn1.receive();
        Packet p2=inIn2.receive();
        Object o1 = p1.getContent();
        Object o2=p2.getContent();

        if(o1.equals(o2)){
            outOut.send(p1);
        } else {
            outOut.close(); //drop conn
        }

        inIn1.close();
        inIn2.close();
        drop(p1);
        drop(p2);
    }

    @Override
    protected void openPorts() {
       inIn1= openInput("IN1");
       inIn2 = openInput("IN2");
       outOut = openOutput("OUT");

    }
}
