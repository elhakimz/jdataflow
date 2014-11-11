package org.hakim.zen.planner.service;

import org.hakim.fbp.util.Settings;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/13/14.
 */
@Path("/planner")
public class PlannerService {

    @GET
    @Path("list")
    @Produces("application/json")
    public String list() {
        System.out.println("Calling list()");

        List<String> results = new ArrayList<>();
        File[] files = new File(Settings.getInstance().getSysLocation() + "/WEB-INF/repository/planner/").listFiles();
        System.out.println("files = " + files);

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
    @Produces("application/json")
    public String get(@PathParam("file") String fileName) {
        String fname = Settings.getInstance().getSysLocation() + "/WEB-INF/repository/planner/" + fileName;

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{}";
    }

    @POST
    @Path("save/{file}/{content}")
    @Produces("application/json")
    public String saveAs(@PathParam("file") String fileName, @PathParam("content") String content) {
        String fname = fileName;
        if (!fname.endsWith(".json")) {
            fname = fname + ".json";
        }

        String toFile = Settings.getInstance().getSysLocation() + "/WEB-INF/repository/planner/" + fname;
        System.out.println("save as " + toFile);
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(toFile, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "{status:-1}";
        }
        return "{status:1}";
    }
}
