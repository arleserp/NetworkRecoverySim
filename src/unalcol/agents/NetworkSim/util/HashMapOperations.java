/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class HashMapOperations {

    public static HashMap JoinSets(HashMap<String, ArrayList> A, HashMap<String, ArrayList> B) {
        HashMap C;

        if (A == null) {
            A = new HashMap<>();
        }
        if (B == null) {
            B = new HashMap<>();
        }

        if (A.isEmpty()) {
            return new HashMap<>(B);
        } else {
            C = new HashMap<>(A);
        }

        if (B.isEmpty()) {
            return new HashMap<>(A);
        }
        for (Iterator<String> iterator = B.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!C.containsKey(key)) {
                C.put(key, B.get(key));
            } else {
                ArrayList cInfo = new ArrayList((ArrayList) C.get(key));
                ArrayList bInfo = new ArrayList((ArrayList) B.get(key));
                cInfo.removeAll(bInfo);
                cInfo.addAll(bInfo);
                C.put(key, cInfo);
            }
        }
        return C;
    }

    public static HashMap<String, ArrayList> calculateDifference(HashMap A, HashMap B) {
        HashMap C = new HashMap();
        //System.out.println("A:" + A + " B:" + B);
        if (A.isEmpty()) {
            return C;
        }
        if (B == null || B.isEmpty()) {
            return new HashMap(A);
        }
        for (Iterator<String> iterator = A.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!B.containsKey(key) || !(A.get(key).equals(B.get(key)))) {
                C.put(key, A.get(key));
            }
        }
        return C;
    }

    public static boolean isContained(HashMap A, HashMap<String, ArrayList> B) {
        if (A.isEmpty()) {
            return true;
        }
        if (B.isEmpty()) {
            return false;
        }
        for (Iterator<String> iterator = A.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!B.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public static HashMap DifferenceSets(HashMap<String, ArrayList> A, HashMap B) {
        HashMap C = new HashMap();
        //System.out.println("A:" + A + " B:" + B);
        if (A.isEmpty()) {
            return C;
        }
        if (B == null || B.isEmpty()) {
            return new HashMap(A);
        }
        for (Iterator<String> iterator = A.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!B.containsKey(key) || !(A.get(key).equals(B.get(key)))) {
                C.put(key, A.get(key));
            }
        }
        return C;
    }
}
