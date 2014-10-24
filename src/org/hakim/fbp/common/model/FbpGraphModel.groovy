package org.hakim.fbp.common.model

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpGraphModel implements Serializable {
    String runtimeMode = 'Server'
    List<FbpNodeModel> nodes = []
    List<FbpInitModel> inits = []
    List<FbpEdgeModel> edges = []

    void addNode(FbpNodeModel nodeModel){
      nodes.add(nodeModel);
    }

    void addEdge(FbpEdgeModel edgeModel){
      edges.add(edgeModel);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FbpGraphModel{");
        sb.append("runtimeMode='").append(runtimeMode).append('\'');
        sb.append(", nodes=").append(nodes);
        sb.append(", inits=").append(inits);
        sb.append(", edges=").append(edges);
        sb.append('}');
        return sb.toString();
    }
}
