package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.hakim.fbp.db.DbUtil;
import org.hakim.fbp.db.SysDataSource;
import org.hakim.fbp.util.Inflector;
import org.hakim.fbp.util.Settings;
import org.javafbp.runtime.pattern.IResponsiveComponent;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;
import org.json.JSONArray;

import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/30/14.
 */
@ComponentDescription("Create Backend Edit Page")
@InPorts({@InPort(value = "TABLE", type = String.class, description = "tables")
        , @InPort(value = "TEMPLFILE", type = String.class, description = "template file")
        , @InPort(value = "TARGETDIR", type = String.class, description = "target directory")})
@OutPort("OUT")
@InPortWidgets({@InPortWidget(value = "TEMPLFILE", widget = InPortWidget.SELECT_REPOSITORY)
        , @InPortWidget(value = "TARGETDIR", widget = InPortWidget.SELECT_DIRECTORY)})
public class CreateBackEditPage extends Component implements IResponsiveComponent {

    final static Logger logger = Logger.getLogger(CreateBackEditPage.class);
    private InputPort tableIn;
    private InputPort templIn;
    private InputPort targetDirIn;
    private OutputPort out;

    @Override
    protected void execute() throws Exception {
        Packet p1 = tableIn.receive();
        Packet p2 = templIn.receive();
        Packet p4 = targetDirIn.receive();

        String templ = String.valueOf(p2.getContent());
        String table = String.valueOf(p1.getContent());
        String targetDir = String.valueOf(p4.getContent());

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
            Map<String, Object> map = query(String.valueOf(tables.get(i)));

            Map<String, Map> objectMap = new HashMap<>();
            objectMap.put("model", map);
            try {
                FileOutputStream fos = new FileOutputStream(targetDir + "/" + tables.get(i) + "_edit.html");
                OutputStreamWriter wout = new OutputStreamWriter(fos);
                temp.process(objectMap, wout);
                result += tables.get(i) + ",";
                wout.close();

            } catch (TemplateException | IOException e) {
                e.printStackTrace();

            }
        }
        result += "]";
        out.send(create(result));
        tableIn.close();
        templIn.close();
        targetDirIn.close();
        drop(p1);
        drop(p2);
        drop(p4);

    }

    @Override
    protected void openPorts() {
        tableIn = openInput("TABLE");
        templIn = openInput("TEMPLFILE");
        targetDirIn = openInput("TARGETDIR");
        out = openOutput("OUT");

    }

    /**
     * query data
     *
     * @param table
     * @return
     */
    private Map<String, Object> query(String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(table).append(" LIMIT 1");
        Map<String, Object> map = new LinkedHashMap<>();
        List<Map> metas = new ArrayList<>();

        try {
            java.sql.Connection connection = SysDataSource.getInstance().getConnection();
            if (!connection.isClosed()) {
                PreparedStatement ps = connection.prepareStatement(sb.toString());
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsm = rs.getMetaData();
                int colCount = rsm.getColumnCount();

                Inflector inflector = Inflector.getInstance();

                while (rs.next()) {
                    for (int i = 1; i <= colCount; i++) {
                        Map<String, Object> metaData = new LinkedHashMap<>();
                        metaData.put("name", rsm.getColumnName(i));
                        metaData.put("column", rsm.getColumnName(i));
                        metaData.put("type", DbUtil.getJsDataType(rsm.getColumnTypeName(i)));
                        metaData.put("scale", rsm.getScale(i));
                        metaData.put("precision", rsm.getPrecision(i));
                        metaData.put("label", rsm.getColumnLabel(i));
                        metaData.put("nullable", rsm.isNullable(i));
                        metaData.put("required", (rsm.isNullable(i) != 1));
                        metaData.put("title", inflector.titleCase(rsm.getColumnName(i)));
                        metas.add(metaData);
                    }
                }

                connection.close();


                Map config = new HashMap();
                config.put("table", table);
                config.put("idcolumn", "id");

                map.put("config", config);
                map.put("metadata", metas);

            }
        } catch (SQLException | IOException | PropertyVetoException e) {
            e.printStackTrace();
        }

        return map;
    }

    @Override
    public void sendMessage(String message) {

    }
}
