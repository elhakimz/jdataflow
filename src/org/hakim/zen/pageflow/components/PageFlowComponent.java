package org.hakim.zen.pageflow.components;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/8/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PageFlowComponent {
    /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy,
     * distribute, or make derivative works under the terms of the Clarified Artistic License,
     * based on the Everything Development Company's Artistic License.  A document describing
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm.
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
    String value() default "";
}
