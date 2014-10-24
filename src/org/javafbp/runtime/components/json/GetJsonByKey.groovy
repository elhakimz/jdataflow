package org.javafbp.runtime.components.json

import com.jpmorrsn.fbp.engine.Component
import com.jpmorrsn.fbp.engine.ComponentDescription
import com.jpmorrsn.fbp.engine.InPort
import com.jpmorrsn.fbp.engine.InPorts
import com.jpmorrsn.fbp.engine.InputPort
import com.jpmorrsn.fbp.engine.OutPort
import com.jpmorrsn.fbp.engine.OutputPort
import com.jpmorrsn.fbp.engine.Packet
import org.json.JSONObject

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/23/14.
 */
@ComponentDescription("Get Json IN by KEY, returns Json String object")
@InPorts([@InPort("IN"), @InPort("KEY")])
@OutPort("OUT")
class GetJsonByKey extends Component {

    InputPort inIn
    InputPort inKey
    OutputPort outOut

    @Override
    protected void execute() throws Exception {
        Packet p1 = inIn.receive()
        Packet p2 = inKey.receive()

        JSONObject json = new JSONObject(p1.content)
        String key = p2.content

        Object res = json.get(key)
        outOut.send(create(res))

        drop(p1)
        drop(p2)

    }

    @Override
    protected void openPorts() {
      inIn = openInput("IN")
      inKey=openInput("KEY")
      outOut = openOutput("OUT")
    }
}
