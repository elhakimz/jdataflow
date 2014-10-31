package org.javafbp.runtime.components.ftl

import com.jpmorrsn.fbp.engine.*
import freemarker.template.Template
import groovy.json.JsonSlurper
import org.hakim.fbp.util.Settings
import org.javafbp.runtime.pattern.InPortWidget

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/22/14.
 */
@ComponentDescription("One pass template processing")
@InPorts([@InPort(value = "FILE", description = "Freemarker FTL"),
        @InPort(value = "MODEL", type = Object.class, description = "Model in JSON format or Map")])
@OutPort(value = "OUT", type = String.class)
@InPortWidget(value = "FILE", widget = "select-templ")
class TemplateProcess extends Component {
    private InputPort inFile
    private OutputPort outOut
    private InputPort inModel

    @Override
    protected void execute() throws Exception {
        Packet p1 = inFile.receive()
        Packet p2 = inModel.receive()

        String s = p1.content
        def model = p2.content

        String ss
        String TEMP_DIR = Settings.SYS_APP_DIR + Settings.SYS_TEMPLATE_DIR;
        if (s.startsWith(TEMP_DIR)) {
            ss = s.substring(TEMP_DIR.length() + 1);
        } else {
            ss = s;
        }

        Template temp = Settings.instance.freeMarkerConfig.getTemplate(ss);
        StringWriter out = new StringWriter()

//        Object obj = new Gson().fromJson(model, new TypeToken<Map<String, Object>>() {}.getType());

        if (model instanceof String) {
            Map<String, Object> map = new HashMap<>();
            println "model is json"
            def slurper = new JsonSlurper()
            def obj = slurper.parseText(model)
            map.put("model", obj)
            temp.process(map, out)
            outOut.send(create(out.toString()))

        } else if (model instanceof Map) {
            Map<String, Object> map = new HashMap<>();
            println "model is map"
            map.put("model", model)
            temp.process(map, out)
            outOut.send(create(out.toString()))
        } else {
            println "model is invalid"
        }


        inFile.close()
        inModel.close()

        drop(p1)
        drop(p2)
    }

    @Override
    protected void openPorts() {
        inFile = openInput("FILE")
        outOut = openOutput("OUT")
        inModel = openInput("MODEL")
    }
}
