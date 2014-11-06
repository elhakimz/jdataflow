package org.hakim.fbp.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/19/14.
 */
public class Settings {
    public static final String PROPERTY_FILE = "Application.properties";
    public static final String COMP_CORE = "fbp.libs.core";
    public static final String COMP_LIBRARY = "fbp.corecomps";

    public static final String FBP_BASE_LIBS = "fbp.libs.base";
    public static final String FBP_IO_LIBS = "fbp.libs.io";
    public static final String FBP_DB_LIBS = "fbp.libs.db";
    public static final String FBP_NET_LIBS = "fbp.libs.net";
    public static final String FBP_UI_LIBS = "fbp.libs.ui";
    public static final String FBP_TEXT_LIBS = "fbp.libs.text";
    public static final String FBP_JSON_LIBS = "fbp.libs.json";

    public static final String COMP_GROUPS = "fbp.compgroups";
    public static final String SCRIPT_REPOSITORY = "fbp.repo";
    public static final String FTL_REPOSITORY = "ftl.repo";
    public static final String SYS_APP_DIR = (String) getInstance().getProperty("app.location");
    public static final String SYS_RUNTIME_DIR = (String) getInstance().getProperty("app.location.runtime");
    public static final String SYS_SCRIPT_DIR = (String) getInstance().getProperty("app.location.scripts");
    public static final String SYS_TEMPLATE_DIR = (String) getInstance().getProperty("app.location.template");
    public static final String SYS_REPOSITORY_DIR = (String) getInstance().getProperty("app.location.repository");
    public static final String SYS_DB_URL = (String) getInstance().getProperty("db.url");
    public static final String SYS_DB_USER = (String) getInstance().getProperty("db.user");
    public static final String SYS_DB_PASSWORD = (String) getInstance().getProperty("db.password");
    public static final String SYS_DB_TYPE = (String) getInstance().getProperty("db.type");
    public static final String SYS_DB_DRIVER = (String) getInstance().getProperty("db.driver");
    public static final String USER_APP_DIR = (String) getInstance().getProperty("user.location");
    public static final String USER_RUNTIME_DIR = (String) getInstance().getProperty("user.location.runtime");
    public static final String USER_SCRIPT_DIR = (String) getInstance().getProperty("user.location.scripts");
    public static final String USER_TEMPLATE_DIR = (String) getInstance().getProperty("user.location.template");
    public static String CONTEXT_PATH = "";
    private static Settings instance;
    Properties prop = new Properties();
    InputStream input = null;
    private Configuration freeMarkerConfig;

    private Settings() {

        try {
            input = this.getClass().getClassLoader().getResourceAsStream("Application.properties");
            // load a properties file
            System.out.println("input=" + input);
            prop.load(input);

            freeMarkerConfig = new Configuration();
            freeMarkerConfig.setDirectoryForTemplateLoading(new File(prop.getProperty(FTL_REPOSITORY)));
            freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
            freeMarkerConfig.setDefaultEncoding("UTF-8");
            freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            freeMarkerConfig.setIncompatibleImprovements(new Version(2, 3, 20));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getAppLocation() {
        return (String) getProperty("app.location");
    }

    public Object getProperty(String key) {
        return prop.getProperty(key);
    }

    public Configuration getFreeMarkerConfig() {
        return freeMarkerConfig;
    }

    public String getOutputFileName() {
        return SYS_APP_DIR + SYS_RUNTIME_DIR + "/fbp.out";
    }

}
