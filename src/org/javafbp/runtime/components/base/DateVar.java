package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

import java.util.Date;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/2/14.
 */
@ComponentDescription("Create a Date variable")
@OutPort(value = "OUT", arrayPort = true, description = "multiple IN packet to OUT")
@InPort(value = "IN", description = "single IN packet", type = Date.class)
public class DateVar extends Component {

    private InputPort inport;

    private OutputPort[] outportArray;

    @Override
    protected void execute() throws Exception {

        Packet p = inport.receive();
        Object o = p.getContent();

        for (OutputPort anOutportArray : outportArray) {
            anOutportArray.send(create(o));
        }

        drop(p);

    }

    @Override
    protected void openPorts() {

        inport = openInput("IN");
        outportArray = openOutputArray("OUT");

    }
}
