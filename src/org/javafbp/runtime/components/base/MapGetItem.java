package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/4/14.
 */
@ComponentDescription("Get Item from a Map")
@InPorts({@InPort(value = "IN", type = Map.class),@InPort(value = "KEY", type = String.class)})
@OutPort(value = "OUT", type = Object.class)
public class MapGetItem extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
