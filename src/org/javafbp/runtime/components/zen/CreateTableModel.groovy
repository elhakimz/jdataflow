package org.javafbp.runtime.components.zen

import com.jpmorrsn.fbp.engine.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/30/14.
 */
@ComponentDescription("Create Table Data Model")
@InPorts([
        @InPort(value = "DATA", type = String.class, description = "JSON List of data")
        , @InPort(value = "META", type = String.class, description = "Metadata")
])
@OutPort(value = "OUT")
class CreateTableModel extends Component {
    InputPort inData
    InputPort inMeta
    OutputPort outOut

    @Override
    protected void execute() throws Exception {
        Packet p1 = inData.receive()
        Packet p2 = inMeta.receive();
        String sdata = p1.content
        String smeta = p2.content

        if (!sdata.trim().startsWith("[")) {
            sdata = "[$sdata]";
        }

        JSONArray arrData = new JSONArray(sdata)
        JSONArray arrMeta = new JSONArray(smeta)
        JSONObject obj = new JSONObject()
        obj.put('data', arrData)
        obj.put('metadata', arrMeta)

        outOut.send(create(obj.toString(1)))

        inData.close()
        inMeta.close()
        drop(p1)
        drop(p2)

    }

    @Override
    protected void openPorts() {
        inData = openInput("DATA")
        inMeta = openInput("META")
        outOut = openOutput("OUT")
    }
}
