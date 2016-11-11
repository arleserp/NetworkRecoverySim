/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unalcol.agents.NetworkSim.util;

import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author Arles Rodriguez
 */
public class StatisticsNormalDist {
    private ArrayList<Double> data;
    private int size;
    
    public StatisticsNormalDist(ArrayList<Double> arr, int s){
        data = arr;
        size = s;
    }
    
    public double getMin(){
        return Collections.min(data);
    }
    
    public double getMax(){
        return Collections.max(data);
    }
    
    public double getMean()
    {
        double sum = 0.0;
        for(double a : getData())
            sum += a;
        return sum/getSize();
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :getData())
            temp += (mean-a)*(mean-a);
        return temp/getSize();
    }

    public double getStdDev()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :getData())
            temp += (mean-a)*(mean-a);
        
        return Math.sqrt(temp/(getSize()-1));
    }
    
    public double getStdDevMedian()
    {
        double median = getMedian();
        double temp = 0;
        for(double a :getData())
            temp += (median-a)*(median-a);
        
        return Math.sqrt(temp/(getSize()-1));
    }

    public double getMedian() 
    {
       Collections.sort(data);
       if (getData().size() % 2 == 0) 
       {
          Double a = data.get((data.size() / 2) - 1);
          Double b = data.get(data.size() / 2); 
          return  (a+b)/2 ;
       } 
       else 
       {
          return data.get(size/2);
       }
    }

    /**
     * @return the data
     */
    public ArrayList<Double> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList data) {
        this.data = data;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
    
    public Double getSkewness(){
        return 3.0*(getMean()- getMedian())/getStdDev();
    }
    
    
    public Double getKurtosis(final int begin, final int length)
    {
        // Initialize the kurtosis
        double kurt = Double.NaN;
        //if (test(values, begin, length) && length > 3) {
        if (length > 3) {
            // Compute the mean and standard deviation
            double mean = getMean();
            double stdDev = getStdDev();
            // Sum the ^4 of the distance from the mean divided by the
            // standard deviation
            double accum3 = 0.0;

            for (int i = begin; i < begin + length; i++) {
                accum3 += Math.pow(data.get(i) - mean, 4.0);
            }
            accum3 /= Math.pow(stdDev, 4.0d);

            // Get N
            double n0 = length;
            double coefficientOne =
                (n0 * (n0 + 1)) / ((n0 - 1) * (n0 - 2) * (n0 - 3));
            double termTwo =
                (3 * Math.pow(n0 - 1, 2.0)) / ((n0 - 2) * (n0 - 3));

            // Calculate kurtosis
            kurt = (coefficientOne * accum3) - termTwo;
        }
        return kurt;
    }
    
}
