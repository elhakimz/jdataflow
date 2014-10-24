package org.javafbp.runtime.components.os;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/9/14.
 */
@ComponentDescription("Execute Shell command")
@InPort(value = "CMD",description = "Command to be executed")
@OutPort(value = "OUT",description = "Output of execution")
public class Exec extends Component{

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
