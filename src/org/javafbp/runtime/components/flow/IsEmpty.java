package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("IN ,boolean true OUT if success, false if no")
@InPort("IN")
@OutPort(value = "OUT", type = Boolean.class)
public class IsEmpty extends Component {
    InputPort inIn;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p = inIn.receive();
        Object o = p.getContent();

        if(o instanceof String && !((String) o).isEmpty()
           || o != null ){
            outOut.send(create(Boolean.TRUE));
        }else{
           outOut.send(create(Boolean.FALSE));
        }

        inIn.close();
        outOut.close();
        drop(p);
    }

    @Override
    protected void openPorts() {
       inIn = openInput("IN");
       outOut = openOutput("OUT");
    }
}
