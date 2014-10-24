package org.hakim.fbp.servlet;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.common.model.FbpEdgeModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.hakim.fbp.util.Util;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/24/14.
 */
public class FbpRuntime extends Network {


    private FbpGraphModel graphModel;
    private Map<Integer,FbpNodeModel> nodeModelMap = new HashMap<>();
    private Map<String,FbpEdgeModel.FbpNodePort> tgtPortMap =new HashMap<>();
    private Map<String,FbpEdgeModel.FbpNodePort> srcPortMap =new HashMap<>();
    private JSONArray params;


    public FbpRuntime(FbpGraphModel graphModel,JSONArray params) {
        this.graphModel = graphModel;
        this.params = params;
    }

    public FbpRuntime(FbpGraphModel graphModel) {
        this.graphModel = graphModel;
    }

    @Override
    protected void define() throws Exception {

      Util.writeLog("..Instantiating components..");
      for(FbpNodeModel node:graphModel.getNodes()){
          nodeModelMap.put(node.getId(), node);
          Class cls=null;
          Util.writeLog("component(" + node.getLabel() + "," + node.getClassType() + ")");
          if(!node.getClassType().isEmpty()){
              cls = Class.forName(node.getClassType());
            if(!cls.equals(SubNet.class) && !cls.equals(Object.class)){

                component(node.getLabel(),cls);


            } else if(node.getGraph()!=null) {

                FbpGraphModel graph = node.getGraph();
                Util.writeLog(graph.toString());
                Class<? extends SubNet> tpe =createSubNet(graph).getClass();
                Util.writeLog("create subnet class="+tpe.getName());
                component(node.getLabel(), tpe);
            }

          }else{
              component(node.getLabel());
          }
      }

        Util.writeLog("..Connecting components..");


      for(FbpEdgeModel edge:graphModel.getEdges()){
          FbpEdgeModel.FbpNodePort source=edge.getSource();
          FbpEdgeModel.FbpNodePort target = edge.getTarget();
          String srcLabel = nodeModelMap.get(source.getNode()).getLabel();
          String targetLabel = nodeModelMap.get(target.getNode()).getLabel();

          Class cls = Class.forName(nodeModelMap.get(source.getNode()).getClassType());
          OutPort outPort;

          outPort = (OutPort) cls.getAnnotation(OutPort.class);
          OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);

          if(outPorts!=null){
              for(OutPort op:outPorts.value()){
                  if(op.arrayPort() && op.value().equals(source.getPort()))
                  source.setPort(getArrOutPortName(srcLabel,source));
              }

          }else if(outPort!=null && outPort.arrayPort()){
              source.setPort(getArrOutPortName(srcLabel,source));
          }


          if(arrIdx != -1){

            connect(component(srcLabel), port(arrOutportName,arrIdx),
                      component(targetLabel), port(target.getPort()));

              //connect(srcLabel+"."+source.getPort(),targetLabel+"."+target.getPort());

              Util.writeLog(String.format("%s %s -> %s %s", srcLabel, arrOutportName+":"+arrIdx, target.getPort(), targetLabel));

          }else{
              connect(srcLabel+"."+source.getPort(),targetLabel+"."+target.getPort());
//              connect(component(srcLabel), port(source.getPort()),
//                      component(targetLabel), port(target.getPort()));
              Util.writeLog(String.format("%s %s -> %s %s", srcLabel, source.getPort(), target.getPort(), targetLabel));

          }
          arrIdx = -1;


      }

        Util.writeLog("..Initializing data packets..");
        if(params!=null){
           initializeValuesFromParam(params);
        }else{
            initializeValues();
        }


    }

    void initializeValues() throws ClassNotFoundException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for(FbpNodeModel node:graphModel.getNodes()){
            // initialize(iip.data, component(iip.tgtNode), port(tgtPort));
            for(Object k:node.getState().keySet()){
                Object v = node.getState().get(k);
                String lbl = node.getLabel();
                DateTime dt;

                Class cls=Class.forName(node.getClassType());
                if(hasDateTimeInPort(cls, (String) k)){
                    dt = formatter.parseDateTime((String)v);
                    initialize(dt,component(lbl),port((String)k));
                    Util.writeLog("initialize(" + dt + "," + lbl + "," + k + ")");
                }else if(hasIntegerInPort(cls, (String) k)){
                    Integer i=Integer.parseInt((String)v);
                    initialize(i,component(lbl),port((String)k));
                    Util.writeLog("initialize(" + i + "," + lbl + "," + k + ")");
                }else {
                    initialize(v,component(lbl),port((String)k));
                    Util.writeLog("initialize(" + v + "," + lbl + "," + k + ")");
                }


            }

        }
    }

    /**
     * initialize values from param
     * @param jsonArray
     * @throws ClassNotFoundException
     */
    void initializeValuesFromParam(JSONArray jsonArray) throws ClassNotFoundException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject obj=jsonArray.getJSONObject(i);
            FbpNodeModel node= getNodeModelByName(obj.getString("component"));
            Class cls=Class.forName(node.getClassType());

            if(cls!=null){

            }

            DateTime dt=null;
            Object value=obj.get("value");

            String comp=obj.getString("component");
            String prt=obj.getString("port");
            Util.writeLog("initialize(" + obj.getString("value") + "," + comp + "," + prt + ")");

            if(hasDateTimeInPort(cls, obj.getString("port"))){
                dt = formatter.parseDateTime((String) value);
                initialize(dt,component(comp),port(prt));
            } else {
                initialize(value,component(comp),port(prt));
            }
        }
    }

    FbpNodeModel getNodeModelByName(String name){
      for(FbpNodeModel node:graphModel.getNodes()){
          if(node.getLabel().contains(name)){
              return node;
          }
      }
      return null;
    }

    int arrIdx=-1;
    String arrOutportName="";

    String getArrOutPortName(String srcLabel,FbpEdgeModel.FbpNodePort source){
        String ret="";
        if(!srcPortMap.containsKey(srcLabel))srcPortMap.put(srcLabel,source);
        FbpEdgeModel.FbpNodePort p = srcPortMap.get(srcLabel);
        String s=p.getPort();

        if(p.getPort().contains("[")){
           s=p.getPort().substring(0,p.getPort().indexOf("["));
        }

        arrOutportName = s;
        ret=s + "[" + p.getCount() + "]";
        arrIdx=p.getCount();
        int n= p.getCount()+1;
        p.setCount(n);
        return ret;
    }

    /**
     * check date time port
     * @param cls
     * @param label
     * @return
     */
    boolean hasDateTimeInPort(Class cls, String label){

            if(cls.getAnnotation(InPort.class)!=null){
                InPort inPort= (InPort) cls.getAnnotation(InPort.class);
                if(inPort.type().equals(DateTime.class) && inPort.value().equals(label)){
                  return true;
                }
            }

            if(cls.getAnnotation(InPorts.class)!=null){
                InPorts inPorts = (InPorts)cls.getAnnotation(InPorts.class);
                for(InPort inPort:inPorts.value()){
                    if(inPort.type().equals(DateTime.class) && inPort.value().equals(label)){
                       return true;
                    }
                }
            }

      return false;
    }

    boolean hasIntegerInPort(Class cls,String label){
      return hasTypeInPort(cls, label,Integer.class);
    }

    boolean hasTypeInPort(Class cls,String label, Class type){

        if(cls.getAnnotation(InPort.class)!=null){
            InPort inPort= (InPort) cls.getAnnotation(InPort.class);
            if(inPort.type().equals(type) && inPort.value().equals(label)){
                return true;
            }
        }

        if(cls.getAnnotation(InPorts.class)!=null){
            InPorts inPorts = (InPorts)cls.getAnnotation(InPorts.class);
            for(InPort inPort:inPorts.value()){
                if(inPort.type().equals(type) && inPort.value().equals(label)){
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * create subnet at runtime
     * @param graphModel graphModel
     * @return SubNet
     */
    private SubNet createSubNet(final FbpGraphModel graphModel){

        return new SubNet() {

            private Map<Integer,FbpNodeModel> nodeMap = new HashMap<>();
            @Override
            protected HashMap<String, InputPort> getInports() {
                //generate inports here
                return super.getInports();
            }

            @Override
            protected HashMap<String, OutputPort> getOutports() {
                //create outports here
                return super.getOutports();
            }

            @Override
            protected void define() throws Exception {
                Util.writeLog("..define subnet..");
                //create components
                for(FbpNodeModel node:graphModel.getNodes()){
                    nodeMap.put(node.getId(), node);
                    Class cls=null;
                    Util.writeLog("subnet->component(" + node.getLabel() + "," + node.getClassType() + ")");

                    if(node.getType().equals("dataflow-input")){
                        component(node.getLabel(), SubIn.class);
                    }else if(node.getType().equals("dataflow-output")){
                        component(node.getLabel(), SubOut.class);
                    }

                    if(!node.getClassType().isEmpty()){
                        cls = Class.forName(node.getClassType());
                        component(node.getLabel(),cls);
                    }
                }

                //create connections
                for(FbpEdgeModel edge:graphModel.getEdges()){
                    FbpEdgeModel.FbpNodePort source=edge.getSource();
                    FbpEdgeModel.FbpNodePort target = edge.getTarget();
                    String srcLabel = nodeMap.get(source.getNode()).getLabel();
                    String targetLabel = nodeMap.get(target.getNode()).getLabel();
                    Util.writeLog(String.format("subnet connect %s %s -> %s %s", srcLabel, source.getPort(), target.getPort(), targetLabel));
                    connect(component(srcLabel),port(source.getPort()),
                            component(targetLabel),port(target.getPort()));

                }

                //init packets
                for(FbpNodeModel node:graphModel.getNodes()){
                    for(Object k:node.getState().keySet()){
                        Object v = node.getState().get(k);
                        String lbl = node.getLabel();
                        initialize(v,component(lbl),port((String)k));
                        Util.writeLog("subnet initialize(" + v + "," + lbl + "," + k + ")");
                    }
                }

            }
        };
    }



}
