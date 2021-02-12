/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmetrics;

import java.util.HashMap;

/**
 *
 * @author Arles
 */
public class ByteScales {

    public static HashMap<Integer, String> prefixMB = new HashMap<Integer, String>() {
        {
            put(1000, "KB");
            put(1000000, "MB");
            put(1000000000, "MB");
        }
    };

}
