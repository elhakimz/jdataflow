package org.hakim.fbp.common;

import com.jpmorrsn.fbp.engine.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.hakim.fbp.common.model.FbpCompDefModel;
import org.hakim.fbp.util.Settings;
import org.javafbp.runtime.pattern.InPortWidget;
import org.javafbp.runtime.pattern.InPortWidgets;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/19/14.
 */
public class FbpCompJsBuilder {

    private static FbpCompJsBuilder instance;

    public static FbpCompJsBuilder getInstance(){
      if(instance==null) instance=new FbpCompJsBuilder();
       return instance;
    }

    private FbpCompJsBuilder(){

    }


    public  String buildFbpCompToJs(Class<?> klass) {
        String data="";
        Template temp = null;
        Writer out=null;

        try {
            temp = Settings.getInstance().getFreeMarkerConfig().getTemplate("fbp_comp_def.ftl");
            Map<String,Object> root = new HashMap<>();
            root.put("user", "abiel");
            FbpCompDefModel model = convertFbpCompToModel(klass);
            root.put("model", model);
            out = new StringWriter();
            temp.process(root, out);

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        if(out!=null){
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
    private  FbpCompDefModel convertFbpCompToModel(Class<?> klass) {
        FbpCompDefModel model = new FbpCompDefModel();
        String name = klass.getName();
        String label="";

        if (name.startsWith("com.jpmorrsn.fbp.components.")) {
            name = name.replace("com.jpmorrsn.fbp.components.", "core.");
        }else if(name.startsWith("org.javafbp.runtime.components.")){
            name = name.replace("org.javafbp.runtime.components.","");
        }else if(name.startsWith((String) Settings.getInstance().getProperty(Settings.FBP_TEXT_LIBS))){
            name = name.replace("com.jpmorrsn.fbp.text.","text.");
        }else if(name.startsWith("com.jpmorrsn.fbp.examples.components.")){
            name=name.replace("com.jpmorrsn.fbp.examples.components.","examples.");
        }else if(name.startsWith((String) Settings.getInstance().getProperty(Settings.FBP_JSON_LIBS))){
            name = name.replace("com.jpmorrsn.fbp.json.","json.");
        }
        model.setJavaType(klass.getName());
        model.setLabel(name.substring(name.lastIndexOf(".")+1));
        model.setName(name);
        model.setWidth(200);


        if(name.startsWith("db.")) {
            model.setIcon("table");
            model.setColor("hsl(120, 68%, 70%)");
        }
        else if(name.startsWith("base.")) {
            model.setIcon("tag");
            model.setColor("hsl(210, 68%, 70%)");
        }
        else if(name.startsWith("ftl."))
            model.setIcon("code");
        else if(name.startsWith("flow."))
            model.setIcon("link");
        else if(name.startsWith("json.")) {
            model.setIcon("coffee");
            model.setColor("hsl( 60, 68%, 70%)");
        }else if(name.startsWith("os.")) {
            model.setIcon("linux");
            model.setColor("hsl(240, 68%, 70%)");
        }else if(name.startsWith("net.")) {
            model.setIcon("net");
            model.setColor("hsl(200, 90%, 78%)");
        }else if(name.startsWith("core.")){
            model.setColor("hsl(219, 16%, 76%)");
        }else if(name.startsWith("zen.")){
            model.setColor("hsl(270, 68%, 70%)");
        }if(klass.getSuperclass().equals(Component.class)){
            model.setCompType("component");
        }else if(klass.getSuperclass().equals(SubNet.class)){
            model.setCompType("subnet");
            model.setIcon("sitemap");
            model.setColor("hsl( 60, 68%, 70%)");
        }

        ComponentDescription componentDescription = klass.getAnnotation(ComponentDescription.class);
        if(componentDescription!=null) model.setDescription(componentDescription.value());

        Map<String,String> ipwMap = new HashMap<>();
        InPortWidget ipw= klass.getAnnotation(InPortWidget.class);
        if(ipw!=null) ipwMap.put(ipw.value(),ipw.widget());

        InPortWidgets ipws=klass.getAnnotation(InPortWidgets.class);
        if(ipws!=null){
            for(InPortWidget ipw2: ipws.value()){
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

            if(ipwMap.get(it.value())!=null){
                port.setWidget(ipwMap.get(it.value()));
            }else{
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
    private  String getJsonType(Class type) {
        if (type.equals(String.class)) {
            return "string";
        } else if (type.equals(Integer.class)) {
            return "int";
        } else if (type.equals(Float.class)) {
            return "float";
        } else if (type.equals(Boolean.class)) {
            return "boolean";
        } else if(type.equals(DateTime.class)){
            return "date";
        } else if(type.equals(Connection.class)){
            return "dbconn";
        } else if(type.equals(Map.class)){
            return "map";
        } else if(type.equals(List.class)){
            return "list";
        }else {
            return "all";
        }

    }


    /**
     * get Fbp Comp Script List
     * @param packageName
     * @return
     */
    public  String getFbpCompScriptList(String packageName) {
        String scripts = "\n";
        Date date = new Date();

        for(Class cls:FbpLibReader.getFbpComponentClasses(packageName)){
            String scriptName = cls.getName();
            String str = "<script type=\"\" src=\"/component?id=" + scriptName + "&time="+date.getTime()+"\"></script>\n";
            scripts+=str;
        }

        return scripts;

    }


}
