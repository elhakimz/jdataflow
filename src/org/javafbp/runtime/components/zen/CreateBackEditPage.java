package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/30/14.
 */
@ComponentDescription("Create Backend Edit Page")
@InPorts({@InPort(value = "TABLE", type = String.class)
        , @InPort(value = "TEMPL", type = String.class)})
@OutPort("OUT")
public class CreateBackEditPage extends Component {
    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
