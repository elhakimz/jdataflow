package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/19/14.
 */
@ComponentDescription("A component for running groovy script residing in SYSTEM/scripts")
@InPorts({@InPort("SCRIPT"), @InPort("PARAMETERS")})
@OutPort("OUT")
public class RunScript extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
