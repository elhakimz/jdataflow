package org.javafbp.runtime.components.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jpmorrsn.fbp.engine.*;
import org.javafbp.runtime.pattern.InPortWidget;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/2/14.
 */
@ComponentDescription("Convert IN (Json) string to Object, with spec from CLASS")
@InPorts({@InPort(value = "IN", description = "Single JSON entity")
        , @InPort(value = "CLASS", description = "Class name to convert to")})
@OutPort(value = "OUT", type = Object.class, description = "Converted object")
@InPortWidget(value = "IN", widget = "jsedit")
public class ToObject extends Component {

    InputPort inIn;
    InputPort inClass;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive();
        Packet p2 = inClass.receive();
        String strJson = (String) p1.getContent();
        String strClass = (String) p2.getContent();
        Gson gson = new Gson();
        Class kls;
        Object obj = new Object();

        try {
            kls = Class.forName(strClass);
            obj = gson.fromJson(strJson, kls);
        } catch (ClassNotFoundException | JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("Oops.. " + e.getMessage());
        }

        outOut.send(create(obj));
        drop(p1);
        drop(p2);
        inIn.close();
        inClass.close();
    }

    @Override
    protected void openPorts() {
        inIn = openInput("IN");
        inClass = openInput("CLASS");
        outOut = openOutput("OUT");
    }
}
