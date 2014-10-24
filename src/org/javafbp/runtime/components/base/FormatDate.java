package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/25/14.
 */
@ComponentDescription("format received date to in FORMAT")
@InPorts({@InPort(value = "IN", type = DateTime.class, description = "Input date"), @InPort(value = "FMT", type = String.class, description = "standard date format ex(dd-MMM-yyyy)")})
@OutPort(value = "OUT", type = String.class, description = "Formatted date")
public class FormatDate extends Component {

    InputPort inPort;
    InputPort fmtPort;
    OutputPort outPort;

    @Override
    protected void execute() throws Exception {
       Packet inp=inPort.receive();
       Packet fmtp=fmtPort.receive();

        DateTimeFormatter fmt = DateTimeFormat.forPattern((String) fmtp.getContent());
        String s=fmt.print((DateTime)inp.getContent());

        outPort.send(create(s));

        drop(inp);
        drop(fmtp);

       inPort.close();
       fmtPort.close();
    }

    @Override
    protected void openPorts() {

       inPort = openInput("IN");
       fmtPort = openInput("FMT");
       outPort = openOutput("OUT");

    }

}
