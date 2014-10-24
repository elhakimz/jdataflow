package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;
import org.javafbp.runtime.pattern.InPortWidget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.sql.Connection;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/22/14.
 */
@ComponentDescription("Query database, CONN is connection, QUERY is query string, OUT is output in JSON format")
@InPorts({@InPort(value = "CONN", type = Connection.class), @InPort(value = "QUERY", description = "SQL select query")})
@OutPorts({@OutPort(value = "OUT", description = "Query result in JSON string"),@OutPort(value = "CONN",type = Connection.class, optional = true)})
@InPortWidget(value = "QUERY",widget = "sql-edit")
public class SqlQuery extends Component{
    InputPort inConn;
    InputPort inQuery;
    OutputPort outOut;
    OutputPort outConn;

    @Override
    protected void execute() throws Exception {
       Packet p=inConn.receive();
       Packet p2 = inQuery.receive();

       String qry = (String) p2.getContent();
       Connection connection = (Connection) p.getContent();
       JSONArray jsonArray = new JSONArray();

        try {
            if(!connection.isClosed()){
                PreparedStatement ps =connection.prepareStatement(qry);
                ResultSet rs=ps.executeQuery();
                ResultSetMetaData rsm=rs.getMetaData();
                rsm.getColumnCount();

                while(rs.next()){
                    JSONObject jsonObject = new JSONObject();
                    for(int i=1;i<=rsm.getColumnCount();i++){
                        jsonObject.put(rsm.getColumnName(i),rs.getObject(i));
                    }
                    jsonArray.put(jsonObject);
                }
            }

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
            jsonArray.put(e.getMessage());
        }

        outOut.send(create(jsonArray.toString(1)));
        outConn.send(p);

        drop(p2);
      // drop(p);
       inConn.close();
       inQuery.close();



    }

    @Override
    protected void openPorts() {

       inConn = openInput("CONN");
       inQuery = openInput("QUERY");
       outOut = openOutput("OUT");
       outConn = openOutput("CONN");

    }
}
