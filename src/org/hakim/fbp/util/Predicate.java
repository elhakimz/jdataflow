package org.hakim.fbp.util;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/19/14.
 */
public  abstract class Predicate<Item> {
    protected abstract boolean apply(Item i);
}
