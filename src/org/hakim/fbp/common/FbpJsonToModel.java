package org.hakim.fbp.common;

import org.hakim.fbp.common.model.FbpEdgeModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/24/14.
 */
public class FbpJsonToModel {

     static JSONArray nodes;
     static JSONArray edges;


     public static FbpGraphModel convert(Object jsonstr){
         FbpGraphModel graphModel = new FbpGraphModel();
         JSONObject json;

         if(jsonstr instanceof JSONObject){
             json = (JSONObject)jsonstr;
         } else {
              json = new JSONObject((String)jsonstr);
         }

         nodes =  json.getJSONArray("nodes");
         edges = json.getJSONArray("edges");

         for(int i=0;i<nodes.length();i++){
           graphModel.addNode(convertNodes(nodes.getJSONObject(i)));
         }

         for(int i=0;i<edges.length();i++){
             graphModel.addEdge(convertEdges(edges.getJSONObject(i)));
         }

         return  graphModel;
    }

    /**
     * convert json object to fbp node model
     * @param jsonObject
     * @return
     */
    private static FbpNodeModel convertNodes(JSONObject jsonObject){
        FbpNodeModel nodeModel=new FbpNodeModel();
        nodeModel.setId(jsonObject.getInt("id"));
        nodeModel.setClassType(jsonObject.getString("javaType"));
        nodeModel.setLabel(jsonObject.getString("label"));

        JSONObject state=jsonObject.getJSONObject("state");

        for(Object s:state.keySet()){
          nodeModel.getState().put(s,state.get((String)s));
        }

        if(jsonObject.has("graph")){
            FbpGraphModel graphModel= convert(jsonObject.get("graph"));
            nodeModel.setGraph(graphModel);
        }

        return nodeModel;

    }

    /**
     * convert edge to fbp edge model
     * @param jsonObject
     * @return
     */
    private static FbpEdgeModel convertEdges(JSONObject jsonObject){
        FbpEdgeModel edgeModel = new FbpEdgeModel();
        JSONObject source = jsonObject.getJSONObject("source");
        JSONObject target = jsonObject.getJSONObject("target");

        edgeModel.setSourcePort(source.getInt("node"), (String) source.get("port"));
        System.out.println("target = " + target);
        edgeModel.setTargetPort(target.getInt("node"), (String) target.get("port"));

        edgeModel.setRoute(jsonObject.getInt("route"));

        return  edgeModel;
    }

}
