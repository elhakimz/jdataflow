package org.javafbp.runtime.components.flow;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;

/**
 * Purpose:
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("Merge a streams of packets onto a list object")
@InPort(value = "IN", arrayPort = true)
@OutPort("OUT")
public class MergeStream extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }

}
