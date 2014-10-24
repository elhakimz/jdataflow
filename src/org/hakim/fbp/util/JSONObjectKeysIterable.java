package org.hakim.fbp.util;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 9/19/14.
 */
public class JSONObjectKeysIterable implements Iterable {

    JSONObject mObject;
    JSONObjectKeysIterable(JSONObject o) {
        mObject = o;
    }

    public Iterator iterator() {
        return mObject.keys();
    }
}