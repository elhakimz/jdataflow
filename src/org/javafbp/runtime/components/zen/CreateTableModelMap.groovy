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
@OutPort(value = "OUT", type = Map.class)
class CreateTableModelMap extends Component {
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

        Map map = new LinkedHashMap();

        map.put("data", toList(arrData))
        map.put("metadata", toListMap(arrMeta))

        outOut.send(create(map))

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

    List toList(JSONArray arrData) {
        List lst = new ArrayList()
        for (int i = 0; i < arrData.length(); i++) {

            def data = arrData.get(i)
            if (data instanceof String && data.trim().startsWith("[")) {
                JSONArray arr = new JSONArray(data)
                List lst2 = new ArrayList()
                for (int j = 0; j < arr.length(); j++) {
                    lst2.add(arr.get(j))
                }
                lst.add(lst2)
            } else if (data instanceof JSONArray) {
                List lst2 = new ArrayList()
                for (int j = 0; j < data.length(); j++) {
                    lst2.add(data.get(j))
                }
                lst.add(lst2)
            } else {
                lst.add(data)
            }
        }
        return lst
    }

    List<Map> toListMap(JSONArray arrMeta) {
        List lst = new ArrayList()
        for (int i = 0; i < arrMeta.length(); i++) {
            def data = arrMeta.get(i)
            JSONObject jsonObject
            if (data instanceof String && data.trim().startsWith("{")) {
                jsonObject = new JSONObject(data)
            }

            if (data instanceof JSONObject) {
                jsonObject = data
            }

            if (jsonObject) {
                Map map = new LinkedHashMap()
                for (Object o : jsonObject.keySet()) {
                    map.put(o, jsonObject.get(String.valueOf(o)))
                }
                lst.add(map)
            }


        }
        return lst
    }
}
