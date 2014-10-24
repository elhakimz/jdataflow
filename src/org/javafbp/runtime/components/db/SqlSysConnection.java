package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import org.hakim.fbp.db.SysDataSource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/29/14.
 */
@ComponentDescription("Get JDBC connection from current application property")
@OutPort(value = "CONNS", arrayPort = true, description = "JDBC Connections from pool")
public class SqlSysConnection extends Component {

    private OutputPort[] connOut;

    @Override
    protected void execute() throws Exception {
        for(OutputPort op:connOut){
            try {
                Connection conn = SysDataSource.getInstance().getConnection();
                op.send(create(conn));
            } catch (SQLException | IOException | PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void openPorts() {
        connOut = openOutputArray("CONNS");
    }
}
