/*
 *  (c) 2013 Michael Beck disco | Distributed Computer Systems Lab
 *                                  University of Kaiserslautern, Germany
 *         All Rights Reserved.
 *
 *  This software is work in progress and is released in the hope that it will
 *  be useful to the scientific community. It is provided "as is" without
 *  express or implied warranty, including but not limited to the correctness
 *  of the code or its suitability for any particular purpose.
 *
 *  You are free to use this software for any non-commercial educational or
 *  research purpose, provided that this copyright notice is not removed or
 *  modified. For commercial uses please contact the respective author(s).
 *
 *  If you find our software useful, we would appreciate if you mentioned it
 *  in any publication arising from the use of this software or acknowledge
 *  our work otherwise. We would also like to hear of any fixes or useful
 *  extensions to this software.
 *
 */

package unikl.disco.mgf.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.gui.Displayable;
import unikl.disco.mgf.Service;

/**
 * This class represents a node in the network. It knows about its
 * incoming arrivals, their priorities at this node and their 
 * flow_ids. This is done by the two HashSets <code>priorities
 * </code> and <code>incoming</code>. The first contains all flows
 * arriving at the node and their priorities. The latter contains
 * all flows, for which an arrival description is given. Further 
 * the vertex can determine, which flow is the next to serve.
 * The most important method in this class is <code>serve()</code>.
 * It is called to calculate the leftover service of this node, 
 * after serving the current flow (in expression the flow, which
 * has the highest priority and was not served yet). Further it 
 * returns the output bound of the served flow. 
 * @author Michael Beck
 * @see Flow
 * @see Arrival
 * @see Service
 *
 */
public class Vertex implements Serializable, Displayable {
	
	private static final long serialVersionUID = -8696545130808777213L;
	private String alias;
	private int vertexID;
	
	private Service service;
	private int prioritizedFlowID;
	private Map<Integer, Integer> priorities;
	private int highest_priority;
	private Map<Integer, Arrival> incoming;
        private Network nw;
	
	//Constructor
	
	/**
	 * Creates an empty node.
     * @param vertex_ID The ID the vertex will have in the network
     * @param alias An optional alias of the node, empty string if alias is null
     * @param nw The corresponding network in which this node exists
	 */
	
	protected Vertex(int vertex_ID, String alias, Network nw){
		service = new Service(nw);
		prioritizedFlowID = 0;
		highest_priority = 0;
		priorities = new HashMap<>();
		incoming = new HashMap<>();
		this.vertexID = vertex_ID;
		service.addServiceDependency(vertex_ID);
		this.alias = alias == null ? "" : alias;
                this.nw = nw;
	}

	/**
	 * Creates a node with a given service
     * @param vertex_ID
	 * @param service the service, which is associated with this
	 * node
     * @param alias An optional alias of the node, empty string if alias is null
     * @param nw The corresponding network in which this node exists
	 */
	
	public Vertex(int vertex_ID, Service service, String alias, Network nw){
		this.service = service;
		prioritizedFlowID = 0;
		highest_priority = 0;
		priorities = new HashMap<>();
		incoming = new HashMap<>();
		this.vertexID = vertex_ID;
		service.addServiceDependency(vertex_ID);
		this.alias = alias == null ? "" : alias;
                this.nw = nw;
	}
	
	//Methods
	
	/**
	 * Adds an arrival to this node.
	 * @param priority the priority this arrival has at this node
	 * @param flow_id the flow-id to which this arrival belongs
	 * @param arrival the arrival bound
	 */
	public void addArrival(int priority, int flow_id, Arrival arrival){
		
		priorities.put(flow_id, priority);
		incoming.put(flow_id, arrival);
		
		//Checks for a change in the highest priority
		if(priority > highest_priority){
			prioritizedFlowID = flow_id;
			highest_priority = priority;
		}
	}
	
	/**
	 * Adds a non-established arrival to this node. 
	 * @param priority the priority of the arrival
	 * @param flow_id the flow-id to which the arrival belongs
	 */
	public void addUnknownArrival(int priority, int flow_id){
		priorities.put(flow_id, priority);
		if(priority > highest_priority){
			prioritizedFlowID = flow_id;
			highest_priority = priority;
		}
	}
	
	/**
	 * Changes a (non-established) arrival to an established 
	 * arrival. If the arrival was already established before, it is
	 * overwritten by this method.
	 * @param flow_id the flow-id to which the arrival belongs
	 * @param arrival the arrival bound
	 * @throws ArrivalNotAvailableException
	 */
	public void learnArrival(int flow_id, Arrival arrival) throws ArrivalNotAvailableException{

		if(priorities.containsKey(flow_id)){
			incoming.put(flow_id, arrival);
		}
		else throw new ArrivalNotAvailableException("The given arrival-flow-id does not appear in the priority list.",this);
	}
	
	//Serves the prioritized flow, returns the output to the calling procedure. After this the 
	//leftover service is calculated and written into service. The served flow is removed from
	//the priority and established_flows HashMaps. Further the next flow to be served is determined.
	//Invoking serve() without any flows in established_incoming will result in an error.
	//Further the invoking analysis is informed via newHoelderCoefficient about new Hölder coefficients
	// rising. Also the dependency sets of service and the involved arrivals are updated.
	
	/**
	 * Serves the prioritized flow and returns the output to the 
	 * calling procedure. After this the leftover service is 
	 * calculated and written into service. The served flow is 
	 * removed from the priority and established_flows HashMaps. 
	 * Further the next flow to be served is determined. Invoking 
	 * serve() without any flows in established_incoming will 
	 * result in an error.
	 * After serving the current flow this also determines, which
	 * flow is the next one to serve.
	 * @return the output bound
	 * @throws ArrivalNotAvailableException
	 */
	public Arrival serve() throws ArrivalNotAvailableException{
		
		//Checks if non-established arrivals exist (number of priority entries larger > arrival entries)
		if(incoming.size() != priorities.size()){
			throw new ArrivalNotAvailableException("Not all arrivals had been established",this);
		}

		else{	
			//Calculates the output-bound
			Arrival arrival = incoming.get(prioritizedFlowID);
			Arrival output = arrival.output(arrival, service);
			
			//Calculates the leftover service		
			service = service.leftover(arrival, service);
			
			//Removes the served flow from the arrival-list
			priorities.remove(prioritizedFlowID);
			incoming.remove(prioritizedFlowID);
			
			//System.out.println("Flow with flow_id "+prioritizedFlowID+" and priority "+highest_priority+" served at node "+vertex_ID);
			
			//Determines the next flow to serve
			prioritizedFlowID = calculatePriority();
			if(prioritizedFlowID > 0)	highest_priority = priorities.get(prioritizedFlowID);
			
			//Returns the output-bound
			return output;
		}
	}

	/**
	 * Determines which flow has the highest priority.
	 * @return the flow-id of the flow with the highest priority
	 */
	public int calculatePriority(){
		int high = 0;
		int higher_priority_id =0;
		for(Map.Entry<Integer,Integer> entry : priorities.entrySet()){
			if(high < entry.getValue()){
				higher_priority_id = entry.getKey();
				high = entry.getValue();
			}
		}
		return higher_priority_id;
	}
        
        public int getPrioritizedFlow() {
            return prioritizedFlowID;
        }
	
	/**
	 * Determines if there is at least one non-established flow
     * @return true, if there are only established flows and at least one non-established flow, false otherwise
	 */
	public boolean canServe(){
            return !priorities.isEmpty() && priorities.size()==incoming.size();
	}
	
	/**
	 * Removes a flow from the vertex. This means, its id is
	 * deleted from <code>priorities</code> and <code>incoming</code>.
	 * After this the highest priority is calculated, since the 
	 * removed flow, might have been the one with the highest
	 * priority.
	 * @param id the id of the flow to be removed.
	 */
	public void removeFlow(int id){
		priorities.remove(id);
		incoming.remove(id);
		prioritizedFlowID = calculatePriority();
		if(prioritizedFlowID > 0)	highest_priority = priorities.get(prioritizedFlowID);
		
	}
     	
	//Copy-Operator
	
	public Vertex copy(){
		Vertex copy = new Vertex(vertexID, service, alias, nw);
		for(Entry<Integer, Integer> entry : priorities.entrySet()){
			if(incoming.containsKey(entry.getKey())) copy.addArrival(entry.getValue(), entry.getKey(), incoming.get(entry.getKey()));
			else copy.addUnknownArrival(entry.getValue(), entry.getKey());
		}
		return copy;
	}
	//Getter and Setter
	
	public void setMGFService(Service service) {
		this.service = service;
	}
	
	public Service getService(){
		return service;
	}
	
        @Override
	public String getAlias() {
		return alias;
	}
	
	public int getVertexID(){
		return vertexID;
	}

	public Map<Integer, Integer> getAllFlowPriorities() {
		return priorities;
	}
	
        public Set<Integer> getAllFlowIDs() {
            return incoming.keySet();
        }
        
        public Arrival getArrivalOfFlow(int flowID) {
            return incoming.get(flowID);
        }
        
        public int getPriorityOfFlow(int flowID) {
            return priorities.get(flowID);
        }
}
