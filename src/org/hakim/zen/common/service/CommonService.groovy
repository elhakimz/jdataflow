package org.hakim.zen.common.service

import org.hakim.fbp.util.Settings

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

import static org.hakim.fbp.util.Util.getJsonFileTree

/**
 * Purpose: Common service
 * @author abilhakim
 * Date: 10/23/14.
 */
@Path("/common")
class CommonService {
    @GET
    @Path("listtempl")
    @Produces("application/json")
    def listTempl() {
        println("Calling CommonService.listtpl()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR + Settings.SYS_TEMPLATE_DIR);
        return json;
    }

    @GET
    @Path("listdir")
    @Produces("application/json")
    def listDir() {
        println("Calling CommonService.listdir()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR);
        return json;
    }

    @GET
    @Path("listscript")
    @Produces("application/json")
    def listScript() {
        println("Calling CommonService.listscript()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR + Settings.SYS_SCRIPT_DIR);
        return json;
    }

    @GET
    @Path("listabs/{rootpath}")
    @Produces("application/json")
    def listAbsolute(@PathParam("rootpath") String rootPath) {
        println("Calling CommonService.listabs()");
        String json = getJsonFileTree(rootPath)
        return json;
    }

}
