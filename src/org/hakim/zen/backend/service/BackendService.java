package org.hakim.zen.backend.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/31/14.
 */
@Path("/backend")
public class BackendService {

    @GET
    @Path("list")
    @Produces("application/json")
    String list() {


        return "";
    }


    @GET
    @Path("get/{id}")
    @Produces("application/json")
    String get(@PathParam("id") int id) {
        return "";
    }

    @GET
    @Path("update/{data}")
    @Produces("application/json")
    String update(@PathParam("data") String data) {
        return "";
    }

    @GET
    @Path("insert")
    @Produces("application/json")
    String insert(@PathParam("data") String data) {
        return "";
    }


    @GET
    @Path("delete")
    @Produces("application/json")
    String delete(@PathParam("id") int id) {
        return "";
    }

}
