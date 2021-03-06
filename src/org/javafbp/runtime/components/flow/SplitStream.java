package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */

/**
 * Component to split an input stream into multiple output streams,
 * where the first 30 packets go to the first output port, the next 30 go
 * to the second and so on.  Each output stream is closed before data starts
 * being sent to the next.  This component was used for testing deadlock behaviour.
 */
@ComponentDescription("Drop and copy a stream into multiple output streams")
@OutPort(value = "OUT", arrayPort = true, description = "multiple IN packet to OUT")
@InPort(value = "IN", description = "single IN packet")
public class SplitStream extends Component {

    private InputPort inport;

    private OutputPort[] outportArray;

    @Override
    protected void execute() {

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
