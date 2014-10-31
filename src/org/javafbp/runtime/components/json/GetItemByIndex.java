package org.javafbp.runtime.components.json;

import com.jpmorrsn.fbp.engine.*;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/11/14.
 */
@ComponentDescription("Get item in JSON Array")
@InPorts({@InPort(value = "IN", type = String.class, description = "JSON Array string")
        , @InPort(value = "INDEX", type = Integer.class, description = "Index of JSON")})
@OutPort(value = "OUT", type = String.class)
public class GetItemByIndex extends Component {
    InputPort inIn;
    InputPort inIndex;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive();
        Packet p2 = inIndex.receive();
        int idx = Integer.parseInt((String) p2.getContent());
        String s = (String) p1.getContent();
        JSONArray arr = new JSONArray(s);
        String item = "";

        try {

            item = String.valueOf(arr.get(idx));

        } catch (JSONException e) {
            e.printStackTrace();

        }
        outOut.send(create(item));
        inIn.close();
        inIn.close();
        drop(p1);
        drop(p2);
    }

    @Override
    protected void openPorts() {

        inIn = openInput("IN");
        inIndex = openInput("INDEX");
        outOut = openOutput("OUT");

    }
}
