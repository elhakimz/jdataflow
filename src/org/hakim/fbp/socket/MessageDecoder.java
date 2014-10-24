package org.hakim.fbp.socket;

import groovy.json.JsonException;
import org.json.JSONObject;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/26/14.
 */
public class MessageDecoder implements Decoder.Text<Message>{

    @Override
    public Message decode(String string) throws DecodeException {
        JSONObject json = new JSONObject(string);
        return new Message(json);
    }

    @Override
    public boolean willDecode(String string) {
        try{
            JSONObject json = new JSONObject(string);
            return true;
        }catch (JsonException ex){
            System.out.println(string);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        System.out.println("init");
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }

}
