package org.hakim.zen.common.service

import org.apache.log4j.Logger
import org.hakim.fbp.util.Settings
import org.hakim.fbp.util.Util
import org.json.JSONArray

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

    final static Logger logger = Logger.getLogger(CommonService.class);

    @GET
    @Path("listtempl")
    @Produces("application/json")
    def listTempl() {
        logger.info("Calling CommonService.listtpl()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR + Settings.SYS_TEMPLATE_DIR);
        return json;
    }

    @GET
    @Path("listdir")
    @Produces("application/json")
    def listDir() {
        logger.info("Calling CommonService.listdir()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR);
        return json;
    }

    /**
     * get available application description
     * @return
     */
    @GET
    @Path("listapps")
    @Produces("application/json")
    def listApps() {
        logger.info("Calling CommonService.listapps()");
        JSONArray ret = new JSONArray();
        String s = "{}"
        try {
            s = Util.readFile(Settings.APPS_LOCATION + "/" + Settings.APPS_CONFIG);
        } catch (e) {
            logger.error(e.message)
        }
        return s
    }


    @GET
    @Path("listappdir/{path}")
    @Produces("application/json")
    def listAppDir(@PathParam("path") String path) {
        logger.info("Calling CommonService.listdir()");
        String json = getJsonFileTree("${Settings.APPS_LOCATION}/${path}");
        return json;
    }


    @GET
    @Path("listscript")
    @Produces("application/json")
    def listScript() {
        logger.info("Calling CommonService.listscript()");
        String json = getJsonFileTree(Settings.SYS_APP_DIR + Settings.SYS_SCRIPT_DIR);
        return json;
    }

    @GET
    @Path("listabs/{rootpath}")
    @Produces("application/json")
    def listAbsolute(@PathParam("rootpath") String rootPath) {
        logger.info("Calling CommonService.listabs()");
        String json = getJsonFileTree(rootPath)
        return json;
    }

}
