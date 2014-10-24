package org.hakim.fbp.socket;

import org.json.JSONObject;

import java.io.StringWriter;

public class Message {
    private JSONObject json;

    public Message(JSONObject json) {
        this.json = json;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    @Override
    public String toString(){

        return json.toString();
    }

}