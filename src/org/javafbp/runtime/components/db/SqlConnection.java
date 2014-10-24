package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/18/14.
 */
@ComponentDescription("JDBC Connection, get a JDBC connection from URL, Connection object will be sent to CONN")
@OutPort(value = "CONNS", type = java.sql.Connection.class, arrayPort = true, description = "single or multiple JDBC Connection")
@InPorts({@InPort(value = "URL", type = String.class, description = "JDBC url"),
        @InPort(value = "USER", type = String.class, description = "JDBC User name"),
        @InPort(value = "PASSWORD", type = String.class, description = "JDBC password"),
        @InPort(value = "DRIVER", type = String.class, description = "JDBC driver class name")})

public class SqlConnection extends Component {
    static final String copyright = "...";

    private InputPort urlIn;
    private InputPort userIn;
    private InputPort passwdIn;
    private InputPort driverIn;

    private OutputPort[] connOut;


    @Override

    protected void openPorts() {
        urlIn = openInput("URL");
        userIn = openInput("USER");
        passwdIn = openInput("PASSWORD");
        driverIn = openInput("DRIVER");
        connOut = openOutputArray("CONNS");

    }

    @Override
    protected void execute() {
       /* execute logic */
        Packet url = urlIn.receive();
        Packet user = userIn.receive();
        Packet passwd = passwdIn.receive();
        Packet driver = driverIn.receive();

        //check driver
        String fail = "";
        Class clsDriver=null;

        try {
            clsDriver = Class.forName((String) driver.getContent());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if(clsDriver!=null ){
            for(OutputPort op:connOut){
                java.sql.Connection conn=null;
                try {
                    conn = DriverManager.getConnection((String) url.getContent()
                            , (String) user.getContent()
                            , (String) passwd.getContent());
                    op.send(create(conn));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        //send to out
        //drop packets
        drop(url);
        drop(user);
        drop(passwd);
        drop(driver);

        //close in
        urlIn.close();
        userIn.close();
        passwdIn.close();
        driverIn.close();

    }

}
