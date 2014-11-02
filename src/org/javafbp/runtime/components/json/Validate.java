package org.javafbp.runtime.components.json;

import com.jpmorrsn.fbp.engine.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Purpose:
 * Validate JSON file
 *
 * @author abilhakim
 *         Date: 10/22/14.
 */
@ComponentDescription("Parse String to JSON object")
@InPort(value = "IN", description = "JSON String to be validated")
@OutPort(value = "OUT", type = Boolean.class, description = "validity, true if valid")
public class Validate extends Component {
    InputPort inIn;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p = inIn.receive();
        String s = (String) p.getContent();

        try {
            new JSONObject(s);
            outOut.send(create(Boolean.TRUE));
        } catch (JSONException ex) {

            try {
                new JSONArray(s);
                outOut.send(create((Boolean.TRUE)));
            } catch (JSONException ex1) {
                outOut.send(create(Boolean.FALSE));
            }
        }
        inIn.close();
        drop(p);
    }

    @Override
    protected void openPorts() {
        inIn = openInput("IN");
        outOut = openOutput("OUT");
    }

}
