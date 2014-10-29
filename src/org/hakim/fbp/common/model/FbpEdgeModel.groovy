package org.hakim.fbp.common.model

/**
 * Purpose:
 * Edge model for FBP network
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpEdgeModel {

    FbpNodePort source = new FbpNodePort()
    FbpNodePort target = new FbpNodePort()
    int route

    void setSourcePort(int node, def port) {
        source.node = node
        source.port = port
        if (port instanceof String) source.portName = port
    }

    void setTargetPort(int node, def port) {
        target.node = node
        target.port = port
        if (port instanceof String) target.portName = port

    }

    public class FbpNodePort {
        int node
        def port           //cant decide the type
        def portName       //port representation
        int count = 0
    }
}
