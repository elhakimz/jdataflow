package org.javafbp.runtime.pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/4/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InPortWidget {
    public static final String SELECT_DIRECTORY="select-dir";
    public static final String SELECT_REPOSITORY="select-repo";
    public static final String SELECT_SCRIPT="select-script";

    public static final String GROOVY_EDIT="groovy-edit";
    public static final String JS_EDIT="js-edit";
    public static final String SQL_EDIT="sql-edit";
    public static final String XML_EDIT="xml-edit";

    String value();
    String widget();
    String config() default "";
}
