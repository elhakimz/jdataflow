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
@ComponentDescription("Get Zen Model Description, NAME is model name, TYPE is model type, APP is application name," +
        " OUT is the object for descriptor")
@InPorts({@InPort(value = "NAME" , type = String.class,description = "Model name")
        , @InPort(value = "TYPE", type = String.class,description = "Model type VIEW/SERVICE/MODEL")
        ,@InPort(value = "APPDESC", description = "Application description")
        , @InPort(value = "CONN",type = java.sql.Connection.class, description = "JDBC connection")})
@OutPorts({@OutPort("OUT"),@OutPort(value = "CONN",type = Connection.class)})
public class GetModelDescription extends Component{

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
