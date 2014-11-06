package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;
import org.apache.log4j.Logger;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/5/14.
 */
@ComponentDescription("Write stream of packets to console")
@InPort(value = "IN", description = "Packets to be displayed", type = Object.class)
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class PrintConsole extends Component {
    final static Logger logger = Logger.getLogger(PrintConsole.class);
    private final double _timeout = 5.0; // 5 secs
    private InputPort inport;
    private OutputPort outport;

    @Override
    protected void execute() {
        Packet p;

        while ((p = inport.receive()) != null) {
            longWaitStart(_timeout);
            // sleep(5000L); //force timeout - testing only
            if (p.getType() == Packet.OPEN) {
                logger.info("===> Open Bracket");
            } else if (p.getType() == Packet.CLOSE) {
                logger.info("===> Close Bracket");
            } else {
                System.out.println(String.valueOf(p.getContent()));
            }
            longWaitEnd();
            if (outport.isConnected()) {
                outport.send(p);
            } else {
                drop(p);
            }
        }

    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");

        outport = openOutput("OUT");

    }
}