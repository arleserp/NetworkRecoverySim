/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import java.util.Hashtable;
import unalcol.agents.Action;

/**
 *
 * @author Arles Rodriguez
 */
public class ActionParameters extends Action {
    protected Hashtable<String, Object> properties = new Hashtable<String, Object>();

    public ActionParameters(String _code) {
        super(_code);
    }

    public void setAttribute(String key, Object value) {
        properties.put(key, value);
    }

    public Object getAttribute(String key) {
        return properties.get(key);
    }

    public boolean removeAttribute(String key) {
        if (properties.remove(key) != null) {
            return true;
        } else {
            return (false);
        }
    }
}
