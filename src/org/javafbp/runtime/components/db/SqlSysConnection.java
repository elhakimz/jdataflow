package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.db.SysDataSource;

import java.sql.Connection;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/29/14.
 */
@ComponentDescription("Get JDBC connection from current application property")
@InPort(value = "IN", optional = true)
@OutPort(value = "CONNS", arrayPort = true, description = "JDBC Connections from pool")

public class SqlSysConnection extends Component {

    private OutputPort[] connOut;
    private InputPort inPort;

    @Override
    protected void execute() throws Exception {
        Packet p = inPort.receive();

        for (OutputPort op : connOut) {
            try {
                Connection conn = SysDataSource.getInstance().getConnection();
                op.send(create(conn));
            } catch (Exception e) {
                e.printStackTrace();
                op.close();
            }
        }

        inPort.close();

        if (p != null) drop(p);


    }

    @Override
    protected void openPorts() {
        connOut = openOutputArray("CONNS");
        inPort = openInput("IN");
    }
}
