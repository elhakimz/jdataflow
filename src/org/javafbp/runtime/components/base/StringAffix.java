package org.javafbp.runtime.components.base;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/23/14.
 */

import com.jpmorrsn.fbp.engine.*;

/**
 * Affix each packet IN with the given Strings PRE before and POST after, and copy it to OUT.
 *
 */
@ComponentDescription("For each packet IN add the Strings PRE as a prefix and POST as a suffix, and copy to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("PRE"), @InPort("POST") })
public class StringAffix extends Component {

    static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
            + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
            + "based on the Everything Development Company's Artistic License.  A document describing "
            + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
            + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

    InputPort inport, preport, postport;

    private OutputPort outport;

    @Override
    protected void execute() {
        Packet pin = inport.receive();
        String spre = ""; // Empty string if null
        Packet pre = preport.receive();
        if (pre != null) {
            spre = (String) pre.getContent();
            drop(pre);
        }
        //preport.close();

        String spost = ""; // Empty string if null
        Packet post = postport.receive();
        if (post != null) {
            spost = (String) post.getContent();
            drop(post);
        }
        //postport.close();


      //  while ((pin = inport.receive()) != null) {
            String sout = spre + (String) pin.getContent() + spost;
            System.out.println("sout = " + sout);
            outport.send(create(sout));
            drop(pin); // did you hear that?

     //   }
    }

    @Override
    protected void openPorts() {
        inport = openInput("IN");
        preport = openInput("PRE");
        postport = openInput("POST");
        outport = openOutput("OUT");
    }
}
