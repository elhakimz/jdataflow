package org.hakim.zen.pageflow.components;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/8/14.
 */
@PageFlowComponent("Service component")
@InPorts({
        @InPort("INPUT")

})
@OutPorts({
        @OutPort(value = "OUTPUT", arrayPort = true),
        @OutPort(value = "ERROR", optional = true)
})
public class Service {

}
