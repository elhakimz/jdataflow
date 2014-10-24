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
public @interface InPortWidgets {
  InPortWidget[] value();
}
