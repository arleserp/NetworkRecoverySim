/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.environment;

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
public class ObjectSerializer {

    public static String serialize(Object h) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        String s = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(h);
            s = baos.toString("ISO-8859-1");
            oos.reset();
            oos.close();
            oos=null;
            baos=null;            
        } catch (IOException ex) {
            Logger.getLogger(ObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }

    public static Object deserialize(String s) {
        Object h = null;
        try {
            ObjectInputStream ois;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes("ISO-8859-1"))) {
                ois = new ObjectInputStream(bais);
                h = (Object) ois.readObject();
            }
            ois.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return h;
    }

}
