/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arles
 */
public class Renamer {

    public static String sDirectorio = ".";

    public static void rename(String simMode) {
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }
            String tmp = file.getName().replace("." + extension, "");
            tmp += simMode + "." + extension;
            File file2 = new File(tmp);

            if (file2.exists()) {
                try {
                    throw new java.io.IOException("file exists");
                } catch (IOException ex) {
                    Logger.getLogger(Renamer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            boolean success = file.renameTo(file2);
            System.out.println("success" + success);
        }
    }
    
    
    public static void main(String[] args) {
        String mode = "";
        if(args.length > 0){
            mode=args[0];
        }
        rename(mode);
    }
    
}

