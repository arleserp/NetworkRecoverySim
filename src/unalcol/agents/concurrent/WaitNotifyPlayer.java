package unalcol.agents.concurrent;
import java.util.Vector;

/**
 * <p>Title: WaitNotifyBroker </p>
 * <p>Description: Esta clase es usada para comunicar los resultados obtenidos en cada paso 
 *     de simulacion. 
 *     Los agentes jugadores (Player) pueden consultar los resultados entregados por el Agente 
 *      Observador.   
 *  </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 * @author Fabian Giraldo
 * @version 1.0
 */
public class WaitNotifyPlayer<T> implements Broker<T> {
	
	/**
    * Message
    */
    private Vector<T> message;
    
    /**
     * number of agent in the simulation
    */
    private int agents;
    
    /**
     * Variable utilizada para controlar el numero de agentes que han consultado 
     * los resultados de la simulacion en el periodo anterior (Percepcion), esto 
     * con el fin de limpiar el buffer de memoria y permitir que en en la iteracion
     * actual de simulacion, el agente Observador pueda especificar los resultados.
     */
    private int count;

    /**
     * WaitNotifyBroker
     * @param agents number of agent
     */
    public WaitNotifyPlayer(int agents){
    	this.message=new Vector<T>();
    	this.message.add(null);
    	this.count=0;
       	this.agents=agents;
    }
    
    /**
     * Permite a los agentes jugadores (Player) consultar los resultados de la simulacion
     * en el periodo anterior (Percepcion)
     */
    public synchronized Vector<T> take() throws InterruptedException {
       	while (message.size() ==0) {
           	wait();
        }
    	
       	count++;
       	Vector<T> _message=(Vector<T>) this.message.clone();
       	if(this.count==this.agents){
       		this.message.clear();
       		this.count=0;
       	}
       	
        notifyAll();
        return _message;
    }

    /**
     * Permite al agente observador comunicar los resultados obtenidos en la simulacion
     * en el periodo de simulacion actual.
     *
     */
    public synchronized void put(T message) throws InterruptedException {
    	while (this.message.size()==1) {
           	wait();
        }
        
        this.message=(Vector<T>) message;
        notifyAll();
    }

	@Override
	public void clean() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
   
	
}