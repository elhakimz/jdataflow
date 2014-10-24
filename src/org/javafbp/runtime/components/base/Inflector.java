package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/4/14.
 */
@ComponentDescription("Change human string representation, capitalize, lower, plural, singular, lowercamel, uppercamel")
@InPorts({@InPort(value = "IN", type = String.class),@InPort(value = "RULE",type = String.class)})
@OutPort(value = "OUT", type = String.class)
public class Inflector extends Component {
    InputPort inIn;
    InputPort inRule;
    OutputPort outOut;
    @Override
    protected void execute() throws Exception {
      Packet p1=inIn.receive();
      Packet p2 = inRule.receive();
      String s1 = (String) p1.getContent();
      String s2 = (String) p2.getContent();

        org.hakim.fbp.util.Inflector infl;
        infl = org.hakim.fbp.util.Inflector.getInstance();

      s2=s2.toLowerCase();
        switch (s2) {
            case "capital":
                s1 = s1.toUpperCase();
                break;
            case "lower":
                s1 = s1.toLowerCase();
                break;
            case "plural":
                s1 = infl.pluralize(s1);
                break;
            case "singular":
                s1 = infl.singularize(s1);
                break;
            case "lowercamel":
                s1 = infl.lowerCamelCase(s1);
                break;
            case "uppercamel":
                s1 = infl.upperCamelCase(s1);
                break;
        }

       if(s1.contains(" ")){
         String[] s3 = s1.split("\\s+");
         StringBuilder sb= new StringBuilder();
         for(String s:s3){
             sb.append(s).append(" ");
         }
         outOut.send(create(sb.toString()));
       }else{
           outOut.send(create(s1));
       }

      inIn.close();
      inRule.close();
      outOut.close();
      drop(p1);
      drop(p2);

    }

    @Override
    protected void openPorts() {
      inIn = openInput("IN");
      inRule = openInput("RULE");
      outOut = openOutput("OUT");
    }
}
