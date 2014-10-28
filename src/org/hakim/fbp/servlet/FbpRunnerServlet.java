package org.hakim.fbp.servlet;

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

    FbpRuntime runtime;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String func = request.getParameter("func");
        String data = request.getParameter("data");

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);
        PrintStream console = System.out;

        switch (func) {
            case "go":
                try {
                    String log = null;
                    log = goFbp(data);
                    response.getWriter().println(log);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().println("-1");
                }

                break;
            case "get-param":
                response.setContentType("application/json");
                try {
                    response.getWriter().println(getInitialParam(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().print("-1");
                }

                break;
            case "go-param":
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
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private String getInitialParam(Object data) throws Exception {
        System.out.println("getting initial parameter..");
        Map<String, Object> flds = new LinkedHashMap<>();
        FbpGraphModel graphModel = FbpJsonToModel.convert(data);
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
            FbpGraphModel graphModel = FbpJsonToModel.convert(data);
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


}
