package org.hakim.fbp.common;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubNet;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import groovy.lang.GString;
import org.hakim.fbp.common.model.FbpCompDefModel;
import org.hakim.fbp.util.Settings;
import org.hakim.zen.pageflow.components.PageFlowComponent;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/8/14.
 */
public class FlowCompJsBuilder {

    private static FlowCompJsBuilder instance;

    private FlowCompJsBuilder() {

    }

    public static FlowCompJsBuilder getInstance() {
        if (instance == null) instance = new FlowCompJsBuilder();
        return instance;
    }

    public String buildFbpCompToJs(Class<?> klass) {
        String data = "";
        Template temp = null;
        Writer out = null;

        try {
            temp = Settings.getInstance().getFreeMarkerConfig().getTemplate("fbp_comp_def.ftl");
            Map<String, Object> root = new HashMap<>();
            root.put("user", "abiel");
            FbpCompDefModel model = convertFbpCompToModel(klass);
            root.put("model", model);
            out = new StringWriter();
            temp.process(root, out);

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        if (out != null) {
            return out.toString();
        }
        return "";

    }

    /**
     * convert FBP Components
     *
     * @param klass
     * @return
     */
    private FbpCompDefModel convertFbpCompToModel(Class<?> klass) {
        FbpCompDefModel model = new FbpCompDefModel();
        String name = klass.getName();
        String label = "";


        if (name.startsWith("org.hakim.zen.pageflow.components")) {
            name = name.replace("org.hakim.zen.pageflow.components.", "pageflow.");
        }
        model.setJavaType(klass.getName());
        model.setLabel(name.substring(name.lastIndexOf(".") + 1));
        model.setName(name);
        model.setWidth(200);


        if (name.startsWith("pageflow.")) {
            model.setIcon("sitemap");
            model.setColor("hsl(200, 90%, 78%)");
        }

        if (klass.getSuperclass().equals(SubNet.class)) {
            model.setCompType("subnet");
            model.setIcon("sitemap");
            model.setColor("hsl( 60, 68%, 70%)");
        }

        if (name.endsWith("Error")) {
            model.setColor("hsl(  0, 68%, 70%)");
        } else if (name.endsWith("Service")) {
            model.setColor("hsl(120, 68%, 70%)");
        }

        PageFlowComponent componentDescription = klass.getAnnotation(PageFlowComponent.class);
        if (componentDescription != null) model.setDescription(componentDescription.value());

        Map<String, String> ipwMap = new HashMap<>();
        InPortWidget ipw = klass.getAnnotation(InPortWidget.class);
        if (ipw != null) ipwMap.put(ipw.value(), ipw.widget());

        InPortWidgets ipws = klass.getAnnotation(InPortWidgets.class);
        if (ipws != null) {
            for (InPortWidget ipw2 : ipws.value()) {
                ipwMap.put(ipw2.value(), ipw2.widget());
            }
        }

        int n1 = 0;
        for (InPort it : FbpLibReader.getInPortsFrom(klass)) {
            n1++;
            FbpCompDefModel.FbpPort port = new FbpCompDefModel.FbpPort();
            port.setName(it.value());
            port.setType(getJsonType(it.type()));
            port.setValue("");
            port.setMultiple(String.valueOf(it.arrayPort()));
            port.setDescription(it.description());

            if (ipwMap.get(it.value()) != null) {
                port.setWidget(ipwMap.get(it.value()));
            } else {
                port.setWidget("");
            }
            model.getInputs().add(port);
        }


        int n2 = 0;
        for (OutPort it : FbpLibReader.getOutPortsFrom(klass)) {
            n2++;
            FbpCompDefModel.FbpPort port = new FbpCompDefModel.FbpPort();
            port.setName(it.value());
            port.setType(getJsonType(it.type()));
            port.setValue("");
            port.setMultiple(String.valueOf(it.arrayPort()));
            port.setDescription(it.description());
            model.getOutputs().add(port);
        }


        if (n1 > n2) model.setHeight(75 * n1);
        else model.setHeight(75 * n2);

        return model;
    }

    /**
     * get json type
     *
     * @param type
     * @return
     */
    private String getJsonType(Class type) {
        if (type.equals(String.class)) {
            return "string";
        } else if (type.equals(GString.class)) {
            return "text";
        } else if (type.equals(Integer.class)) {
            return "int";
        } else if (type.equals(Float.class)) {
            return "float";
        } else if (type.equals(Boolean.class)) {
            return "boolean";
        } else if (type.equals(DateTime.class)) {
            return "date";
        } else if (type.equals(java.sql.Connection.class)) {
            return "dbconn";
        } else if (type.equals(Map.class)) {
            return "map";
        } else if (type.equals(List.class)) {
            return "list";
        } else {
            return "all";
        }

    }


    /**
     * get Fbp Comp Script List
     *
     * @param packageName
     * @return
     */
    public String getFlowCompScriptJs(String packageName) {
        String scripts = "\n";
        java.util.Date date = new java.util.Date();

        for (Class cls : FbpLibReader.getFbpComponentClasses(packageName)) {
            String scriptName = cls.getName();
            String str = "<script type=\"\" src=\"/pageflowcomponent?id=" + scriptName + "&time=" + date.getTime() + "\"></script>\n";
            scripts += str;
        }

        return scripts;

    }


}
