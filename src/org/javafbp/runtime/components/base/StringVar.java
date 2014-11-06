package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;
import org.javafbp.runtime.pattern.InPortWidget;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/2/14.
 */
@ComponentDescription("Create a Date variable")
@OutPort(value = "OUT", arrayPort = true, description = "multiple IN packet to OUT")
@InPort(value = "IN", description = "single IN packet", type = String.class)
@InPortWidget(value = "IN", widget = InPortWidget.JS_EDIT)
public class StringVar extends Component {

    private InputPort inport;

    private OutputPort[] outportArray;

    @Override
    protected void execute() throws Exception {

        Packet p = inport.receive();
        String o = String.valueOf(p.getContent());

        for (OutputPort anOutportArray : outportArray) {

            if (anOutportArray.isConnected()) {
                anOutportArray.send(create(o));
            }
        }
        drop(p);
    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");
        outportArray = openOutputArray("OUT");
    }
}
