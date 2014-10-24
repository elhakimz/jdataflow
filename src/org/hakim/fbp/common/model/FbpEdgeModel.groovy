package org.hakim.fbp.common.model

/**
 * Purpose:
 * Edge model for FBP network
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpEdgeModel {

    FbpNodePort source= new FbpNodePort()
    FbpNodePort target= new FbpNodePort()
    int route

    void setSourcePort(int node, String port){
        source.node = node
        source.port = port
    }
    void setTargetPort(int node, String port){
       target.node = node
       target.port = port
    }

    public class FbpNodePort{
        int node
        String port
        int count = 0
    }
}
