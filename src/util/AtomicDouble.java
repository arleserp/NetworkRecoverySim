/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author arlese.rodriguezp
 */
public class AtomicDouble {    
    private AtomicReference<Double> value = new AtomicReference(0.0);
    
    public double getAndAdd(double delta) {
        while (true) {
            Double currentValue = value.get();
            Double newValue = currentValue + delta;
            if (value.compareAndSet(currentValue, newValue))
                return currentValue;
        }
    }
}