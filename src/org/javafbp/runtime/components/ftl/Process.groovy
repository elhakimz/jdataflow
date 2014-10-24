package org.javafbp.runtime.components.ftl

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/29/14.
 */
@ComponentDescription("Process Freemarker Template with template TEMPL and model MODEL onto OUT string")
@InPorts([@InPort(value="TEMPL",type = Object.class, description = "Template object")
        ,@InPort(value = "MODEL",type = String.class, description = "Model in JSON format")])
@OutPort(value="OUT", type=String.class)
class Process extends Component{

    InputPort inTempl
    InputPort inModel
    OutputPort outOut

    @Override
    protected void execute() throws Exception {
        Packet p1= inTempl.receive()
        Packet p2= inModel.receive()
        Template templ = p1.content as Template

        String model
        model = p2.content

        StringWriter out= new StringWriter()

        Map<String,Object> map = new HashMap<>();

         Object obj = new Gson().fromJson(model, new TypeToken<Map<String, String>>() {}.getType());
         map.put("model", obj)
         templ.process(map,out)

        outOut.send(create(out.toString()))
        inTempl.close()
        inModel.close()

        drop(p1)
        drop(p2)

    }

    @Override
    protected void openPorts() {

        inTempl = openInput("TEMPL")
        inModel = openInput("MODEL")
        outOut = openOutput("OUT")

    }
}
