package org.hakim.fbp.servlet;

import com.jpmorrsn.fbp.engine.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.hakim.fbp.common.model.FbpEdgeModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.hakim.fbp.common.model.FbpProgramModel;
import org.hakim.fbp.util.Settings;
import org.hakim.fbp.util.Util;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/24/14.
 */
public class FbpRuntime {

    private FbpGraphModel graphModel;
    private JSONArray params;
    private FbpProgramModel networkModel = new FbpProgramModel();
    private FbpScriptGenerator fbpGenerator;
    private String fbpScriptName;

    public FbpRuntime(FbpGraphModel graphModel, JSONArray params) {
        this.graphModel = graphModel;
        this.params = params;
        fbpGenerator = new FbpScriptGenerator(this.graphModel, this.params);
    }

    public FbpRuntime(FbpGraphModel graphModel) {
        this.graphModel = graphModel;
        fbpGenerator = new FbpScriptGenerator(this.graphModel);
    }

    public void go() throws Exception {
        fbpGenerator.define();
        generateProgram();
        runScript(fbpScriptName);
    }

    public void generateProgram() {
        FbpProgramModel model = fbpGenerator.getProgramModel();
        Template temp;
        Writer out;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddssmm");
        DateTime dt = new DateTime();
        fbpScriptName = "FbpRunner" + dt.toString(formatter) + ".groovy";
        try {
            temp = Settings.getInstance().getFreeMarkerConfig().getTemplate("fbp/runner.java.ftl");
            Map<String, Object> root = new HashMap<>();
            root.put("user", "abiel");
            root.put("model", model);
            FileOutputStream fos = new FileOutputStream(Settings.SYS_APP_DIR + Settings.SYS_RUNTIME_DIR + "/" + fbpScriptName);
            out = new OutputStreamWriter(fos);
            temp.process(root, out);
            out.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    /**
     * run fbp script
     *
     * @param scriptName
     */
    public void runScript(String scriptName) {

        String[] roots = new String[]{Settings.SYS_APP_DIR + Settings.SYS_RUNTIME_DIR};
        Binding binding;
        try {
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            binding = new Binding();
            binding.setVariable("input", "world");
            gse.run(scriptName, binding);
            System.out.println(binding.getVariable("output"));
        } catch (IOException | ResourceException | ScriptException e) {
            e.printStackTrace();
        }
    }


    /**
     * create subnet at runtime
     *
     * @param graphModel graphModel
     * @return SubNet
     */

    private SubNet createSubNet(final FbpGraphModel graphModel) {
        return new SubNet() {

            private Map<Integer, FbpNodeModel> nodeMap = new HashMap<>();

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
                for (FbpNodeModel node : graphModel.getNodes()) {
                    nodeMap.put(node.getId(), node);
                    Class cls;
                    Util.writeLog("subnet->component(" + node.getLabel() + "," + node.getClassType() + ")");

                    if (node.getType().equals("dataflow-input")) {
                        component(node.getLabel(), SubIn.class);
                    } else if (node.getType().equals("dataflow-output")) {
                        component(node.getLabel(), SubOut.class);
                    }

                    if (!node.getClassType().isEmpty()) {
                        cls = Class.forName(node.getClassType());
                        component(node.getLabel(), cls);
                    }
                }

                //create connections
                for (FbpEdgeModel edge : graphModel.getEdges()) {
                    FbpEdgeModel.FbpNodePort source = edge.getSource();
                    FbpEdgeModel.FbpNodePort target = edge.getTarget();
                    String srcLabel = nodeMap.get(source.getNode()).getLabel();
                    String targetLabel = nodeMap.get(target.getNode()).getLabel();
                    Util.writeLog(String.format("subnet connect %s %s -> %s %s", srcLabel, source.getPort(), target.getPort(), targetLabel));
                    connect(component(srcLabel), port(source.getPort()),
                            component(targetLabel), port(target.getPort()));

                }

                //init packets
                for (FbpNodeModel node : graphModel.getNodes()) {
                    for (Object k : node.getState().keySet()) {
                        Object v = node.getState().get(k);
                        String lbl = node.getLabel();
                        initialize(v, component(lbl), port((String) k));
                        Util.writeLog("subnet initialize(" + v + "," + lbl + "," + k + ")");
                    }
                }

            }
        };
    }

    public FbpProgramModel getNetworkModel() {
        return networkModel;
    }


}
