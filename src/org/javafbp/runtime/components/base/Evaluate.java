package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/19/14.
 */
@ComponentDescription("A component for evaluate groovy")
@InPorts({@InPort(value = "EXPR",description = "Groovy expression/String",type = String.class)
        , @InPort(value = "PARAMS",description = "Object for parameter/JSON",type = String.class)})
@OutPort(value = "OUT",description = "Output for evaluation result")
public class Evaluate extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
