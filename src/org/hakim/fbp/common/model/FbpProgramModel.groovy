package org.hakim.fbp.common.model

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/28/14.
 */
class FbpProgramModel {
    String id = "ID";
    String name = "MyProgram";
    String description = "description";

    List<String> components = new ArrayList<>();
    List<String> iips = new ArrayList<>();
    List<String> connections = new ArrayList<>();
    List<FbpProgramModel> subnets = new ArrayList<>();
}
