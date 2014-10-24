package org.javafbp.runtime.components.ftl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jpmorrsn.fbp.engine.Component
import com.jpmorrsn.fbp.engine.ComponentDescription
import com.jpmorrsn.fbp.engine.InPort
import com.jpmorrsn.fbp.engine.InPorts
import com.jpmorrsn.fbp.engine.InputPort
import com.jpmorrsn.fbp.engine.OutPort
import com.jpmorrsn.fbp.engine.OutputPort
import com.jpmorrsn.fbp.engine.Packet
import freemarker.template.Template
import org.hakim.fbp.util.Settings
import org.javafbp.runtime.pattern.InPortWidget

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/22/14.
 */
@ComponentDescription("One pass template processing")
@InPorts([@InPort(value="FILE",description = "Freemarker FTL"),
        @InPort(value = "MODEL",type = String.class, description = "Model in JSON format")])
@OutPort(value="OUT", type=String.class)
@InPortWidget(value = "FILE",widget = "select-templ")
class TemplateProcess extends Component {
    private InputPort inFile
    private OutputPort outOut
    private InputPort inModel

    @Override
    protected void execute() throws Exception {
        Packet p1= inFile.receive()
        Packet p2 = inModel.receive()

        String s = p1.content
        String model = p2.content

        String ss
        String TEMP_DIR =Settings.SYS_APP_DIR+Settings.SYS_TEMPLATE_DIR;
        if(s.startsWith(TEMP_DIR)){
            ss=s.substring(TEMP_DIR.length()+1);
        }else{
            ss=s;
        }

        Template temp = Settings.instance.freeMarkerConfig.getTemplate(ss);
        StringWriter out= new StringWriter()
        Map<String,Object> map = new HashMap<>();
        Object obj = new Gson().fromJson(model, new TypeToken<Map<String, String>>() {}.getType());
        map.put("model", obj)
        temp.process(map,out)
        outOut.send(create(out.toString()))

        inFile.close()
        inModel.close()

        drop(p1)
        drop(p2)
    }

    @Override
    protected void openPorts() {
      inFile=openInput("FILE")
      outOut=openOutput("OUT")
      inModel=openInput("MODEL")
    }
}
