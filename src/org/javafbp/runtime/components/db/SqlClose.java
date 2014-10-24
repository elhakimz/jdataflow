package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.components.Output;
import com.jpmorrsn.fbp.engine.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/29/14.
 */
@ComponentDescription("Close JDBC connection")
@InPort(value = "CONN",type = Connection.class , description = "JDBC Connection to be closed")
public class SqlClose extends Component {

    InputPort inpConn;


    @Override
    protected void execute() throws Exception {
        Packet p = inpConn.receive();
        Connection conn = (Connection) p.getContent();

        if(!conn.isClosed()){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        inpConn.close();
        drop(p);

    }

    @Override
    protected void openPorts() {
        inpConn = openInput("CONN");
    }

}
