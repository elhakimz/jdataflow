package org.hakim.zen.templater.service;

import org.hakim.fbp.util.Settings;
import org.hakim.fbp.util.Util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/17/14.
 */
@Path("/templater")
public class TemplaterService {
    @GET
    @Path("list")
    @Produces("application/json")
    public String list() {
        System.out.println("Calling TemplaterService.list()");
        String json = Util.getJsonFileTree(Settings.SYS_APP_DIR + "/WEB-INF/template");
        return json;

    }

    @GET
    @Path("get/{file}")
    @Produces("text/plain")
    public String get(@PathParam("file") String fileName) {
        String fname = fileName.replace('|', '/');
        System.out.println("fname = " + fname);

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
}
