package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.hakim.fbp.db.SysDataSource;
import org.hakim.fbp.util.Settings;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;
import org.json.JSONArray;

import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.*;
import java.util.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/30/14.
 */
@ComponentDescription("Create Backend Browse Page")
@InPorts({@InPort(value = "TABLE", type = String.class, description = "list of tables in JSON format")
        , @InPort(value = "TMPLFILE", type = String.class, description = "template file path")
        , @InPort(value = "WITHDATA", type = Boolean.class, description = "populate with or without data")
        , @InPort(value = "TARGETDIR", type = String.class, description = "target directory")})
@OutPort("OUT")
@InPortWidgets({@InPortWidget(value = "TMPLFILE", widget = InPortWidget.SELECT_DIRECTORY)
        , @InPortWidget(value = "TARGETDIR", widget = InPortWidget.SELECT_DIRECTORY)})

public class CreateBackBrowsePage extends Component {
    boolean withData;
    private InputPort tableIn;
    private InputPort templIn;
    private InputPort withDataIn;
    private InputPort targetDirIn;
    private OutputPort out;

    @Override
    protected void execute() throws Exception {
        Packet p1 = tableIn.receive();
        Packet p2 = templIn.receive();
        Packet p3 = withDataIn.receive();
        Packet p4 = targetDirIn.receive();

        String templ = String.valueOf(p2.getContent());
        String table = String.valueOf(p1.getContent());
        String targetDir = String.valueOf(p4.getContent());

        withData = Boolean.valueOf((String) p3.getContent());

        String ss;
        String TEMP_DIR = Settings.SYS_APP_DIR + Settings.SYS_TEMPLATE_DIR;
        if (templ.startsWith(TEMP_DIR)) {
            ss = templ.substring(TEMP_DIR.length() + 1);
        } else {
            ss = templ;
        }

        JSONArray tables = new JSONArray();
        if (table.startsWith("[")) {
            tables = new JSONArray(table);
        } else {
            tables = new JSONArray();
            tables.put(table);
        }
        String result = "[";
        Template temp = Settings.getInstance().getFreeMarkerConfig().getTemplate(ss);

        for (int i = 0; i < tables.length(); i++) {
            Map<String, List> map = query(String.valueOf(tables.get(i)));

            Map<String, Map> objectMap = new HashMap<>();
            objectMap.put("model", map);
            try {
                FileOutputStream fos = new FileOutputStream(targetDir + "/" + tables.get(i) + ".html");
                OutputStreamWriter out = new OutputStreamWriter(fos);
                temp.process(objectMap, out);
                result += tables.get(i) + ",";
                out.close();

            } catch (TemplateException | IOException e) {
                e.printStackTrace();

            }
        }
        result += "]";
        out.send(create(result));

        tableIn.close();
        templIn.close();
        withDataIn.close();
        targetDirIn.close();
        drop(p1);
        drop(p2);
        drop(p3);
        drop(p4);

    }

    @Override
    protected void openPorts() {
        tableIn = openInput("TABLE");
        templIn = openInput("TMPLFILE");
        withDataIn = openInput("WITHDATA");
        targetDirIn = openInput("TARGETDIR");
        out = openOutput("OUT");
    }

    /**
     * create query on tables
     *
     * @param table
     * @return
     */
    private Map<String, List> query(String table) {
        StringBuilder sb = new StringBuilder();
        if (!withData) {
            sb.append("SELECT * FROM ").append(table).append(" LIMIT 1");
        } else {
            sb.append("SELECT * FROM ").append(table);
        }
        Map<String, List> map = new LinkedHashMap<>();
        try {
            List<List> datas = new ArrayList<>();
            List<Map> metas = new ArrayList<>();

            boolean metacollect = true;
            Connection connection = SysDataSource.getInstance().getConnection();
            if (!connection.isClosed()) {
                PreparedStatement ps = connection.prepareStatement(sb.toString());
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsm = rs.getMetaData();
                rsm.getColumnCount();

                while (rs.next()) {
                    int colcount = rsm.getColumnCount();
                    Object val = new Object();
                    List<Object> lst = new ArrayList<>();

                    for (int i = 1; i <= colcount; i++) {
                        lst.add(rs.getObject(i));

                        if (metacollect) {
                            Map<String, Object> metaData = new LinkedHashMap<>();
                            metaData.put("name", rsm.getColumnName(i));
                            metaData.put("column", rsm.getColumnName(i));
                            metaData.put("type", rsm.getColumnTypeName(i));
                            metaData.put("scale", rsm.getScale(i));
                            metaData.put("precision", rsm.getPrecision(i));
                            metaData.put("label", rsm.getColumnLabel(i));
                            metaData.put("nullable", rsm.isNullable(i));
                            metas.add(metaData);
                        }
                    }
                    metacollect = false;
                    datas.add(lst);
                }
            }
            connection.close();
            map.put("data", datas);
            map.put("metadata", metas);
            return map;

        } catch (SQLException | IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
        return map;
    }

}
