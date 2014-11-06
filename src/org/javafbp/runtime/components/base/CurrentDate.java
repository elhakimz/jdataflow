package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/25/14.
 */
@ComponentDescription("Get current date from system")
@OutPort(value = "OUT", type = DateTime.class)
public class CurrentDate extends Component {
    final static Logger logger = Logger.getLogger(CurrentDate.class);
    OutputPort outPort;

    @Override
    protected void execute() throws Exception {
        outPort.send(create(new DateTime()));
    }


    @Override
    protected void openPorts() {
        outPort = openOutput("OUT");
    }
}
