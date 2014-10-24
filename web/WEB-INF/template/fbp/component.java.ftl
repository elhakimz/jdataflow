//component.java.ftl
//generates Java FBP Component
package ${model.javaPackage};

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

<#list model.imports as importClass>
import ${importClass} ;
</#list>

/** Description
*/
@ComponentDescription("...")
@OutPort(value = "OUT", arrayPort = true)
// change as needed
@InPort("IN")
// change as needed
public class ${model.name} extends Component {

static final String copyright = "...";

private InputPort inport;

private OutputPort[] outport;

@Override
protected void openPorts() {

inport = openInput("IN");

outport = openOutputArray("OUT");
}

@Override
protected void execute() {

/* execute logic */

Packet p = inport.receive();

outport[0].send(p);
}

}