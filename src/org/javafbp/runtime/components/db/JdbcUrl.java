package org.javafbp.runtime.components.db;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/22/14.
 */
@ComponentDescription(value = "JdbcUrl will form URL string from inputted value, resulting URL string")
@InPorts(value = {
     @InPort(value = "ADDRESS", type = String.class),
     @InPort(value="PORT",type=String.class),
     @InPort(value = "DB", type = String.class),
     @InPort(value="TYPE", type=String.class)
})
@OutPort(value = "URL", type = String.class)
public class JdbcUrl  extends Component {
    static final String copyright = "...";
    private InputPort addrIn;
    private InputPort portIn;
    private InputPort dbIn;
    private InputPort typeIn;


    private OutputPort outport;
    @Override
    protected void openPorts() {
        addrIn = openInput("ADDRESS");
        dbIn=openInput("DB");
        portIn=openInput("PORT");
        typeIn=openInput("TYPE");
        outport = openOutput("URL");
    }


    @Override
    protected void execute() {
       /* execute logic */
        Packet addrP=addrIn.receive();
        Packet dbP=dbIn.receive();
        Packet typeP=typeIn.receive();
        Packet portP = portIn.receive();

        StringBuilder sb=new StringBuilder();
        sb.append("jdbc:").append(typeP.getContent()).append("://")
          .append(addrP.getContent())
          .append(":").append(portP.getContent())
          .append("/").append(dbP.getContent());
        outport.send(create(sb.toString()));

        addrIn.close();
        dbIn.close();
        typeIn.close();
        portIn.close();

        drop(addrP);
        drop(dbP);
        drop(typeP);
        drop(portP);

    }

}
