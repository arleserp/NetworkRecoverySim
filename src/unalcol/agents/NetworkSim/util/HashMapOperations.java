/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class HashMapOperations {
        public static HashMap JoinSets(HashMap A, HashMap B) {
        HashMap C;

        if (A == null) {
            A = new HashMap();
        }
        if (B == null) {
            B = new HashMap();
        }

        if (A.isEmpty()) {
            return new HashMap(B);
        } else {
            C = new HashMap(A);
        }

        if (B.isEmpty()) {
            return new HashMap(A);
        }
        for (Iterator<String> iterator = B.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!C.containsKey(key)) {
                C.put(key, B.get(key));
            }
        }
        return C;
    }
}
