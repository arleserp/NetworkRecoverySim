/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.util.ArrayList;

/**
 *
 * @author Arles
 */
public class StringNodeChainHelper {

    public static String trimNodeNeighbor(String nodeName) {
        ArrayList<Integer> loc = new ArrayList<>();
        for (int i = 0; i < nodeName.length(); i++) {
            if (nodeName.charAt(i) == 'p') {
                loc.add(i);
            }
        }
        if (loc.size() == 1) {
            return nodeName;
        }
        return nodeName.substring(loc.get(loc.size() - 2), nodeName.length());
    }

    public static void main(String[] args) {
        System.out.println("trim" + trimNodeNeighbor("p1"));
        System.out.println("trim" + trimNodeNeighbor("p1222p2222"));
        System.out.println("trim" + trimNodeNeighbor("p1p778p2838738p3"));
    }

}
