package org.hakim.fbp.servlet;

import org.hakim.fbp.common.FlowCompJsBuilder;
import org.hakim.fbp.util.Settings;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Purpose:
 * generate JS description of component through reflection ex /component?op=gen&id=com.jpmorsn.components.Collate
 * using templating
 *
 * @author abilhakim
 *         Date: 9/18/14.
 */
public class PageFlowComponentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        Settings.CONTEXT_PATH = servletContext.getRealPath(File.separator);

        String className = request.getParameter("id");
        String out = "";
        try {
            out = FlowCompJsBuilder.getInstance().buildFbpCompToJs(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        final PrintWriter writer = response.getWriter();
        writer.println(out);

    }

}
