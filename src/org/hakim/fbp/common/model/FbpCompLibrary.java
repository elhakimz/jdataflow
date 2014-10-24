package org.hakim.fbp.common.model;

import com.jpmorrsn.fbp.engine.*;
import org.hakim.fbp.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/24/14.
 */
public class FbpCompLibrary {

    private HashMap<String, Class> mComponents = new HashMap<String, Class>();

    public FbpCompLibrary() {

    }
    public FbpCompLibrary(String fbpPath) throws Exception {
        InputStream in = new FileInputStream(fbpPath);
        loadFromJson(Util.stringFromStream(in));
    }

    public void loadFromJson(String fbpFileContent) throws JSONException {
        JSONObject libs = null;
        try {
            JSONTokener tokener = new JSONTokener(fbpFileContent);
            JSONObject root = new JSONObject(tokener);
            JSONObject rt = root.getJSONObject("javafbp");
            libs = rt.getJSONObject("libraries");
        } catch (Exception e) {
            System.err.println("Unable to parse fbp.json: " + e.toString());
        }

        Iterable<String> libNames = new Util.JSONObjectKeysIterable(libs);
        for (String libName : libNames) {
            JSONObject lib = libs.getJSONObject(libName);
            String classPath = lib.getString("_classpath");
            JSONObject components = lib.optJSONObject("components");
            Iterable<String> names = new Util.JSONObjectKeysIterable(components);
            buildComponentMap(libName, names, classPath);
        }
    }

    private void buildComponentMap(String libName, Iterable<String> componentNames, String baseLib) {
        for (String name : componentNames) {
            final String fullName = libName+"/"+name;
            final String className = baseLib + "." + name;
            try {
                Class c = Class.forName(className);
                mComponents.put(fullName, c);
            } catch (Exception e) {
                System.err.println("Cannot load component " + fullName + ": " + e.toString());
                e.printStackTrace();
            }
        }
    }


    public Map<String, Class> getComponents() { return mComponents; }

    public Class getComponent(String componentName) { return mComponents.get(componentName); }

    private static List<InPort> getInports(Class comp) {
        ArrayList<InPort> ret = new ArrayList<InPort>();
        InPort p = (InPort)comp.getAnnotation(InPort.class);
        if (p != null) {
            ret.add(p);
        }
        InPorts ports = (InPorts)comp.getAnnotation(InPorts.class);
        if (ports != null) {
            for (InPort ip : ports.value()) {
                ret.add(ip);
            }
        }
        return ret;
    }
    private static List<OutPort> getOutports(Class comp) {
        ArrayList<OutPort> ret = new ArrayList<OutPort>();
        OutPort p = (OutPort)comp.getAnnotation(OutPort.class);
        if (p != null) {
            ret.add(p);
        }
        OutPorts ports = (OutPorts)comp.getAnnotation(OutPorts.class);
        if (ports != null) {
            for (OutPort op : ports.value()) {
                ret.add(op);
            }
        }
        return ret;
    }
    private static String getDescription(Class comp) {
        String description = "";
        ComponentDescription a = (ComponentDescription)comp.getAnnotation(ComponentDescription.class);
        if (a != null) {
            description = a.value();
        }
        return description;
    }

    public JSONObject getComponentInfoJson(String componentName) throws JSONException {
        // Have to instantiate the component to introspect :(
        Class componentClass = mComponents.get(componentName);

        // Top-level
        JSONObject def = new JSONObject();
        def.put("name", componentName);
        def.put("description", getDescription(componentClass));
        def.put("subgraph", false); // TODO: support subgraphs
        def.put("icon", "coffee"); // TODO: allow components to specify icon

        // InPorts
        JSONArray inPorts = new JSONArray();
        for (InPort port : FbpCompLibrary.getInports(componentClass)) {
            JSONObject portInfo = new JSONObject();
            portInfo.put("id", port.value());
            portInfo.put("type", FbpCompLibrary.mapPortType(port.type()));
            portInfo.put("description", port.description());
            portInfo.put("addressable", port.arrayPort());
            portInfo.put("required", !port.optional());
            inPorts.put(portInfo);
        }
        def.put("inPorts", inPorts);

        // OutPorts
        JSONArray outPorts = new JSONArray();
        for (OutPort port : FbpCompLibrary.getOutports(componentClass)) {
            JSONObject portInfo = new JSONObject();
            portInfo.put("id", port.value());
            portInfo.put("type", FbpCompLibrary.mapPortType(port.type()));
            portInfo.put("description", port.description());
            portInfo.put("addressable", port.arrayPort());
            portInfo.put("required", !port.optional());
            outPorts.put(portInfo);
        }
        def.put("outPorts", outPorts);

        return def;
    }

    // Return a FBP type string for a
    static String mapPortType(Class javaType) {
        if (javaType == String.class) {
            return "string";
        } else if (javaType == Boolean.class) {
            return "boolean";
        } else if (javaType == Hashtable.class) {
            return "object";
        } else {
            // Default
            return "any";
        }
    }

}
