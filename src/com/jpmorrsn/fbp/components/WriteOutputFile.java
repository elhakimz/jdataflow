package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.util.Settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;

/**
 * Purpose:
 * Write output file
 * @author abilhakim
 *         Date: 10/10/14.
 */
@ComponentDescription("Write IN to system file output with Token as filename ex XYZ123.out.txt")
@InPort(value = "IN", type = String.class, description = "Input data in string")
public class WriteOutputFile extends Component {

    InputPort inIn;
    private static String linesep = System.getProperty("line.separator");


    @Override
    protected void execute() throws Exception {
        Packet p;
        String sf = Settings.getInstance().getOutputFileName();
        Writer w;
        try {
            FileOutputStream fos = new FileOutputStream(sf);
            w = new OutputStreamWriter(fos);
            while ((p = inIn.receive()) != null) {
                double _timeout = 10.0;
                longWaitStart(_timeout);
                try {
                    w.write((String) p.getContent());
                } catch (IOException e) {
                    System.err.println(e.getMessage() + " - component: " + this.getName());
                }
                w.write(linesep);
                longWaitEnd();
                //if (fp != null)
                w.flush();
                drop(p);
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inIn.close();
    }

    @Override
    protected void openPorts() {
      inIn = openInput("IN");

    }
}
