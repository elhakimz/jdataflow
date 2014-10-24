package org.hakim.fbp.util

import org.json.JSONArray
import org.json.JSONObject

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/26/14.
 */
class JsonFormUtil {

    private static JSONObject createHeader(){
      JSONObject header=new JSONObject();

      return header;
    }

    /**
     * create form
     * @param fields
     * @return
     */
    public static JSONObject createForm(Map fields){
       JSONObject form = new JSONObject();


       JSONArray array = new JSONArray();
       JSONObject fld = new JSONObject()
       fld.put "type","p"
       fld.put"html","Please enter values"

       array.put fld

       for(String k:fields.keySet()){

          fld = new JSONObject()
          fld.put("id",k)
          fld.put"caption",k
          fld.put "type","text"
          fld.put "value",fields[k]

          array.put(fld)
           fld = new JSONObject()
           fld.put "type","div"
           array.put(fld)

       }

        form.put "html", array
        return form
    }


    private static JSONObject createFooter(){
        JSONObject footer=new JSONObject();
        return footer;
    }

}
