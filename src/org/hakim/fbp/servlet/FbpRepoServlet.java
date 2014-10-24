package org.hakim.fbp.servlet;

import org.json.JSONArray;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/18/14.
 */
public class FbpRepoServlet extends HttpServlet {
    String contextPath;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      //saves a json to .json file

      ServletContext servletContext = getServletContext();
      contextPath  = servletContext.getRealPath(File.separator);

      String func=request.getParameter("func");
      String name=request.getParameter("file");
      if(name==null) name="";
      DateFormat sdf=SimpleDateFormat.getInstance();
      if(name.isEmpty()) name="fbp"+sdf.format(new Date());
      if(!name.endsWith(".json")) name=name+".json";
      String data=request.getParameter("data");

      System.out.println("data = " + data);
      System.out.println("func = " + func);
      System.out.println("name = " + name);

      if(func.equals("save")){
          try {

              saveFile(name,data);
              response.getWriter().print("1");
          } catch (Exception e) {
              e.printStackTrace();
              response.getWriter().print("-1");
          }
      } else if(func.equals("load")){

          try {
              response.setContentType("application/json");
              response.getWriter().print(readFile(name));
          } catch (Exception e) {
              e.printStackTrace();
              response.getWriter().print("-1");
          }

      }else if(func.equals("list")){
          try {
             response.setContentType("application/json");
             response.getWriter().print(listFile());
          } catch (Exception e) {
              e.printStackTrace();
              response.getWriter().print("-1");
          }
      }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      //read json from .json file
    }

    /**
     * save file
     * @param fileName file name
     * @param content  content of data
     * @throws Exception
     */
    void saveFile(String fileName, String content) throws Exception{
        String toFile=contextPath+"WEB-INF/repository/"+fileName;
        System.out.println();
        PrintWriter writer = new PrintWriter(toFile, "UTF-8");
        writer.println(content);
        writer.close();
    }

    /**
     * read file
     * @param fileName file name
     * @return
     * @throws Exception
     */
    String readFile(String fileName) throws Exception{
        String toFile=contextPath+"WEB-INF/repository/"+fileName;

        try(BufferedReader br = new BufferedReader(new FileReader(toFile))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    /**
     * list files from repo
     * @return
     * @throws Exception
     */
    JSONArray listFile() throws Exception{
        List<String> results = new ArrayList<>();
        File[] files = new File(contextPath+"WEB-INF/repository").listFiles();

        assert files != null;

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        StringBuilder sb = new StringBuilder();

        return new JSONArray(results.toArray());
    }

}
