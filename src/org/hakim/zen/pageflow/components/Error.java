package org.hakim.zen.pageflow.components;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/8/14.
 */
@PageFlowComponent("Error Page")
@InPorts({
        @InPort(value = "INPUT", arrayPort = true),
})
public class Error {

}
