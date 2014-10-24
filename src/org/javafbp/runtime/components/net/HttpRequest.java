package org.javafbp.runtime.components.net;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/11/14.
 */
@ComponentDescription("HTTP POST Operation")
@InPorts({@InPort("URL"),@InPort("DATA")})
@OutPort("OUT")
public class HttpRequest extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
