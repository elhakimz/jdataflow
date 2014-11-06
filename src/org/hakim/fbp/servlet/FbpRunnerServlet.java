package org.hakim.fbp.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hakim.fbp.common.FbpJsonToModel;
import org.hakim.fbp.common.model.FbpGraphModel;
import org.hakim.fbp.common.model.FbpNodeModel;
import org.hakim.fbp.util.JsonFormUtil;
import org.hakim.fbp.util.Settings;
import org.hakim.fbp.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Purpose:
 * read a json then run the FBP routine
 * run the script
 *
 * @author abilhakim
 *         Date: 9/18/14.
 */
public class FbpRunnerServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(FbpRunnerServlet.class);
    FbpRuntime runtime;
    String token;
    String PROGRAM_DIR = Settings.SYS_APP_DIR + "/WEB-INF/repository/programs";
    String SCRIPT_DIR = Settings.SYS_APP_DIR + Settings.SYS_SCRIPT_DIR;
    String RUNTIME_DIR = Settings.SYS_APP_DIR + Settings.SYS_RUNTIME_DIR;
    String APP_PARAMETER = "APP-PARAMETER";
    String PARAM_SCHEMA = "PARAMETER-SCHEMA";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String func = request.getParameter("func");
        String data = request.getParameter("data");
        String fname = request.getParameter("file");


        token = request.getParameter("token");
        if (token == null) {
            token = String.valueOf(new Date().getTime());
        }

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);
        PrintStream console;

        switch (func) {
            case "go":
                try {
                    console = System.out;
                    String log = null;
                    log = goFbp(data);
                    response.getWriter().println(log);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().println("-1");
                }

                break;

            case "get-param":
                console = System.out;
                response.setContentType("application/json");
                try {
                    response.getWriter().println(getInitialParam(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().print("-1");
                }

                break;
            case "go-param":
                console = System.out;
                JSONObject json = new JSONObject(data);
                try {
                    String graph = json.getString("data");
                    String param = json.getString("params");
                    JSONArray paramz = new JSONArray(param);
                    String result = goFbp(graph, paramz);
                    response.getWriter().print(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().print("-1");
                }

                break;

            case "get-form":
                response.setContentType("application/json");
                response.getWriter().write(getParams(fname));

                break;

            case "run-file":
                response.setContentType("application/json");
                response.getWriter().write(goFbpFile(fname, data, null));

                break;

            case "run-program":
                response.setContentType("application/json");
                response.getWriter().write(runProgramFile(fname, data));

                break;
            case "list-program":
                response.setContentType("application/json");
                response.getWriter().write(listProgramFiles());
                break;

            case "list-json":
                response.setContentType("application/json");
                response.getWriter().write(listJsonFiles());
                break;

        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private String getInitialParam(Object data) throws Exception {
        logger.info("get initial parameter " + String.valueOf(data));
        Map<String, Object> flds = new LinkedHashMap<>();
        FbpJsonToModel jsonToModel = new FbpJsonToModel();
        FbpGraphModel graphModel = jsonToModel.convert(data);
        for (FbpNodeModel node : graphModel.getNodes()) {
            for (Object k : node.getState().keySet()) {
                Object v = node.getState().get(k);
                String lbl = node.getLabel();
                flds.put(lbl + "." + k, v);
            }
        }
        JSONObject form = JsonFormUtil.createForm(flds);
        return form.toString();
    }


    /**
     * run fbp
     *
     * @param data
     * @return
     */
    private String goFbp(String data) throws Exception {
        return goFbp(data, null);
    }

    /**
     * Run FBP Graph
     *
     * @param data
     * @return
     */
    private String goFbp(String data, Object params) throws Exception {

        FbpRuntime runtime;
        PrintStream console = System.out;
        String LOG_FILE_NAME = Settings.CONTEXT_PATH + "WEB-INF/runtime/log.txt";

        File file = new File(LOG_FILE_NAME);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();

        try {
            FbpJsonToModel jsonToModel = new FbpJsonToModel();
            FbpGraphModel graphModel = jsonToModel.convert(data);
            if (params == null) json.put("message", "go");
            else
                json.put("message", "go-param");
            JSONArray paramz;
            if (params instanceof JSONArray) {
                paramz = (JSONArray) params;
            } else {
                paramz = new JSONArray(params);
            }
            runtime = new FbpRuntime(graphModel, paramz);
            Util.writeLog("..executing flow..");
            runtime.go();
            Util.writeLog("execution SUCCESS");
            json.put("status", "1");
        } catch (Exception e) {
            e.printStackTrace();
            Util.writeLog("ERROR in executing flow:\n" + e.getMessage());
            json.put("message", "FBP programm had errors");
            json.put("status", "-1");
        }
        String log = "";
        System.setOut(console);
        try {
            log = Util.readFile(LOG_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return log;

    }


    /**
     * run programfile
     *
     * @param programFile
     * @return
     */
    private String runProgramFile(String programFile, String param) {
        String file = Settings.SYS_APP_DIR + "/WEB-INF/scripts/" + programFile;
        JSONObject paramData = new JSONObject(param);


        return "";
    }


    private String getParams(String file) {
        logger.info("getting form parameter");
        String fileName = PROGRAM_DIR + "/" + file + ".fbp.json";
        String everything;

        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            everything = IOUtils.toString(inputStream);
            return getFormParam(everything);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{message:'none'}";
    }


    /**
     * list program files
     *
     * @return
     */
    private String listProgramFiles() {


        return "";
    }

    /**
     * list json files
     *
     * @return
     */
    private String listJsonFiles() {

        return "";
    }

    /**
     * get form param
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String getFormParam(Object data) throws Exception {
        FbpJsonToModel jsonToModel = new FbpJsonToModel();
        FbpGraphModel graphModel = jsonToModel.convert(data);
        String params = "{}";

        for (FbpNodeModel node : graphModel.getNodes()) {
            for (Object k : node.getState().keySet()) {
                Object v = node.getState().get(k);
                String lbl = node.getLabel();
                if (lbl.equals(APP_PARAMETER) && k.equals("IN")) {
                    params = String.valueOf(v);
                }
            }
        }

        JSONObject jsonParams = new JSONObject(params);
        return buildJsonForm(jsonParams);
    }

    /**
     * build for json editor
     *
     * @param jsonParams
     * @return
     */
    String buildJsonForm(JSONObject jsonParams) {
        JSONObject jsonForm = new JSONObject();
        jsonForm.put("type", "object");
        JSONObject prop = new JSONObject();

        for (Object k : jsonParams.keySet()) {
            JSONObject fld = new JSONObject();
            String type = "string";
            String stype = ((String) k);
            if (stype.contains("|")) {
                type = stype.substring(stype.indexOf('|') + 1);
            }
            fld.put("type", type);
            fld.put("value", jsonParams.get(String.valueOf(k)));

            String name = (String) k;
            if (name.contains("|")) {
                name = name.substring(0, name.indexOf('|'));
            }
            prop.put(name, fld);
        }

        jsonForm.put("properties", prop);
        return jsonForm.toString();
    }

    /**
     * run fbp file
     *
     * @param fname
     * @param params
     * @return
     * @throws Exception
     */
    private String goFbpFile(String fname, Object params, String token) {
        FbpRuntime runtime;
        String fileName = PROGRAM_DIR + "/" + fname + ".fbp.json";
        String everything;

        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            everything = IOUtils.toString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return "{status:-1,message:'ERROR'}";
        }

        JSONObject json = new JSONObject();

        try {
            FbpJsonToModel jsonToModel = new FbpJsonToModel();
            FbpGraphModel graphModel = jsonToModel.convert(everything);
            JSONArray paramz;
            if (params instanceof JSONArray) {
                paramz = (JSONArray) params;
            } else {
                logger.info(params);
                Gson gson = new Gson(); //TODO this is suck
                ArrayList obj = new Gson().fromJson((String) params, new TypeToken<ArrayList<Object>>() {
                }.getType());
                paramz = new JSONArray(obj);
            }

            logger.info(paramz.toString());
            runtime = new FbpRuntime(graphModel, paramz);
            runtime.go();
            json.put("status", "1");
            json.put("message", "Success");
            String data = Util.readFile(Settings.getInstance().getOutputFileName());
            json.put("data", data);

        } catch (Exception e) {
            e.printStackTrace();
            json.put("message", "FBP programm had errors");
            json.put("status", "-1");
        }

        return json.toString();
    }
}
