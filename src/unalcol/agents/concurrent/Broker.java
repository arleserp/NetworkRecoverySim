package unalcol.agents.concurrent;
import java.util.Vector;

/**
 * <p>Title: Broker </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 * @author Fabian Giraldo
 * @version 1.0
 */
public interface Broker<T> {
    Vector<T> take() throws InterruptedException;
    void put(T message) throws InterruptedException;
    void clean() throws InterruptedException;
   
}