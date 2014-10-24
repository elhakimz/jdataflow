package org.javafbp.runtime.components.json;

import com.google.gson.Gson;
import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/2/14.
 */
@ComponentDescription("Convert Object to JSON String")
@InPorts({@InPort(value="IN", type = Object.class, description = "object input")})
@OutPort("OUT")
public class ObjectToJson extends Component {
    InputPort inIn;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
      Packet inP=inIn.receive();
      Gson gson = new Gson();
      String s = gson.toJson(inP.getContent());

      outOut.send(create(s));
      inIn.close();
      drop(inP);

    }

    @Override
    protected void openPorts() {
      inIn=openInput("IN");
      outOut=openOutput("OUT");
    }
}
