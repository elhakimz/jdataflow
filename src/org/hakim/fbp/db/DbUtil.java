package org.hakim.fbp.db;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/31/14.
 */
public class DbUtil {


    public static Object convert(Object obj, String dbType) {

        return new Object();
    }

    /**
     * db to javascript converter
     *
     * @param dbType
     * @return
     */
    public static String getJsDataType(String dbType) {
        String type = dbType.toUpperCase().trim();
        switch (type) {
            case "CHAR":
            case "VARCHAR2":
            case "VARCHAR":
                return "string";
            case "INTEGER":
            case "INT":
                return "number";
            case "DOUBLE":
            case "DATETIME":
            case "DATE":
                return "date";
            case "FLOAT":
                return "number";
            default:
                return "string";
        }
    }


}
