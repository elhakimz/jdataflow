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
@PageFlowComponent("Variable Data Component")
@InPorts({
        @InPort(value = "DATA", description = "json data to be read", arrayPort = true),
})
@OutPorts({
        @OutPort(value = "OUTPUT", arrayPort = true, description = "resulting data"),
})
public class Variable {
}
