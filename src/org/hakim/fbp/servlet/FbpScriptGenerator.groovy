package org.hakim.fbp.servlet

import com.jpmorrsn.fbp.engine.*
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

    FbpGraphModel graphModel
    JSONArray params
    FbpProgramModel programModel = new FbpProgramModel()
    Map<Integer, FbpNodeModel> nodeModelMap = new HashMap<>()
    Map<String, FbpEdgeModel.FbpNodePort> srcPortMap = new HashMap<>()

    private int arrIdx = -1
    private String arrOutportName = ""

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
            println "genComponents node name= ${node.label}, ${node.id}"
            if (!node.getClassType().empty) {
                cls = Class.forName(node.classType)
                if (!cls.equals(SubNet.class) && !cls.equals(Object.class)) {
                    comp = String.format("\tcomponent(\"%s\",%s.class);\n", node.label, cls.name);
                } else if (node.graph) {
                    FbpGraphModel graph = node.graph
                    FbpScriptGenerator generator = new FbpScriptGenerator(graph)
                    generator.define()
                    generator.programModel.name = node.label
                    programModel.subnets.add(generator.programModel)
                    comp = String.format("\tcomponent(\"%s\",%s.class);\n", node.label, node.label);
                }
            } else {
                comp = "\tcomponent(\"${node.label}\");\n"
            }
            comps.add(comp)
        }
        return comps
    }

    List<String> genConnections() {
        def conns = []
        for (FbpEdgeModel edge : graphModel.edges) {
            FbpEdgeModel.FbpNodePort source = edge.source;
            FbpEdgeModel.FbpNodePort target = edge.target;

            String srcLabel
            srcLabel = nodeModelMap[source.node].label;
            String targetLabel = '*'

            try {
                targetLabel = nodeModelMap[target.node].label;
            } catch (e) {
            }

            Class cls = Class.forName(nodeModelMap[source.node].classType);
            OutPort outPort;
            outPort = (OutPort) cls.getAnnotation(OutPort.class);
            OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);

            if (outPorts != null) {
                for (OutPort op : outPorts.value()) {
                    if (op.arrayPort() && op.value().equals(source.getPort()))
                        source.port = getArrOutPortName(srcLabel, source);
                }

            } else if (outPort != null && outPort.arrayPort()) {
                source.port = getArrOutPortName(srcLabel, source);
            }

            if (arrIdx != -1) {
                String s = String.format("\tconnect(component(\"%s\"), port(\"%s\",%d),component(\"%s\"), port(\"%s\"));\n"
                        , srcLabel, arrOutportName, arrIdx, targetLabel, target.port);
                conns.add(s);
            } else {
                String s = String.format("\tconnect(\"%s.%s\",\"%s.%s\");\n", srcLabel, source.port, targetLabel, target.port);
                conns.add(s);

            }
            arrIdx = -1;

        }
        return conns
    }

    List<String> genIips() {
        if (params != null) {
            return initializeValuesFromParam(params);
        } else {
            return initializeEmptyValues(graphModel);
        }
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
                iip = String.format("\tinitialize(\"%s\",component(\"%s\"),port(\"%s\"));\n", String.valueOf(dt), comp, prt);
            } else {
                //initialize(value,component(comp),port(prt));
                String sValue;
                if (value instanceof String) {
                    sValue = "\"" + value + "\"";
                } else {
                    sValue = String.valueOf(value);
                }
                iip = String.format("\tinitialize(%s,component(\"%s\"),port(\"%s\"));\n", sValue, comp, prt);
            }
            iips.add(iip);
        }
        return iips
    }

    def initializeEmptyValues(FbpGraphModel graphModel) {
        def iips = []
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for (FbpNodeModel node : graphModel.nodes) {
            // initialize(iip.data, component(iip.tgtNode), port(tgtPort));
            for (Object k : node.state.keySet()) {
                Object v = node.state.get(k);
                String lbl = node.label;
                DateTime dt;
                try {
                    Class cls = Class.forName(node.classType);
                    String s;
                    if (hasDateTimeInPort(cls, (String) k)) {
                        dt = formatter.parseDateTime((String) v);
                        s = String.format("initialize(\"%s\",component(\"%s\"),port(\"%s\"));", dt, lbl, String.valueOf(k));
                    } else if (hasIntegerInPort(cls, (String) k)) {
                        Integer i = Integer.parseInt((String) v);
                        s = String.format("initialize(%d,component(\"%s\"),port(\"%s\"));", i, lbl, String.valueOf(k));
                    } else {
                        s = String.format("initialize(\"%s\",component(\"%s\"),port(\"%s\"));", String.valueOf(v), lbl, String.valueOf(k));
                    }
                    iips.add(s);
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
