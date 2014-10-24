package org.javafbp.runtime.components.flow

import com.jpmorrsn.fbp.engine.*

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/28/14.
 */
@ComponentDescription("accept and forward certain incoming packets")
@InPorts([@InPort(value = "IN",description = "An IP to be forwarded if accepted")
        , @InPort(value = "ACCEPT", arrayPort = true, description = 'IP to be accepted')
        ,@InPort(value = "RESET",arrayPort = true, description = 'Reset the list accepted IP')])
@OutPort(value="OUT")
class Accept extends Component {

    InputPort inp
    InputPort[] acceptp
    InputPort[] resetp
    OutputPort outp


    @Override
    protected void execute() throws Exception {
    Packet data= inp.receive()

        if(acceptp.length>-1){
            outp.send(data)
        }
        outp.close();
    }

    @Override
    protected void openPorts() {
       inp=openInput("IN")
       acceptp=openInputArray ("ACCEPT")
       resetp=openInputArray("RESET")
       outp=openOutput("OUT")
    }
}

