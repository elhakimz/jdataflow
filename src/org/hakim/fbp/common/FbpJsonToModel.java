package org.hakim.fbp.common;

import org.hakim.fbp.common.model.FbpEdgeModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/24/14.
 */
public class FbpJsonToModel {

    JSONArray nodes;
    JSONArray edges;


    public FbpGraphModel convert(Object jsonstr) {
        FbpGraphModel graphModel = new FbpGraphModel();
        JSONObject json;

        if (jsonstr instanceof JSONObject) {
            json = (JSONObject) jsonstr;
        } else {
            json = new JSONObject((String) jsonstr);
        }


        nodes = json.getJSONArray("nodes");
        edges = json.getJSONArray("edges");

        //System.out.println("nodes.length = " + nodes.length());

        List<JSONObject> lst = new ArrayList<>();

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject obj = nodes.getJSONObject(i);
            lst.add(obj);
            //System.out.println("obj.get(\"label\") = " + obj.get("label"));
        }

        for (JSONObject jso : lst) {
            FbpNodeModel model = convertNodes(jso);
            graphModel.addNode(model);
        }

        List<JSONObject> lstEdge = new ArrayList<>();

        for (int i = 0; i < edges.length(); i++) {
            JSONObject obj = edges.getJSONObject(i);
            lstEdge.add(obj);
        }

        //System.out.println("lstEdge = " + lstEdge);

        for (JSONObject jso : lstEdge) {
            graphModel.addEdge(convertEdges(jso));

        }


        return graphModel;
    }

    /**
     * convert json object to fbp node model
     *
     * @param jsonObject
     * @return
     */
    private FbpNodeModel convertNodes(JSONObject jsonObject) {
        FbpNodeModel nodeModel = new FbpNodeModel();

        nodeModel.setId(jsonObject.getInt("id"));
        nodeModel.setClassType(jsonObject.getString("javaType"));
        nodeModel.setLabel(jsonObject.getString("label"));
        JSONObject state = jsonObject.getJSONObject("state");

        for (Object s : state.keySet()) {
            nodeModel.getState().put(s, state.get((String) s));
        }
        if (jsonObject.has("graph")) {
            FbpJsonToModel jsonToModel = new FbpJsonToModel();
            FbpGraphModel graphModel = jsonToModel.convert(jsonObject.get("graph"));
            nodeModel.setGraph(graphModel);
        }

        return nodeModel;

    }

    /**
     * convert edge to fbp edge model
     *
     * @param jsonObject
     * @return
     */
    private FbpEdgeModel convertEdges(JSONObject jsonObject) {
        FbpEdgeModel edgeModel = new FbpEdgeModel();
        JSONObject source = jsonObject.getJSONObject("source");
        JSONObject target = jsonObject.getJSONObject("target");
        edgeModel.setSourcePort(source.getInt("node"), String.valueOf(source.get("port")));
        edgeModel.setTargetPort(target.getInt("node"), String.valueOf(target.get("port")));
        edgeModel.setRoute(jsonObject.getInt("route"));

        return edgeModel;
    }

}
