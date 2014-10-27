package org.javafbp.runtime.components.base

import com.jpmorrsn.fbp.engine.*
import org.json.JSONArray

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/27/14.
 */
/** Component to do simple stack testing.
 */
@ComponentDescription("Collect streams then continue it to outport")
@InPort("IN")
@OutPort("OUT")
class StackStreams extends Component {

    private InputPort inport;
    private OutputPort outport;
    private def list = []

    @Override
    protected void execute() throws Exception {
        Packet p;
        if (stackSize() > 0) {
            p = pop();
        } else {
            p = create("");
        }
        Packet q = inport.receive();
        if (q != null) {
            list.add(q.content)
            drop(q);
            push(p);
        } else { // end of stream

            JSONArray jsonArray = new JSONArray()
            for (Object obj : list) {
                jsonArray.put(obj)
            }
            outport.send(create(jsonArray))
            drop(p);
        }
    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");
        outport = openOutput("OUT")
    }
}
