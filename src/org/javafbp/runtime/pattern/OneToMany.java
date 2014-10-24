package org.javafbp.runtime.pattern;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@InPort("IN")
@OutPort(value = "OUT", arrayPort = true)
public class OneToMany extends Component {
    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
