package org.javafbp.runtime.components.io

import com.jpmorrsn.fbp.engine.*

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/23/14.
 */
@ComponentDescription("Writes a stream of packets to an I/O file")
@InPorts([
        @InPort(value = "IN", description = "Packets to be written", type = String.class),
        @InPort(value = "DESTINATION", description = "File name and optional format, separated by a comma", type = String.class)])
// filename [, format ]
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
class CreateTextFile extends Component {
    private InputPort inport;

    private InputPort destination;

    private static String linesep = System.getProperty("line.separator");

    private final double _timeout = 10.0; // 10 secs

    private OutputPort outport;

    @Override
    protected void execute() throws Exception {
        Packet p1 = destination.receive()
        Packet p2 = inport.receive()

        String dest = p1.content
        String content = p2.content
        new File(dest).write(content)
        outport.send(create(content))

        inport.close()
        destination.close()
        drop(p1)
        drop(p2)

    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");
        destination = openInput("DESTINATION");
        outport = openOutput("OUT");
    }
}
