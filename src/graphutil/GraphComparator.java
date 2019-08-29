package graphutil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import environment.NetworkEnvironment;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GraphComparator {

    public double calculateSimilarity(NetworkEnvironment world) {
        int[][] adyacA = world.getInitialAdyacenceMatrix().clone();
        int[][] adyacB = world.getAdyacenceMatrix().clone();

        if (world.getAge() > 4500) {
            for (int i = 0; i < adyacA.length; i++) {
                for (int j = 0; j < adyacA.length; j++) {
                    if (adyacA[i][j] != adyacB[i][j]) {
                        System.out.println("env age:" + world.getAge() + " difference: " + world.getLocationtoVertexName().get(i) + "-" + world.getLocationtoVertexName().get(j));
                    }
                }
            }
        }

        double similarity = 0.0;
        double sumA = 0.0;
        double sumB = 0.0;
        for (int j = 0; j < adyacA.length; j++) {
            for (int k = 0; k < adyacA.length; k++) {
                similarity += adyacA[j][k] * adyacB[j][k];
                sumA += (adyacA[j][k]) * adyacA[j][k];
                sumB += (adyacB[j][k]) * adyacB[j][k];
            }
        }

        similarity /= (Math.sqrt(sumA) * Math.sqrt(sumB));
        return similarity * 100.0;
    }
}
