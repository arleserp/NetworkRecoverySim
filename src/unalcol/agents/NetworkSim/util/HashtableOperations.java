/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arles Rodriguez
 */
public class HashtableOperations {

    public static int Maximum(Hashtable A) {
        if (!A.isEmpty()) {
            //System.out.println("A" + A);
            return (int) Collections.max(A.values());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public static int Minimum(Hashtable A) {
        if (!A.isEmpty()) {
            //System.out.println("A" + A);
            return (int) Collections.min(A.values());
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static ArrayList Sort(Hashtable A, Hashtable B) {
        Hashtable C = JoinSets(A, B);
        //System.out.println("vals ssssssssssssssssssss" + C.values());
        Object[] D = (C.values()).toArray();
        Arrays.sort(D);
        //System.out.println("arr" + Arrays.toString(D));
        ArrayList<Integer> E = new ArrayList(Arrays.asList(D));
        return E;
    }

    public static Hashtable JoinSets(Hashtable A, Hashtable B) {
        Hashtable C;

        if (A == null) {
            A = new Hashtable();
        }
        if (B == null) {
            B = new Hashtable();
        }

        if (A.isEmpty()) {
            return new Hashtable(B);
        } else {
            C = new Hashtable(A);
        }

        if (B.isEmpty()) {
            return new Hashtable(A);
        }
        for (Iterator<String> iterator = B.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!C.containsKey(key)) {
                C.put(key, B.get(key));
            }
        }
        return C;
    }

    public static Hashtable DifferenceSets(Hashtable A, Hashtable B) {
        Hashtable C = new Hashtable();
        //System.out.println("A:" + A + " B:" + B);
        if (A.isEmpty()) {
            return C;
        }
        if (B == null || B.isEmpty()) {
            return new Hashtable(A);
        }
        for (Iterator<String> iterator = A.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (!B.containsKey(key) || !(A.get(key).equals(B.get(key)))) {
                C.put(key, A.get(key));
            }
        }
        return C;
    }

    public static boolean BelongsTo(String key, Hashtable B) {
        if (B.isEmpty()) {
            return false;
        }
        return B.containsKey(key);
    }

    public static boolean isContained(Hashtable A, Hashtable B) {
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

    public static String serializeHashtable(Hashtable h) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        String s = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(h);
            oos.close();
            //baos.close();
            s = baos.toString("ISO-8859-1");
            //System.out.println("sb:" + s.getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(HashtableOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }

    public static Hashtable deserializeHashtable(String s) {
        Hashtable h = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes("ISO-8859-1"));
            ObjectInputStream ois = new ObjectInputStream(bais);
            h = (Hashtable) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(HashtableOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HashtableOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return h;
    }

    public static boolean isCutPoint(Hashtable h) {
        int count = 0;
        int i = 0;
        ArrayList<String> bicon = new ArrayList<>();
        ArrayList<String> ones = new ArrayList<>();

        for (Iterator<Hashtable> iterator = h.keySet().iterator(); iterator.hasNext();) {
            i++;
            Hashtable n = (Hashtable) h.get(iterator.next());
            System.out.println("n" + n);
            Object[] D = (n.values()).toArray();
            Integer[] E = Arrays.copyOf(D, D.length, Integer[].class);
            int min = (int) Collections.min(Arrays.asList(E));
            int max = (int) Collections.max(Arrays.asList(E));
            System.out.println("min" + min + ", max" + max);

            if (n.size() == 1) {
                for (Iterator<String> it2 = n.keySet().iterator(); it2.hasNext();) {
                    String key = it2.next();
                    if (!ones.contains(key)) {
                        ones.add(key);
                    }
                }
            }
            if (n.size() > 1 && (min == max || Math.abs(max - min) == 1)) {
                count++;
                for (Iterator<String> it3 = n.keySet().iterator(); it3.hasNext();) {
                    String key = it3.next();
                    if (!bicon.contains(key)) {
                        bicon.add(key);
                    }
                }
            }
        }

        if (bicon.isEmpty() && ones.size() > 1) {
            return true;
        }

        for (String item : ones) {
            if (!bicon.isEmpty() && !bicon.contains(item)) {
                return true;
            }
        }
        return false;
    }

    static boolean isCicle(Hashtable tmpout) {
        if (tmpout.size() == 1) {
            return false;
        }
        Object[] tmp = (tmpout.values()).toArray();
        Integer[] tmpvalues = Arrays.copyOf(tmp, tmp.length, Integer[].class);
        int min = (int) Collections.min(Arrays.asList(tmpvalues));
        int max = (int) Collections.max(Arrays.asList(tmpvalues));
        System.out.println("tmin" + min + ", tmax" + max);
        return (min == max || Math.abs(max - min) == 1);
    }

    static boolean isCutPoint(Hashtable h, Hashtable tmpout) {
        int count = 0;

        for (Iterator<Hashtable> iterator = h.keySet().iterator(); iterator.hasNext();) {
            Hashtable n = (Hashtable) h.get(iterator.next());
            if (tmpout.size() <= n.size()) {
                if (isCicle(n) && !isContained(tmpout, n)) {
                    return true;
                } else if (tmpout.size() == 1) {
                    if (!isContained(tmpout, n)) {
                        count++;
                    }
                }
            } else {
                if (isCicle(tmpout) && !isContained(n, tmpout)) {
                    return true;
                } else if (n.size() == 1) {
                    if (!isContained(n, tmpout)) {
                        count++;
                    }
                }
            }
        }
        return count == h.size();
    }

    static boolean isCutP(Hashtable r) {
        System.out.println("entra.");
        int count = 0;
        if (r.isEmpty()) {
            return false;
        }

        if (r.size() > 1) {
            System.out.println("mas de uno");
            return true;
        }

        //There is only one keyset 
        for (Iterator<String> iterator = r.keySet().iterator(); iterator.hasNext();) {
            //System.out.println("is not cicle"+ (Hashtable)r.get(iterator.next()));
            String key = iterator.next();
            boolean res;
            if (res = isCicle((Hashtable) r.get(key))) {
                System.out.println("Is cicle:" + r + ", key:" + key + ", res:" + res);
                count++;
            }
        }
        //System.out.println("raro");
        return count == 0 || count != 1;
    }

    static Hashtable calculateR(String pid, Hashtable r, Hashtable t) {
        System.out.println("r" + r);
        System.out.println("t" + t);

        //if relation is empty add this to the relation
        if (r.isEmpty()) {
            r.put(pid, t);
        }
        /**
         * if t size is equal to 1 is added iff t is not contained in r.
         */
        if (t.size() == 1) {
            for (Iterator<String> iterator = r.keySet().iterator(); iterator.hasNext();) {
                Hashtable n = (Hashtable) r.get(iterator.next());
                // if t is in r do nothing return relation
                if (isContained(t, n)) {
                    return r;
                }
            }
            r.put(pid, t);
            return r;
        }

        if (t.size() > 1) {
            //1. remove r_i contained in t
            Hashtable rcopy = new Hashtable(r);
            for (Iterator<String> iterator = rcopy.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                Hashtable n = (Hashtable) r.get(key);
                if (isContained(n, t)) { //study here that also n can be a cycle and t not so it is not necesary to remove them.
                    r.remove(key);
                }
            }
            //2. if t is in r do nothing return relation
            for (Iterator<String> iterator = r.keySet().iterator(); iterator.hasNext();) {
                Hashtable n = (Hashtable) r.get(iterator.next());
                // if t is in r do nothing return relation
                if (isContained(t, n)) {
                    return r;
                }
            }
            //if(isCicle(t)){
            //falta evaluar bien la parte de los ciclos!
            r.put(pid, t);
            //}
            return r;
        }
        return r;
    }

    static Hashtable calculateR2(String pid, Hashtable r, Hashtable t) {
        System.out.println("r" + r);
        System.out.println("t" + t);

        if (!isCicle(t)) {
            return r;
        }

        //if relation is empty add this to the relation
        if (r.isEmpty()) {
            r.put(pid, t);
        }

        if (t.size() > 1) {
            //1. remove r_i contained in t
            Hashtable rcopy = new Hashtable(r);
            for (Iterator<String> iterator = rcopy.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                Hashtable n = (Hashtable) r.get(key);
                if (isContained(n, t)) { //study here that also n can be a cycle and t not so it is not necesary to remove them.
                    r.remove(key);
                }
            }
            //2. if t is in r do nothing return relation
            for (Iterator<String> iterator = r.keySet().iterator(); iterator.hasNext();) {
                Hashtable n = (Hashtable) r.get(iterator.next());
                // if t is in r do nothing return relation
                if (isContained(t, n)) {
                    return r;
                }
            }
            //if(isCicle(t)){
            //falta evaluar bien la parte de los ciclos!
            r.put(pid, t);
            //}
            return r;
        }
        return r;
    }

    public static double Average(Hashtable A) {
        //System.out.println("vals ssssssssssssssssssss" + C.values());
        Object[] D = (A.values()).toArray();
        Integer[] E = Arrays.copyOf(D, D.length, Integer[].class);
        Double sum = 0.0;
        for(Integer i: E){
            sum +=i;
        }
        return sum/E.length;
    }
}
