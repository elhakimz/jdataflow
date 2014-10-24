package org.javafbp.runtime.components.base

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jpmorrsn.fbp.engine.Component
import com.jpmorrsn.fbp.engine.ComponentDescription
import com.jpmorrsn.fbp.engine.InPort
import com.jpmorrsn.fbp.engine.InputPort
import com.jpmorrsn.fbp.engine.OutPort
import com.jpmorrsn.fbp.engine.OutputPort
import com.jpmorrsn.fbp.engine.Packet
import groovy.json.JsonSlurper

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/23/14.
 */
@ComponentDescription("Send Each Item with slight delays")
@InPort(value = "IN", description = "List or JSON List object")
@OutPort("OUT")
class SendEach extends Component {
    InputPort inIn
    OutputPort outOut

    @Override
    protected void execute() throws Exception {
       Packet p= inIn.receive()
       def obj = p.content

       if(obj instanceof List){
           obj.each{
               outOut.send(create(it))
               sleep(100)
           }
       }else if(obj instanceof String){
          def lst= new JsonSlurper().parseText( obj )
           lst.each {
               Gson gson=new Gson()
               String s=gson.toJson(it)
               outOut.send(create(s))
               //sleep(100)
           }
       }

       inIn.close()
       drop(p)
    }

    @Override
    protected void openPorts() {
      inIn = openInput("IN")
      outOut = openOutput("OUT")
    }
}
