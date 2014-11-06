package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.*;
import org.apache.log4j.Logger;
import org.hakim.fbp.util.Settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Purpose:
 * Write output file
 *
 * @author abilhakim
 *         Date: 10/10/14.
 */
@ComponentDescription("Write IN to system file output with Token as filename ex XYZ123.out.txt")
@InPort(value = "IN", type = String.class, description = "Input data in string")
public class WriteOutputFile extends Component {

    final static Logger logger = Logger.getLogger(WriteOutputFile.class);
    private static String linesep = System.getProperty("line.separator");
    InputPort inIn;

    @Override
    protected void execute() throws Exception {
        Packet p;
        String sf = Settings.getInstance().getOutputFileName();
        Writer w;
        try {
            FileOutputStream fos = new FileOutputStream(sf);
            w = new OutputStreamWriter(fos);
            p = inIn.receive();
            try {
                String s = String.valueOf(p.getContent());
                logger.info(s);
                w.write(s);
            } catch (IOException e) {
                logger.error(e.getMessage() + " - component: " + this.getName());
            }
            w.write(linesep);
            w.flush();
            drop(p);
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
