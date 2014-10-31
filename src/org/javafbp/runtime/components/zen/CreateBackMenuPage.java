package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.hakim.fbp.util.Settings;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/30/14.
 */
@ComponentDescription("Create Backend Browse Page")
@InPorts({@InPort(value = "TABLE", type = String.class, description = "list of tables in JSON format")
        , @InPort(value = "TMPLFILE", type = String.class, description = "template file path")
        , @InPort(value = "TARGETDIR", type = String.class, description = "target directory")})
@OutPort("OUT")
@InPortWidgets({@InPortWidget(value = "TMPLFILE", widget = InPortWidget.SELECT_DIRECTORY)
        , @InPortWidget(value = "TARGETDIR", widget = InPortWidget.SELECT_DIRECTORY)})

public class CreateBackMenuPage extends Component {
    boolean withData;
    private InputPort tableIn;
    private InputPort templIn;
    private InputPort targetDirIn;
    private OutputPort outData;

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

        JSONArray tables;
        if (table.startsWith("[")) {
            tables = new JSONArray(table);
        } else {
            tables = new JSONArray();
            tables.put(table);
        }

        JSONObject result;
        result = new JSONObject();

        try {

            Template temp = Settings.getInstance().getFreeMarkerConfig().getTemplate(ss);
            FileOutputStream fos = new FileOutputStream(targetDir + "/menu.html");
            OutputStreamWriter out = new OutputStreamWriter(fos);
            Map<String, Object> objectMap = new HashMap<>();
            List<String> tableList = new ArrayList<>();

            for (int i = 0; i < tables.length(); i++) {
                tableList.add(String.valueOf(tables.get(i)));
            }

            objectMap.put("model", tableList);
            temp.process(objectMap, out);
            out.close();
            result.put("status", "1");
            result.put("message", "Success");
        } catch (IOException | JSONException | TemplateException e) {
            e.printStackTrace();
            result.put("status", "-1");
            result.put("message", e.getMessage());
        }

        outData.send(create(result.toString(1)));

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
        templIn = openInput("TMPLFILE");
        targetDirIn = openInput("TARGETDIR");
        outData = openOutput("OUT");
    }


}