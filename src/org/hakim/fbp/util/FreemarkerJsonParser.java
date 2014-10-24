package org.hakim.fbp.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Map;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/22/14.
 */
// a class to parse Json, just add this method to your rendered template data
// with data.put("JsonParser", new FreemarkerJsonParser());
// or in shared variables http://freemarker.sourceforge.net/docs/pgui_config_sharedvariables.html
public class FreemarkerJsonParser implements TemplateMethodModel {
    @Override
    public Object exec(List args) throws TemplateModelException {
        String s=((String) args.get(0));
        return new Gson().fromJson(s, new TypeToken<Map<String, String>>() {}.getType());

    }
}
