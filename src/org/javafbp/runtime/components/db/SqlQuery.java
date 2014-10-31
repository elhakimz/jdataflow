package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;
import org.javafbp.runtime.pattern.InPortWidget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.*;

/**
 * Purpose:
 * Sql Query component
 *
 * @author abilhakim
 *         Date: 9/22/14.
 */
@ComponentDescription("Query database, CONN is connection, QUERY is query string, OUT is output in JSON format")
@InPorts({@InPort(value = "CONN", type = Connection.class)
        , @InPort(value = "QUERY", description = "SQL select query")
        , @InPort(value = "KEYED", description = "Keyed Result", type = Boolean.class, optional = true)})
@OutPorts({@OutPort(value = "OUT", description = "Query result in JSON string")
        , @OutPort(value = "METADATA", description = "Resultset Metadata in JSON", optional = true)
        , @OutPort(value = "CONN", type = Connection.class, optional = true, description = "Connection object")})
@InPortWidget(value = "QUERY", widget = "sql-edit")
public class SqlQuery extends Component {
    InputPort inConn;
    InputPort inQuery;
    InputPort inKeyed;

    OutputPort outOut;
    OutputPort outConn;
    OutputPort outMeta;
    boolean keyed = false;

    @Override
    protected void execute() throws Exception {
        Packet p = inConn.receive();
        Packet p2 = inQuery.receive();
        Packet p3;
        if ((p3 = inKeyed.receive()) != null) {
            keyed = (Boolean) p3.getContent();
        }


        String qry = (String) p2.getContent();
        Connection connection = (Connection) p.getContent();
        JSONArray datas = new JSONArray();
        JSONArray metas = new JSONArray();
        boolean metacollect = true;
        try {
            if (!connection.isClosed()) {
                PreparedStatement ps = connection.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsm = rs.getMetaData();
                rsm.getColumnCount();

                while (rs.next()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONArray arr;
                    arr = new JSONArray();
                    int colcount = rsm.getColumnCount();
                    Object val = new Object();

                    for (int i = 1; i <= colcount; i++) {

                        if (keyed) {
                            jsonObject.put(rsm.getColumnName(i), rs.getObject(i));
                        } else {
                            if (colcount > 1) arr.put(rs.getObject(i));
                            else val = rs.getObject(i);
                        }

                        if (metacollect && outMeta.isConnected()) {
                            JSONObject metaData = new JSONObject();
                            metaData.put("name", rsm.getColumnName(i));
                            metaData.put("column", rsm.getColumnName(i));
                            metaData.put("type", rsm.getColumnTypeName(i));
                            metaData.put("scale", rsm.getScale(i));
                            metaData.put("precision", rsm.getPrecision(i));
                            metaData.put("label", rsm.getColumnLabel(i));
                            metaData.put("nullable", rsm.isNullable(i));
                            metas.put(metaData);
                        }
                    }
                    metacollect = false;

                    if (keyed) {
                        datas.put(jsonObject);
                    } else {
                        if (colcount > 1) datas.put(arr);
                        else datas.put(val);
                    }
                }
            }

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("status", -1);
            err.put("message", e.getMessage());
            datas.put(err);
        }
        outOut.send(create(datas.toString(1)));

        if (outMeta.isConnected()) {
            outMeta.send(create(metas.toString(1)));
        }

        if (outConn.isConnected()) {
            outConn.send(p);
        } else {
            connection.close();
            drop(p);
        }
        drop(p2);
        // drop(p);
        inConn.close();
        inQuery.close();

    }

    @Override
    protected void openPorts() {
        inConn = openInput("CONN");
        inQuery = openInput("QUERY");
        inKeyed = openInput("KEYED");
        outOut = openOutput("OUT");
        outConn = openOutput("CONN");
        outMeta = openOutput("METADATA");
    }
}
