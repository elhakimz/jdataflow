package org.hakim.fbp.servlet

import com.jpmorrsn.fbp.engine.*
import org.apache.log4j.Logger
import org.hakim.fbp.common.model.FbpEdgeModel
import org.hakim.fbp.common.model.FbpGraphModel
import org.hakim.fbp.common.model.FbpNodeModel
import org.hakim.fbp.common.model.FbpProgramModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.json.JSONArray
import org.json.JSONObject

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/28/14.
 */
class FbpScriptGenerator {

    final static Logger logger = Logger.getLogger(FbpScriptGenerator.class);
    FbpGraphModel graphModel
    JSONArray params
    FbpProgramModel programModel = new FbpProgramModel()
    Map<Integer, FbpNodeModel> nodeModelMap = new HashMap<>()
    Map<String, FbpEdgeModel.FbpNodePort> srcPortMap = new HashMap<>()

    private int arrIdx = -1
    private String arrOutportName = ""
    boolean isSubnet = false

    def subnetInPortMap = [:]
    def subnetOutPortMap = [:]

    def subnetOutport = [:]
    def subnetInport = [:]
    String SUBNET_CLASS_NAME = 'com.jpmorrsn.fbp.engine.SubNet';

    public FbpScriptGenerator(FbpGraphModel graphModel, JSONArray params) {
        this.graphModel = graphModel;
        this.params = params;
    }

    public FbpScriptGenerator(FbpGraphModel graphModel) {
        this.graphModel = graphModel;
    }

    def define() {

        List<String> comps = genComponents()
        programModel.components = comps

        List<String> conns = genConnections()
        programModel.connections = conns

        List<String> iips = genIips()
        programModel.iips = iips
    }


    List<String> genComponents() {
        def comps = []
        def comp = ""
        Class cls
        List<FbpNodeModel> nodeModels = graphModel.nodes

        for (FbpNodeModel node : nodeModels) {

            nodeModelMap[node.id] = node;
            //println "registering node name= ${nodeModelMap[node.id].label}, ${nodeModelMap[node.id].id}"
            if (!node.getClassType().empty) {
                cls = Class.forName(node.classType)

                if (this.isSubnet && (cls.equals(SubIn.class) || cls.equals(SubOut.class))) {
                    if (cls.equals(SubIn.class))
                        subnetInport[node.id] = node.label
                    else if (cls.equals(SubOut.class))
                        subnetOutport[node.id] = node.label

                    comp = String.format("component(\"%s\",%s.class); //id=%d ", node.label, cls.name, node.id);

                } else if (!cls.equals(SubNet.class) && !cls.equals(Object.class)) {

                    comp = String.format("component(\"%s\",%s.class) //id=%d;", node.label, cls.name, node.id);

                } else if (cls.equals(SubNet.class) || node.graph) {
                    FbpGraphModel graph = node.graph
                    FbpScriptGenerator generator = new FbpScriptGenerator(graph)
                    generator.isSubnet = true
                    generator.define()
                    subnetInPortMap[node.id] = generator.subnetInport
                    subnetOutPortMap[node.id] = generator.subnetOutport
                    generator.programModel.name = node.label
                    programModel.subnets.add(generator.programModel)
                    comp = String.format("component(\"%s\",%s.class) //id=%d;", node.label, node.label, node.id);
                }
            } else {
                comp = "component(\"${node.label}\");"
            }
            comps.add(comp)
        }
        return comps
    }


    List<String> genConnections() {
        def conns = []
        List<FbpEdgeModel> edges = graphModel.edges
        //println "connecting ${edges.size()} edges"
        for (FbpEdgeModel edge : edges) {
            FbpEdgeModel.FbpNodePort source = edge.source;
            FbpEdgeModel.FbpNodePort target = edge.target;

            int sourceId = source.node
            int tgtId = target.node

            FbpNodeModel srcNode = nodeModelMap[sourceId]
            FbpNodeModel tgtNode = nodeModelMap[tgtId]

            String srcLabel = (srcNode != null ? srcNode.label : '*')

//            if(!srcNode){
//                println "STRUCTURE FAIL: srcNode id=null =>" + sourceId+" issubnet= "+isSubnet
//            }else{
//
//                if (srcNode.classType.trim().equals(SUBNET_CLASS_NAME)) {
//                    def subnetoutp=subnetOutPortMap[srcNode.id]
//                    srcLabel = subnetoutp[source.node]
//                } else {
//                    srcLabel = srcNode.label;
//                }
//            }


            String targetLabel = (tgtNode != null ? tgtNode.label : '*')
            if (targetLabel == '*') {
                //println "failed target id for labeling=" + tgtId
            }
//            if(!tgtNode){
//                println "STRUCTURE FAIL: tgtNode id=null =>" + tgtId+" issubnet= "+isSubnet
//            }else{
//                if(tgtNode.classType.trim().equals(SUBNET_CLASS_NAME)){
//                    def subnetinp = subnetInPortMap[tgtNode.id]
//                    targetLabel = subnetinp[target.node]
//                }else{
//                    targetLabel = tgtNode.label;
//                }
//            }

            String sourcePort
            if (String.valueOf(source.port).equals("99")) {
                sourcePort = "OUT"
            } else {
                sourcePort = source.port
            }

            String targetPort
            if (String.valueOf(target.port).equals("1")) {
                targetPort = "IN"
            } else {
                targetPort = target.port
            }


            String codeString

            Class cls = Class.forName(nodeModelMap[source.node].classType);
            OutPort outPort;
            outPort = (OutPort) cls.getAnnotation(OutPort.class);
            OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);

            if (outPorts != null) {
                for (OutPort op : outPorts.value()) {
                    if (op.arrayPort() && op.value().equals(sourcePort))
                        sourcePort = getArrOutPortName(srcLabel, source);
                }

            } else if (outPort != null && outPort.arrayPort()) {
                sourcePort = getArrOutPortName(srcLabel, source);
            }

            if (arrIdx != -1) {
                codeString = String.format("connect(component(\"%s\"), port(\"%s\",%d),component(\"%s\"), port(\"%s\"));"
                        , srcLabel, arrOutportName, arrIdx, targetLabel, targetPort);
                conns.add(codeString);
            } else {
                codeString = String.format("connect(\"%s.%s\",\"%s.%s\");", srcLabel, sourcePort, targetLabel, targetPort);
                conns.add(codeString);

            }
            arrIdx = -1;
//            codeString = String.format("connect(\"%s.%s\",\"%s.%s\");", srcLabel, sourcePort, targetLabel, targetPort);
//            conns.add(codeString);

        }
        return conns
    }


    List<String> genIips() {
        List<String> inits2 = new ArrayList<>();
        List<String> inits


        if (params != null) {
            inits = initializeEmptyValues(graphModel, params);
            inits2 = initializeValuesFromParam(params);
        } else {
            inits = initializeEmptyValues(graphModel, null);
        }
        inits.addAll(inits2);

        return inits;
    }

    String getArrOutPortName(String srcLabel, FbpEdgeModel.FbpNodePort source) {
        String ret;
        if (!srcPortMap.containsKey(srcLabel)) srcPortMap.put(srcLabel, source);
        FbpEdgeModel.FbpNodePort p = srcPortMap.get(srcLabel);
        String s = p.getPort();
        if (p.getPort().contains("[")) {
            s = p.getPort().substring(0, p.getPort().indexOf("["));
        }
        arrOutportName = s;
        ret = s + "[" + p.getCount() + "]";
        arrIdx = p.getCount();
        int n = p.getCount() + 1;
        p.setCount(n);
        return ret;
    }

    def initializeValuesFromParam(JSONArray jsonArray) throws ClassNotFoundException {
        def iips = []
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            FbpNodeModel node = getNodeModelByName(obj.getString("component"));
            Class cls = Class.forName(node.classType);
            if (cls == null) {
                cls = Object.class;
            }

            DateTime dt;
            Object value = obj.get("value");
            String comp = obj.getString("component");
            String prt = obj.getString("port");

            String iip;

            if (hasDateTimeInPort(cls, obj.getString("port"))) {
                dt = formatter.parseDateTime((String) value);
                iip = 'initialize(\'' + String.valueOf(dt) + '\',component(\'' + comp + '\'),port(\'' + prt + '\'));'
            } else {
                //initialize(value,component(comp),port(prt));
                String sValue;
                if (value instanceof String) {
                    sValue = "$value";
                    sValue = sValue.replace("\"", "\\\"");
                } else {
                    sValue = String.valueOf(value);
                }
                iip = 'initialize(\'' + sValue + '\',component(\'' + comp + '\'),port(\'' + prt + '\'));'
            }
            iips.add(iip);

            if (isSubnet) {

            }

        }
        return iips
    }

    /**
     * find an IIP if it is valued, then
     * @param graphModel1
     * @param iip
     * @return
     */
    boolean findIip(JSONArray jsonArray, String component, String port) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Object value = obj.get("value");
            String comp = obj.getString("component");
            String prt = obj.getString("port");

            if ((component.equals(comp)) && (port.equals(prt))) {
                return true;
            }
        }
        return false;
    }

    def initializeEmptyValues(FbpGraphModel graphModel, JSONArray jsonArray) {
        def iips = []
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for (FbpNodeModel node : graphModel.nodes) {
            // initialize(iip.data, component(iip.tgtNode), port(tgtPort));
            for (Object k : node.state.keySet()) {
                Object v = node.state.get(k);
                String lbl = node.label;
                DateTime dt;
                try {

                    boolean hasIip = false;
                    if (jsonArray != null) {
                        hasIip = findIip(jsonArray, lbl, (String) k);
                    };

                    if (!hasIip) {
                        Class cls = Class.forName(node.classType);
                        String s;
                        if (hasDateTimeInPort(cls, (String) k)) {
                            dt = formatter.parseDateTime((String) v);
                            s = String.format('initialize(\'%s\',component(\'%s\'),port(\'%s\'));', dt, lbl, String.valueOf(k));
                        } else if (hasIntegerInPort(cls, (String) k)) {
                            Integer i = Integer.parseInt((String) v);
                            s = String.format('initialize(%d,component(\'%s\'),port(\'%s\'));', i, lbl, String.valueOf(k));
                        } else {
                            String sv = String.valueOf(v)
                            if (sv.indexOf('"') > -1) sv = sv.replaceAll('"', '\"');
                            s = String.format('initialize(\'%s\',component(\'%s\'),port(\'%s\'));', sv, lbl, String.valueOf(k));
                        }

                        iips.add(s);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return iips
    }


    FbpNodeModel getNodeModelByName(String name) {
        for (FbpNodeModel node : graphModel.nodes) {
            if (node.label.contains(name)) {
                return node;
            }
        }
        return null;
    }

    static boolean hasDateTimeInPort(Class cls, String label) {
        return hasTypeInPort(cls, label, DateTime.class);
    }

    static boolean hasIntegerInPort(Class cls, String label) {
        return hasTypeInPort(cls, label, Integer.class);
    }

    static boolean hasTypeInPort(Class cls, String label, Class type) {
        if (cls.getAnnotation(InPort.class) != null) {
            InPort inPort = (InPort) cls.getAnnotation(InPort.class);
            if (inPort.type().equals(type) && inPort.value().equals(label)) {
                return true;
            }
        }

        if (cls.getAnnotation(InPorts.class) != null) {
            InPorts inPorts = (InPorts) cls.getAnnotation(InPorts.class);
            for (InPort inPort : inPorts.value()) {
                if (inPort.type().equals(type) && inPort.value().equals(label)) {
                    return true;
                }
            }
        }
        return false;
    }


}
