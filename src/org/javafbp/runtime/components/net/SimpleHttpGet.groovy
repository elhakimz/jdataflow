package org.javafbp.runtime.components.net

import com.jpmorrsn.fbp.engine.*
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/26/14.
 */
@ComponentDescription('Simple HTTP GET with URL as parameter , returning string OUT')
@InPort(value = "URL", type = String.class, description = "Simple HTTP get with URL")
@OutPort(value = "OUT", description = "Result of HTTP get")
class SimpleHttpGet extends Component {
    private final String USER_AGENT = "Mozilla/5.0";

    private InputPort inUrl
    private OutputPort outOut

    @Override
    protected void execute() throws Exception {
        Packet p = inUrl.receive()
        String url = p.content
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.statusLine.statusCode
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.entity
                    return entity != null ? EntityUtils.toString(entity) : null
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status)
                }
            }
        }
        String responseBody = client.execute(request, responseHandler)
        outOut.send(create(responseBody.toString()))
        drop(p)
    }

    @Override
    protected void openPorts() {
        inUrl = openInput("URL")
        outOut = openOutput("OUT")
    }

}
