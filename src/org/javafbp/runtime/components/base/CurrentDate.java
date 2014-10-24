package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/25/14.
 */
@ComponentDescription("Get current date from system")
@OutPort(value = "OUT", type = DateTime.class)
public class CurrentDate extends Component {
    OutputPort outPort;


    @Override
    protected void execute() throws Exception {
        outPort.send(create(new DateTime()));
    }


    @Override
    protected void openPorts() {
      outPort= openOutput("OUT");
    }
}
