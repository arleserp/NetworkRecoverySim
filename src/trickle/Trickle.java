/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trickle;

import java.util.concurrent.ThreadLocalRandom;
import networkrecoverysim.SimulationParameters;

/**
 *
 * @author Arles
 */
public class Trickle {

    private int intervalMin;
    private int intervalMax;
    private int redundancyFactorK;
    private int currentInterval = 1; //rounds
    private int counter = 0;

    public int getCounter() {
        return counter;
    }
        
    public Trickle() {
        intervalMin = 1;
        intervalMax = 2 << 16;
        redundancyFactorK = SimulationParameters.redundancyFactor;
    }

    public Trickle(int iMin, int iFactor, int redunFactor) {
        intervalMin = iMin;
        intervalMax = 2 << 16;
        redundancyFactorK = redunFactor;
    }

    public int[] next() {
        //System.out.println("inconsistency!");
        currentInterval = Math.min(intervalMin, currentInterval * 2);
        //System.out.println("current" + currentInterval);
        counter = 0;
        return new int[]{currentInterval, currentInterval};
    }

    //Possible step 5
    public int[] iExpired() {    
        //System.out.println("entraaaaaa");
        counter = 0;
        if(currentInterval*2 > intervalMax){
            currentInterval = intervalMax;
        }
        currentInterval *=2;
       // System.out.println("i expired: " + currentInterval);
        return new int[]{currentInterval/2, currentInterval};
    }

    public void incr() {
        counter++;
    }

    public boolean check() {
        return counter < redundancyFactorK;
    }

    public int[] reset() {
        if (currentInterval <= intervalMin) {
            currentInterval = intervalMin;
        }
        return next();
    }
    
    public int getT(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max+1);    
    }
        
}
