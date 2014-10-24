package org.hakim.fbp.common.model

/**
 * Purpose:
 * node model for FBP component initialization
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpNodeModel {
    int id                  //node id
    String label            //node label
    String type             //node data type
    String classType        //

    int x                   //node x position
    int y                   //node y position
    int w                   //node width
    int h                   //node height
    Map state=[:]           //state
    String inputType        // input type
    FbpGraphModel graph     //subgraph


    @Override
    public String toString() {
        return "FbpNodeModel{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", classType='" + classType + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                ", state=" + state +
                ", inputType='" + inputType + '\'' +
                ", graph=" + graph +
                '}';
    }
}
