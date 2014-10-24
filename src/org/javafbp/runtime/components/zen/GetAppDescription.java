package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;

import java.sql.*;
import java.sql.Connection;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/19/14.
 */
@ComponentDescription("Get Application description")
@InPorts({@InPort("NAME"),@InPort(value = "CONN",type = java.sql.Connection.class)})
@OutPorts({@OutPort("OUT"),@OutPort(value = "CONN",type = Connection.class)})
public class GetAppDescription extends Component {
    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
