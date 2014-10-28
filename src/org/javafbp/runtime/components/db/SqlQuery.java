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
@InPorts({@InPort(value = "CONN", type = Connection.class), @InPort(value = "QUERY", description = "SQL select query")})
@OutPorts({@OutPort(value = "OUT", description = "Query result in JSON string")
        , @OutPort(value = "METADATA", description = "Resultset Metadata in JSON", optional = true)
        , @OutPort(value = "CONN", type = Connection.class, optional = true, description = "Connection object")})
@InPortWidget(value = "QUERY", widget = "sql-edit")
public class SqlQuery extends Component {
    InputPort inConn;
    InputPort inQuery;
    OutputPort outOut;
    OutputPort outConn;
    OutputPort outMeta;

    @Override
    protected void execute() throws Exception {
        Packet p = inConn.receive();
        Packet p2 = inQuery.receive();

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
                    for (int i = 1; i <= rsm.getColumnCount(); i++) {
                        jsonObject.put(rsm.getColumnName(i), rs.getObject(i));
                        if (metacollect && outMeta.isConnected()) {
                            JSONObject metaData = new JSONObject();
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
                    datas.put(jsonObject);
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
        outConn.send(p);
        if (outMeta.isConnected()) {
            outMeta.send(create(metas.toString(1)));
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
        outOut = openOutput("OUT");
        outConn = openOutput("CONN");
        outMeta = openOutput("METADATA");
    }
}
