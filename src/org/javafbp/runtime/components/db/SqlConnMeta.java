package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;
import org.json.JSONObject;

import java.sql.Connection;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/28/14.
 */
@ComponentDescription("JDBC Connection metadata")
@InPort(value = "CONN",type = Connection.class)
@OutPort(value = "OUT", type = String.class)
public class SqlConnMeta extends Component{

    InputPort connIn;
    OutputPort outPort;
    @Override
    protected void execute() throws Exception {
      Packet p = connIn.receive();
      Connection conn = (Connection) p.getContent();

      JSONObject json = new JSONObject();
      json.put("catalog",conn.getCatalog());

      if(conn.getMetaData()!=null){
          System.out.println("[JdbcConnMeta] getting metadata..");
      }

      outPort.send(create(json.toString(1)));
      connIn.close();
      drop(p);
    }

    @Override
    protected void openPorts() {
       connIn=openInput("CONN");
       outPort = openOutput("OUT");

    }

}
