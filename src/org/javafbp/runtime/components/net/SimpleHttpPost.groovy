package org.javafbp.runtime.components.net

import com.jpmorrsn.fbp.engine.*

import javax.net.ssl.HttpsURLConnection

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/26/14.
 */
@ComponentDescription('Simple HTTP POST with URL and PARAM as parameter , returning string OUT')
@InPorts([@InPort(value = "URL", type = String.class, description = "Simple HTTP get with URL")
        , @InPort(value = "PARAM", description = "Post Parameter in String")])
@OutPort(value = "OUT", description = "Result of HTTP get")
class SimpleHttpPost extends Component {
    private final String USER_AGENT = "Mozilla/5.0";
    private InputPort inUrl
    private InputPort inParam
    private OutputPort outOut

    @Override
    protected void execute() throws Exception {
        Packet p1 = inUrl.receive()
        Packet p2 = inParam.receive()

        String url = p1.content;
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.requestMethod = "POST";
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = p2.content;

        // Send post request
        con.doOutput = true;
        DataOutputStream wr = new DataOutputStream(con.outputStream);
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.responseCode;
        println("\nSending 'POST' request to URL : " + url);
        println("Post parameters : " + urlParameters);
        println("Response Code : " + responseCode);


        InputStreamReader isr = new InputStreamReader(con.inputStream)
        BufferedReader buf = new BufferedReader(isr);
        String inputLine;
        StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = buf.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (e) {
            println(e.message)
        }
        buf.close();
        outOut.send(create(response.toString()))
        inUrl.close()
        inParam.close()
        drop(p1)
        drop(p2)
    }

    @Override
    protected void openPorts() {
        inUrl = openInput("URL")
        inParam = openInput("PARAM")
        outOut = openOutput("OUT")
    }
}
