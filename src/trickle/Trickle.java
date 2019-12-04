/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trickle;

/**
 *
 * @author Arles
 */
public class Trickle {

    private long intervalMin;
    private long intervalMax;
    private int redundancyFactorK;
    private long currentInterval = 100; //milliseconds
    private int counter = 0;

    public Trickle() {
        intervalMin = 100;
        intervalMax = (long) (2 >> 16);
        redundancyFactorK = 5; //3
     }

    public Trickle(long iMin, int iFactor, int redunFactor) {
        intervalMin = iMin;
        intervalMax = (long) (2 >> iFactor) * iMin;
        redundancyFactorK = redunFactor;
    }

    public long[] next() {
        currentInterval = Math.min(intervalMin, currentInterval * 2);
        counter = 0;
        return new long[]{currentInterval - Math.round(Math.random() * (currentInterval / 2)), currentInterval};
    }

    public void incr() {
        counter++;
    }

    public boolean check() {
        return counter < redundancyFactorK;
    }

    public long[] reset() {
        if (currentInterval <= intervalMin) {
            currentInterval = intervalMin;
            return new long[0];
        } else {
            currentInterval = intervalMin / 2;
            return next();
        }
    }
}
