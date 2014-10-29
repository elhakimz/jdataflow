package org.hakim.fbp.servlet;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.common.model.FbpEdgeModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.hakim.fbp.common.model.FbpProgramModel;
import org.hakim.fbp.util.Util;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/28/14.
 */
public class FbpGenerator {
    private final FbpGraphModel graphModel;
    private final Map<Integer, FbpNodeModel> nodeModelMap = new HashMap<>();
    private final Map<String, Map> subNodeModelMap = new HashMap<>();
    private final Map<String, FbpEdgeModel.FbpNodePort> srcPortMap = new HashMap<>();
    private final Map<String, Map<String, FbpEdgeModel.FbpNodePort>> srcSubPortMap = new HashMap<>();
    private final FbpProgramModel networkModel = new FbpProgramModel();
    private Map<String, FbpEdgeModel.FbpNodePort> tgtPortMap = new HashMap<>();
    private JSONArray params;
    private int arrIdx = -1;
    private String arrOutportName = "";
    private Map<String, Integer> arrIdxMap = new HashMap<>();
    private Map<String, String> arrOutportNameMap = new HashMap<>();

    public FbpGenerator(FbpGraphModel graphModel, JSONArray params) {
        this.graphModel = graphModel;
        this.params = params;
    }

    public FbpGenerator(FbpGraphModel graphModel) {
        this.graphModel = graphModel;
    }

    protected void define() throws Exception {

        Util.writeLog("..Instantiating components..");
        for (FbpNodeModel node : graphModel.getNodes()) {
            nodeModelMap.put(node.getId(), node);
            Class cls;
            String comps = "";
            if (!node.getClassType().isEmpty()) {
                cls = Class.forName(node.getClassType());
                if (!cls.equals(SubNet.class) && !cls.equals(Object.class)) {

                    comps = String.format("\tcomponent(\"%s\",%s.class);\n", node.getLabel(), cls.getName());

                } else if (node.getGraph() != null) {
                    FbpGraphModel graph = node.getGraph();
                    Util.writeLog(graph.toString());
                    FbpProgramModel subnetProgramModel = new FbpProgramModel();
                    subnetProgramModel = createSubNet(node.getLabel(), graph, subnetProgramModel);
                    networkModel.getSubnets().add(subnetProgramModel);
                    comps = String.format("\tcomponent(\"%s\",%s.class);\n", node.getLabel(), node.getLabel());
                }
            } else {
                comps = MessageFormat.format("\tcomponent(\"{0}\");\n", node.getLabel());
            }
            networkModel.getComponents().add(comps);
        }

        Util.writeLog("..Connecting components..");

        for (FbpEdgeModel edge : graphModel.getEdges()) {
            FbpEdgeModel.FbpNodePort source = edge.getSource();
            FbpEdgeModel.FbpNodePort target = edge.getTarget();
            String srcLabel = nodeModelMap.get(source.getNode()).getLabel();
            String targetLabel = nodeModelMap.get(target.getNode()).getLabel();
            Class cls = Class.forName(nodeModelMap.get(source.getNode()).getClassType());
            OutPort outPort;
            outPort = (OutPort) cls.getAnnotation(OutPort.class);
            OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);

//            if (outPorts != null) {
//                for (OutPort op : outPorts.value()) {
//                    if (op.arrayPort() && op.value().equals(source.getPort()))
//                        source.setPort(getArrOutPortName(srcLabel, source));
//                }
//
//            } else if (outPort != null && outPort.arrayPort()) {
//                source.setPort(getArrOutPortName(srcLabel, source));
//            }

            if (arrIdx != -1) {
                String s = String.format("\tconnect(component(\"%s\"), port(\"%s\",%d),component(\"%s\"), port(\"%s\"));\n"
                        , srcLabel, arrOutportName, arrIdx, targetLabel, target.getPort());
                networkModel.getConnections().add(s);
                Util.writeLog(s);

            } else {
                String s = String.format("\tconnect(\"%s.%s\",\"%s.%s\");\n", srcLabel, source.getPort(), targetLabel, target.getPort());
                networkModel.getConnections().add(s);
                Util.writeLog(s);
            }
            arrIdx = -1;
        }

        Util.writeLog("..Initializing data packets..");
        if (params != null) {
            initializeValuesFromParam(params, networkModel);
        } else {
            initializeEmptyValues(graphModel, networkModel);
        }
    }

    /**
     * initialize values
     *
     * @throws ClassNotFoundException
     */
    void initializeEmptyValues(FbpGraphModel graphModel, FbpProgramModel networkModel) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for (FbpNodeModel node : graphModel.getNodes()) {
            // initialize(iip.data, component(iip.tgtNode), port(tgtPort));
            for (Object k : node.getState().keySet()) {
                Object v = node.getState().get(k);
                String lbl = node.getLabel();
                DateTime dt;
                try {
                    Class cls = Class.forName(node.getClassType());
                    String s;
                    if (hasDateTimeInPort(cls, (String) k)) {
                        dt = formatter.parseDateTime((String) v);
                        s = String.format("initialize(\"%s\",component(\"%s\"),port(\"%s\"));", dt, lbl, String.valueOf(k));
                        Util.writeLog(s);

                    } else if (hasIntegerInPort(cls, (String) k)) {
                        Integer i = Integer.parseInt((String) v);
                        s = String.format("initialize(%d,component(\"%s\"),port(\"%s\"));", i, lbl, String.valueOf(k));
                        Util.writeLog(s);
                    } else {
                        s = String.format("initialize(\"%s\",component(\"%s\"),port(\"%s\"));", String.valueOf(v), lbl, String.valueOf(k));
                        Util.writeLog(s);
                    }
                    networkModel.getIips().add(s);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * initialize values from param
     *
     * @param jsonArray
     * @throws ClassNotFoundException
     */
    void initializeValuesFromParam(JSONArray jsonArray, FbpProgramModel networkModel) throws ClassNotFoundException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            FbpNodeModel node = getNodeModelByName(obj.getString("component"));
            Class cls = Class.forName(node.getClassType());

            if (cls == null) {
                cls = Object.class;
            }

            DateTime dt;
            Object value = obj.get("value");

            String comp = obj.getString("component");
            String prt = obj.getString("port");

            String iips;

            if (hasDateTimeInPort(cls, obj.getString("port"))) {
                dt = formatter.parseDateTime((String) value);
                iips = String.format("\tinitialize(\"%s\",component(\"%s\"),port(\"%s\"));\n", String.valueOf(dt), comp, prt);
            } else {
                //initialize(value,component(comp),port(prt));
                String sValue;
                if (value instanceof String) {
                    sValue = "\"" + value + "\"";
                } else {
                    sValue = String.valueOf(value);
                }
                iips = String.format("\tinitialize(%s,component(\"%s\"),port(\"%s\"));\n", sValue, comp, prt);
            }

            networkModel.getIips().add(iips);
            Util.writeLog(iips);
        }
    }

    /**
     * get node model by name
     *
     * @param name
     * @return
     */
    FbpNodeModel getNodeModelByName(String name) {
        for (FbpNodeModel node : graphModel.getNodes()) {
            if (node.getLabel().contains(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * get array outport name
     *
     * @param srcLabel
     * @param source
     * @return
     */
    String getArrOutPortName(String srcLabel, FbpEdgeModel.FbpNodePort source) {
        String ret;
        if (!srcPortMap.containsKey(srcLabel)) srcPortMap.put(srcLabel, source);
        FbpEdgeModel.FbpNodePort p = srcPortMap.get(srcLabel);
        String s = String.valueOf(p.getPort());
        if (s.contains("[")) {
            s = s.substring(0, s.indexOf("["));
        }
        arrOutportName = s;
        ret = s + "[" + p.getCount() + "]";
        arrIdx = p.getCount();
        int n = p.getCount() + 1;
        p.setCount(n);
        return ret;
    }

    String getSubArrOutPortName(String srcLabel, FbpEdgeModel.FbpNodePort source, String subnetName) {
        String ret;
        Map<String, FbpEdgeModel.FbpNodePort> srcPortMap = srcSubPortMap.get(subnetName);
        if (!srcPortMap.containsKey(srcLabel)) srcPortMap.put(srcLabel, source);
        FbpEdgeModel.FbpNodePort p = srcPortMap.get(srcLabel);
        String s = String.valueOf(p.getPort());
        if (((String) p.getPort()).contains("[")) {
            s = ((String) p.getPort()).substring(0, ((String) p.getPort()).indexOf("["));
        }
        String arrOutportName = arrOutportNameMap.put(subnetName, s);
        ret = s + "[" + p.getCount() + "]";
        arrIdx = p.getCount();
        int n = p.getCount() + 1;
        p.setCount(n);
        return ret;
    }

    /**
     * check date time port
     *
     * @param cls
     * @param label
     * @return
     */
    boolean hasDateTimeInPort(Class cls, String label) {
        if (cls.getAnnotation(InPort.class) != null) {
            InPort inPort = (InPort) cls.getAnnotation(InPort.class);
            if (inPort.type().equals(DateTime.class) && inPort.value().equals(label)) {
                return true;
            }
        }

        if (cls.getAnnotation(InPorts.class) != null) {
            InPorts inPorts = (InPorts) cls.getAnnotation(InPorts.class);
            for (InPort inPort : inPorts.value()) {
                if (inPort.type().equals(DateTime.class) && inPort.value().equals(label)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasIntegerInPort(Class cls, String label) {
        return hasTypeInPort(cls, label, Integer.class);
    }

    boolean hasTypeInPort(Class cls, String label, Class type) {
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

    private FbpProgramModel createSubNet(String name, final FbpGraphModel graphModel, FbpProgramModel programModel) {
        programModel.setName(name);

        List<String> comps = createSubComponentInstances(graphModel, name);
        for (String comp : comps) {
            programModel.getComponents().add(comp);
        }

        List<String> conns = createSubConnections(graphModel, name, programModel);
        for (String conn : conns) {
            programModel.getConnections().add(conn);
        }

        Util.writeLog("..Initializing data packets..");
        initializeEmptyValues(graphModel, programModel);
        return programModel;

    }

    public FbpProgramModel getNetworkModel() {
        return networkModel;
    }

    /**
     * create component instance string
     *
     * @param graphModel
     * @return
     */
    List<String> createSubComponentInstances(FbpGraphModel graphModel, String subnetName) {
        List<String> strings = new ArrayList<>();
        Map<Integer, FbpNodeModel> nodeModelMap = new HashMap<>();


        for (FbpNodeModel node : graphModel.getNodes()) {

            nodeModelMap.put(node.getId(), node);
            Class cls = null;
            String comps = "";
            if (!node.getClassType().isEmpty()) {

                try {
                    cls = Class.forName(node.getClassType());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (cls != null && (!cls.equals(SubNet.class) && !cls.equals(Object.class))) {
                    comps = String.format("\tcomponent(\"%s\",%s.class);\n", node.getLabel(), cls.getName());
                } else if (node.getGraph() != null) {
                    FbpGraphModel graph = node.getGraph();
                    Util.writeLog(graph.toString());
                    FbpProgramModel subnetProgramModel = new FbpProgramModel();
                    createSubNet(node.getLabel(), graph, subnetProgramModel);
                    comps = String.format("\tcomponent(\"%s\",%s.class);\n", node.getLabel(), node.getClassType());
                }
            } else {
                comps = MessageFormat.format("\tcomponent(\"{0}\");\n", node.getLabel());
            }
            strings.add(comps);
        }
        if (subNodeModelMap.get(subnetName) == null) {
            subNodeModelMap.put(subnetName, nodeModelMap);
        }
        return strings;
    }

    List<String> createSubConnections(FbpGraphModel graphModel, String subnetName, FbpProgramModel networkModel) {
        List<String> strings = new ArrayList<>();
        Map<Integer, FbpNodeModel> nodeModelMap = subNodeModelMap.get(subnetName);

        for (FbpEdgeModel edge : graphModel.getEdges()) {
            FbpEdgeModel.FbpNodePort source = edge.getSource();
            FbpEdgeModel.FbpNodePort target = edge.getTarget();

            String srcLabel = nodeModelMap.get(source.getNode()).getLabel();
            String targetLabel = nodeModelMap.get(target.getNode()).getLabel();

            try {
                Class cls = Class.forName(nodeModelMap.get(source.getNode()).getClassType());
                OutPort outPort;
                outPort = (OutPort) cls.getAnnotation(OutPort.class);
                OutPorts outPorts = (OutPorts) cls.getAnnotation(OutPorts.class);

                if (outPorts != null) {
                    for (OutPort op : outPorts.value()) {
                        if (op.arrayPort() && op.value().equals(source.getPort()))
                            source.setPort(getSubArrOutPortName(srcLabel, source, subnetName));
                    }

                } else if (outPort != null && outPort.arrayPort()) {
                    source.setPort(getSubArrOutPortName(srcLabel, source, subnetName));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String s;

            int arrIdx = arrIdxMap.get(subnetName);
            String arrOutportName = arrOutportNameMap.get(subnetName);

            if (arrIdx != -1) {
                s = String.format("\tconnect(component(\"%s\"), port(\"%s\",%d),component(\"%s\"), port(\"%s\"));\n"
                        , srcLabel, arrOutportName, arrIdx, targetLabel, target.getPort());

            } else {
                s = String.format("\tconnect(\"%s.%s\",\"%s.%s\");\n", srcLabel, source.getPort(), targetLabel, target.getPort());

            }
            strings.add(s);
            arrIdx = arrIdxMap.get(subnetName) - 1;
        }
        return strings;
    }

}
