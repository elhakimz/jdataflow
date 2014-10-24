package org.hakim.zen.fbp.services

import org.hakim.fbp.util.Settings
import org.hakim.fbp.util.Util
import org.json.JSONArray

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/19/14.
 */
@Path("/fbp")
class FbpService {
    @GET
    @Path("list")
    @Produces("application/json")
    String list() {
        println("Calling list()");

        List<String> results = new ArrayList<>();
        File[] files = new File(Settings.SYS_APP_DIR+"/WEB-INF/repository/fbp/").listFiles();
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
    String getFile(@PathParam("file")String fileName) {
       String fname= fileName.replace('|','/');

       System.out.println("fname = " + fname);
       String fileContents = new File(fname).getText('UTF-8')
       return fileContents;
    }


    @GET
    @Path("run/{file}")
    @Produces("application/json")
    String runFbpJsonFile(@PathParam("file")String fileName){

        return ""
    }

    @GET
    @Path("gen/{file}")
    @Produces("application/json")
    String genFbpJava(@PathParam("file")String fileName){


        return ""
    }


    @POST
    @Path("save/{file}/{content}")
    @Produces("application/json")
    String saveFbp(@PathParam("file")String fileName,@PathParam("content") String content){

       String fname="";
       if(fileName.contains("/")){
           fname= fileName.replace('|','/');
       }

       if(!fname.endsWith(".json"))  fname+".json"

       if(!fname.startsWith(Settings.SYS_APP_DIR)){
         fname = Settings.SYS_APP_DIR+Settings.SYS_REPOSITORY_DIR+"/programs/"+fileName
       }

        System.out.println("fname = " + fname);

        try {
            PrintWriter writer = new PrintWriter(fname, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (e) {
            return "{'status':'0','message':'error: $e.message'}"
        }

        return "{'status':'1'}"

    }


    @GET
    @Path("listtree")
    @Produces("application/json")
    public String listTree() {
        System.out.println("Calling TemplaterService.list()");
        String json =Util.getJsonFileTree(Settings.SYS_APP_DIR+Settings.SYS_REPOSITORY_DIR);
        return json;
    }



}
