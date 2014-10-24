package org.hakim.fbp.util;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.Objects;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/26/14.
 */
public class FbpGraphUtil {

    /**
     * get inport type
     * @param componentLabel
     * @param portLabel
     * @param graphModel
     * @return
     */
    static Class getInPortType(String componentLabel, String portLabel, FbpGraphModel graphModel){

      for(FbpNodeModel node:graphModel.getNodes()){

          if(node.getLabel().equals(componentLabel)){
              try {
                  Class cls=Class.forName(node.getClassType());
                  InPort inPort = (InPort) cls.getAnnotation(InPort.class);
                  if(inPort!=null && inPort.value().equals(portLabel)) return inPort.type();

                  InPorts inPorts = (InPorts) cls.getAnnotation(InPorts.class);
                  if(inPorts!=null){
                      for(InPort inp:inPorts.value()){
                          if(inp.value().equals(portLabel)) return inp.type();
                      }
                  }
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
              }
          }
      }
      return null;
    }

    /**
     * get out port type
     * @param componentLabel
     * @param portLabel
     * @param graphModel
     * @return
     */
    static Class getOutPortType(String componentLabel, String portLabel, FbpGraphModel graphModel){

        for(FbpNodeModel node:graphModel.getNodes()){

            if(node.getLabel().equals(componentLabel)){
                try {
                    Class cls=Class.forName(node.getClassType());
                    OutPort outPort = (OutPort) cls.getAnnotation(OutPort.class);
                    if(outPort!=null && outPort.value().equals(portLabel)) return outPort.type();

                    OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);
                    if(outPorts!=null){
                        for(OutPort outp:outPorts.value()){
                            if(outp.value().equals(portLabel)) return outp.type();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    static Class convertFbpType(String type){
        switch (type) {
            case "string":
                return String.class;
            case "int":
                return Integer.class;
            case "float":
                return Float.class;
            case "date":
                return DateTime.class;
            case "dbconn":
                return java.sql.Connection.class;
            case "map":
                return java.util.Map.class;
            case "list":
                return java.util.List.class;
            default:
                return Object.class;
        }


    }
}
