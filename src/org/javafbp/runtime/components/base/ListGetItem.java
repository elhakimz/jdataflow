package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

import java.util.List;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/4/14.
 */
@ComponentDescription("Get Item from a List")
@InPorts({@InPort(value = "IN", type = List.class, description = "List of item objects"),@InPort(value = "INDEX", type = Integer.class, description = "Index of item")})
@OutPort(value = "OUT", type = Object.class)
public class ListGetItem extends Component {
    InputPort inIn;
    InputPort inIndex;
    OutputPort outOut;

    @Override
    protected void execute() throws Exception {
       Packet p1=inIn.receive();
       Packet p2=inIndex.receive();

       List list= (List) p1.getContent();
       Integer  idx= Integer.parseInt((String) p2.getContent());

       if(list!=null){
         Object o=   list.get(idx);
         outOut.send(create(o));
       }else {
         outOut.send(create(null));
       }

       inIn.close();
       inIndex.close();

       drop(p1);
       drop(p2);

    }

    @Override
    protected void openPorts() {
      inIn = openInput("IN");
      inIndex= openInput("INDEX");
      outOut= openOutput("OUT");
    }
}
