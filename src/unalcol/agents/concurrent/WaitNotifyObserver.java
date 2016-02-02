package unalcol.agents.concurrent;
import java.util.Vector;

/**
 * <p>Title: WaitNotifyObserver </p>
 * <p>Description: Esta clase permite sincronizar la operacion de los jugadores y el observador, 
 *    especificamente, los jugadores ponen las decisiones tomadas en cada iteracion 
 *    para que sean consultadas por el observador (Ambiente). 
 *    Dichas decisiones son utilizadas en cada paso de simulacion para realizar el pago 
 *    de recompensa determinado. 
 * </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 * @author Fabian Giraldo
 * @version 1.0
 */
public class WaitNotifyObserver<T> implements Broker<T> {
	
	/**
    * Message
    */
    private Vector<T> message;
       
    /**
     * number of Agents in the simulation 
    */
    private int agents;

    /**
     * WaitNotifyBroker
     * @param agents number of agent
     */
    public WaitNotifyObserver(int agents){
    	this.message=new Vector<T>();
    	this.agents=agents;
    }
    
    /**
     * Permite al agente Observador recuperar las decisiones 
     * de los agentes jugadores.
    */
    public synchronized Vector<T> take() throws InterruptedException {
        while (message.size() < this.agents) {
           	wait();
        }
    	Vector<T> _message=(Vector<T>) this.message.clone();
    	this.message.clear();
        notifyAll();
        return _message;
    }
    
    /**
     * Permite a los agentes jugadores poner la decisión para que posteriormente 
     * sea consultda por el agente observador.
    */
    public synchronized void put(T message) throws InterruptedException {
        while (this.message.size()==this.agents) {
           	wait();
        }
               
        this.message.add(message);
        notifyAll();
     
        
    }

	@Override
	public void clean() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
   
    
}