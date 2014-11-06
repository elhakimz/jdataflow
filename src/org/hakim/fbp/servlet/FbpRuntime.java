package org.hakim.fbp.servlet;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.log4j.Logger;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpProgramModel;
import org.hakim.fbp.util.Settings;
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

    final static Logger logger = Logger.getLogger(FbpRuntime.class);
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
            logger.info("Processing FBP runner");
            temp = Settings.getInstance().getFreeMarkerConfig().getTemplate("fbp/runner.java.ftl");
            Map<String, Object> root = new HashMap<>();
            root.put("user", "abiel");
            root.put("model", model);
            FileOutputStream fos = new FileOutputStream(Settings.SYS_APP_DIR + Settings.SYS_RUNTIME_DIR + "/" + fbpScriptName);
            out = new OutputStreamWriter(fos);
            temp.process(root, out);
            out.close();
            logger.info("End runtime");

        } catch (IOException | TemplateException e) {
            logger.error(e.getMessage());
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


    public FbpProgramModel getNetworkModel() {
        return networkModel;
    }


}
