package org.javafbp.runtime.components.ftl

import com.jpmorrsn.fbp.engine.*
import freemarker.template.Template
import org.hakim.fbp.util.Settings
import org.javafbp.runtime.pattern.InPortWidget

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/29/14.
 */
@ComponentDescription("Get Freemarker Template in IN on OUT")
@InPort(value="FILE",description = "Freemarker FTL")
@OutPort(value="TEMPL", type = Object.class)
@InPortWidget(value = "FILE",widget = "select-templ")
class GetTemplate extends Component {
    InputPort inFile
    OutputPort outTempl

    @Override
    protected void execute() throws Exception {
      Packet p = inFile.receive()
       String s= p.content
       String ss="";
       String TEMP_DIR =Settings.SYS_APP_DIR+Settings.SYS_TEMPLATE_DIR;
       if(s.startsWith(TEMP_DIR)){
         ss=s.substring(TEMP_DIR.length()+1);
       }else{
         ss=s;
       }

       Template temp = Settings.getInstance().getFreeMarkerConfig().getTemplate(ss);
       outTempl.send(create(temp))
      drop(p)
      inFile.close()
    }

    @Override
    protected void openPorts() {
        inFile = openInput("FILE")
        outTempl = openOutput("TEMPL")
    }

}
