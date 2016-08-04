package unalcol.agents.NetworkSim.environment;

import unalcol.agents.NetworkSim.environment.NetworkEnvironmentCollection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

/**
 * -
 *
 * @author Arles
 */
public class controlBoard implements Observer {

    private Hashtable out;
    private Hashtable expectedResMap;
    private String filename;

    
    private static class Holder {

        static final controlBoard INSTANCE = new controlBoard();
        //System.out.println("newwwwwwwwwwwwwwwwwwwwww cb");
    }

    public static controlBoard getInstance() {
        return Holder.INSTANCE;
    }

    private controlBoard() {
        out = new Hashtable();
    }

    /**
     * @return the out
     */
    public Hashtable getOut() {
        return out;
    }

    /**
     * @param out the out to set
     */
    public void setOut(Hashtable out) {
        this.out = out;
    }

    /**
     * @param out to set
     */
    public void resetOutput() {
        this.out = new Hashtable();
    }

    /**
     * @return the expectedResMap
     */
    public Hashtable getExpectedResMap() {
        return expectedResMap;
    }

    /**
     * @param expectedResMap the expectedResMap to set
     */
    public void setExpectedResMap(Hashtable expectedResMap) {
        this.expectedResMap = expectedResMap;
    }


    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof NetworkEnvironmentCollection) {
            NetworkEnvironmentCollection t = (NetworkEnvironmentCollection) obs;
            try (PrintWriter escribir = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
                escribir.println(t.getLog());
            } catch (IOException ex) {
                Logger.getLogger(controlBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addObserver(NetworkEnvironmentCollection network) {
        network.addObserver(this);
    }

    void addOutput(String pid, Hashtable outp) {
        getOut().put(pid, outp);
        // System.out.println("Ag: " + pid + ", Control Board Output: " + out);
    }

    public String toString() {
        return getOut().toString();
    }

    public int getSize() {
        return getOut().size();
    }


    Hashtable getStatisticsHashtable() {
        Hashtable Statistics = new Hashtable();
        Hashtable outtmp = new Hashtable(expectedResMap);
        int right = 0;
        int wrong = 0;

        if (out.equals(expectedResMap)) {
            right++;
        } else {
            wrong++;
        }
        Statistics.put("right", right);
        Statistics.put("wrong", wrong);
        System.out.println("stats: " + Statistics);
        return Statistics;
    }
}


