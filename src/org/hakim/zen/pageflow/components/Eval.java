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
@PageFlowComponent("Evaluator component")
@InPorts({
        @InPort(value = "INPUT", arrayPort = true),
        @InPort(value = "EXPRESSION", arrayPort = true)

})
@OutPorts({
        @OutPort(value = "OUTPUT", arrayPort = true),
        @OutPort(value = "ERROR", optional = true)
})
public class Eval {
}
