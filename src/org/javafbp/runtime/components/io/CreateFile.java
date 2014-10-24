package org.javafbp.runtime.components.io;

import com.jpmorrsn.fbp.engine.*;
import org.javafbp.runtime.pattern.InPortWidget;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/22/14.
 */
/**
 * Component to write data to a file, using a stream of
 * packets. The file name is specified as a string via an
 * InitializationConnection. It is specified as "must run" so that the output
 * file will be cleared even if no data packets are input, and it will start at beginning of run.
 * This component converts Unicode to the specified format (if one is specified).
 */
@ComponentDescription("Writes a stream of packets to an I/O file")
@InPorts({
        @InPort(value = "IN", description = "Packets to be written", type = String.class),
        @InPort(value = "DESTINATION", description = "File name and optional format, separated by a comma", type = String.class) })
// filename [, format ]
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
@InPortWidget(value="DESTINATION",widget = InPortWidget.SELECT_DIRECTORY)
public class CreateFile extends Component {

    static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
            + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
            + "based on the Everything Development Company's Artistic License.  A document describing "
            + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
            + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

    private InputPort inport;

    private InputPort destination;

    private static String linesep = System.getProperty("line.separator");

    private final double _timeout = 10.0; // 10 secs

    private OutputPort outport;

    @Override
    protected void execute() {
        Packet dp = destination.receive();
        if (dp == null) {
            return;
        }
       // destination.close();

        String sf = (String) dp.getContent();
        String format = null;
        int i = sf.indexOf(",");
        if (i != -1) {
            format = sf.substring(i + 1);
            format = format.trim();
            sf = sf.substring(0, i);
        }
        try {
            Writer w = null;
            FileOutputStream fos = new FileOutputStream(sf);
            if (format == null) {
                w = new OutputStreamWriter(fos);
            } else {
                w = new OutputStreamWriter(fos, format);
            }

            drop(dp);
            Packet p;

            while ((p = inport.receive()) != null) {
                longWaitStart(_timeout);

                try {
                    w.write((String) p.getContent());
                    //System.out.println("WT" + p.getContent());
                } catch (IOException e) {
                    System.err.println(e.getMessage() + " - component: " + this.getName());
                }
                w.write(linesep);
                longWaitEnd();

                //if (fp != null)
                w.flush();
                if (outport.isConnected()) {
                    outport.send(p);
                } else {
                    drop(p);
                }
            }
            w.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " - file: " + sf + " - component: " + this.getName());
        }

    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");
        destination = openInput("DESTINATION");
        outport = openOutput("OUT");

    }
}
