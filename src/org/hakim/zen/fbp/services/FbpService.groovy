package org.hakim.zen.fbp.services

import org.apache.log4j.Logger
import org.hakim.fbp.util.Settings
import org.hakim.fbp.util.Util
import org.json.JSONArray
import org.json.JSONObject

import javax.ws.rs.*

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/19/14.
 */
@Path("/fbp")
class FbpService {

    final static Logger logger = Logger.getLogger(FbpService.class);


    @GET
    @Path("list")
    @Produces("application/json")
    String list() {
        println("Calling list()");
        List<String> results = new ArrayList<>();
        File[] files = new File(Settings.SYS_APP_DIR + "/WEB-INF/repository/fbp/").listFiles();
        println("files = " + files);
        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        JSONArray jsonArray = new JSONArray(results.toArray());
        return jsonArray.toString();
    }

    @GET
    @Path("get/{file}")
    @Produces("text/plain")
    String getFile(@PathParam("file") String fileName) {
        String fname = fileName.replace('|', '/');
        System.out.println("fname = " + fname);
        String fileContents = new File(fname).getText('UTF-8')
        return fileContents;
    }


    @GET
    @Path("run/{file}")
    @Produces("application/json")
    String runFbpJsonFile(@PathParam("file") String fileName) {

        return ""
    }

    @GET
    @Path("gen/{file}")
    @Produces("application/json")
    String genFbpJava(@PathParam("file") String fileName) {

        return ""
    }


    @POST
    @Path("save/{file}/{content}")
    @Produces("application/json")
    String saveFbp(@PathParam("file") String fileName, @PathParam("content") String content) {
        String fname = "";
        if (fileName.contains("|")) {
            fname = fileName.replace('|', '/');
        } else (fname = fileName)

        if (!fname.endsWith(".fbp.json")) fname = fname + ".fbp.json"

        if (!fname.startsWith(Settings.SYS_APP_DIR)) {
            fname = Settings.SYS_APP_DIR + Settings.SYS_REPOSITORY_DIR + "/programs/" + fname
        }

        logger.info("saving " + fname);
        JSONObject json = new JSONObject()

        try {
            PrintWriter writer = new PrintWriter(fname, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (e) {
            logger.error(e.message)
            json.put("status", -1);
            json.put("message", "error ${e.message}")
            return json.toString();
        }
        json.put('status', 1)
        json.put('message', 'Success')
        return json.toString()
    }


    @GET
    @Path("listtree")
    @Produces("application/json")
    public String listTree() {
        System.out.println("Calling TemplaterService.list()");
        String json = Util.getJsonFileTree(Settings.SYS_APP_DIR + Settings.SYS_REPOSITORY_DIR);
        return json;
    }


}
