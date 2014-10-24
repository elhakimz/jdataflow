package org.javafbp.runtime.components.flow

import com.jpmorrsn.fbp.engine.*

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/28/14.
 */
@ComponentDescription("Send packet if OPEN is true")
@InPorts([@InPort(value = "IN", description ="IN data" )
        ,@InPort(value = "OPEN", type = Boolean.class, description = "OPEN as boolean")])
@OutPort("OUT")
class Gate extends Component {

    InputPort inIn;
    InputPort inOpen;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive()
        Packet p2 = inOpen.receive();

        boolean sent=false
        if(Boolean.valueOf(p2.content as boolean)){
            outOut.send(p1);
            sent=true
        }

        inIn.close();
        inOpen.close();
        drop(p2)
        if(!sent) drop(p1)

    }

    @Override
    protected void openPorts() {
      inIn = openInput "IN"
      inOpen = openInput "OPEN"
      outOut = openOutput "OUT"
    }
}
