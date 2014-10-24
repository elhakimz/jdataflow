package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/9/14.
 */
@ComponentDescription("Convert any objects to string representation")
@InPort(value = "IN", type = Object.class)
@OutPort(value = "OUT")
public class ToString extends Component {

    InputPort inIn;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
       Packet p=inIn.receive();
       Object o = p.getContent();
       if(o!=null){
           outOut.send(create(o.toString()));
       }else{
           outOut.send(create("null"));
       }
       inIn.close();
       drop(p);
    }

    @Override
    protected void openPorts() {
        inIn=openInput("IN");
        outOut = openOutput("OUT");
    }
}
