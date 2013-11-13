/*
 *  (c) 2013 Michael A. Beck, disco | Distributed Computer Systems Lab
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.Hoelder;
import unikl.disco.mgf.Service;

/**
 * This class provides several methods to construct and change a
 * network consisting of {@link Flow}s and {@link vertex}-objects.
 * It has two static variables, which are used to automatically tag
 * new nodes and flows with distinct IDs.
 * @author Michael Beck
 * @see Flow
 * @see Vertex
 * @see Arrival
 * @see Service
 * @see Hoelder
 *
 */
public class Network {
	
	//Members
	
	private static int FLOW_ID = 1;
	private static int VERTEX_ID = 1;
	private static int HOELDER_ID = 1;
	private static HashMap<Integer,Flow> flows = new HashMap<Integer, Flow>(0);
	private static HashMap<Integer,Vertex> vertices = new HashMap<Integer, Vertex>(0);
	private static HashMap<Integer,Hoelder> hoelders = new HashMap<Integer, Hoelder>(0);
	
	// Constructor
	
	public Network(){}
	
	//Methods
	
	/**
	 * Creates a new Hoelder-Object and returns its id.
	 * @return the newly created Hoelder-Object.
	 */
	public static Hoelder createHoelder(){
		Hoelder hoelder = new Hoelder(HOELDER_ID);
		hoelders.put(hoelder.getHoelderID(), hoelder);
		incrementHOELDER_ID();
		return hoelder;
	}
	
	/**
	 * Adds a new dummy vertex to the network
	 */
	public static void addVertex(){
		Vertex vertex = new Vertex(VERTEX_ID);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
	}
	
	/**
	 * Adds a new dummy vertex with alias 
	 */
	public static void addVertex(String alias){
		Vertex vertex = new Vertex(VERTEX_ID, alias);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
	}
	
	/**
	 * Adds a new vertex with predefined service to the network
	 * @param service the service the new vertex possesses
	 */
	public static void addVertex(Service service){
		Vertex vertex = new Vertex(VERTEX_ID, service);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
	}
	
	/**
	 * Adds a new vertex with predefined service and alias
	 * @param service
	 */
	public static void addVertex(Service service, String alias){
		Vertex vertex = new Vertex(VERTEX_ID, service, alias);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
	}
	
	/**
	 * Sets the service at a specific node in the network
	 * @param vertex the vertex to be altered
	 * @param service the new service at the specific vertex
	 */
	public static void setServiceAt(Vertex vertex, Service service){
		vertex.setMGFService(service);
	}
	
	/**
	 * Sets the service at the node with alias <code>alias</code>
	 * @param alias the alias of the vertex to be altered
	 * @param service the new service at the specific vertex
	 */
	public static void setServiceAt(String alias, Service service){
		Vertex vertex = null;
		for(Map.Entry<Integer, Vertex> entry : vertices.entrySet()){
			if(entry.getValue().getAlias() == alias) vertex = entry.getValue();
		}
		if(vertex != null) setServiceAt(vertex, service);
		else throw new NullPointerException("There is no vertex with the given alias.");
	
	}
	
	/**
	 * Sets the service at the vertex with the given id
	 * @param vertex_id the id of the vertex to be altered
	 * @param service the new service at the specific vertex
	 */
	public static void setServiceAt(int vertex_id, Service service){
		vertices.get(vertex_id).setMGFService(service);
	}
	
	/**
	 * Removes a vertex from the network. This is done by 
	 * removing the vertex from all flows, which include 
	 * this vertex in its route. I.e. if a flow routes 
	 * through A-B-C and B is removed the new route will be
	 * A-C. After this the vertex is removed from the list 
	 * of vertices.
	 * @param vertex the <code>Vertex</code> to be removed
	 * @return if removing the vertex was successful
	 */
	public static boolean removeVertex(Vertex vertex) {
		boolean success = false;
		if(vertices.containsKey(vertex.getVertexID())){
			for(int i : vertex.getAllFlowIDs().keySet()){
				flows.get(i).removeVertex(vertex.getVertexID());
			}
			vertices.remove(vertex.getVertexID());
			success = true;
			
		}
		return success;
	}
	/**
	 * Adds a new dummy flow to the network
	 */
	public static void addFlow(){
		Flow flow = new Flow(FLOW_ID);
		flows.put(FLOW_ID, flow);
		incrementFLOW_ID();
	}

	/**
	 * Adds a new dummy flow with some alias to the network
	 */
	public static void addFlow(String alias){
		Flow flow = new Flow(FLOW_ID, alias);
		flows.put(FLOW_ID, flow);
		incrementFLOW_ID();
	}
	
	/**
	 * Creates a new flow with no initial arrival, but a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow). The priorities at all nodes is
	 * the same and given by <code>priority</code>.
	 * @param vertices the  vertices the flow traverses
	 * @param priority the priority of the flow at the 
	 * corresponding vertices.
	 */
	public static void addFlow(ArrayList<Integer> route, int priority){

		//Creates the dummy arrivals for all vertices
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
		for(int i=0; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Creates the array of priorities
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		for(int i=0; i < route.size(); i++){
			priorities.add(priority);
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities);
		flows.put(FLOW_ID, flow);

		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		//increments the flow count
		incrementFLOW_ID();
	}
	
	/**
	 * Creates a new flow with no initial arrival, but a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow). The priorities at all nodes is
	 * the same and given by <code>priority</code>.
	 * @param vertices the  vertices the flow traverses
	 * @param priority the priority of the flow at the 
	 * corresponding vertices.
	 * @parm alias the alias of the new flow
	 */
	public static void addFlow(ArrayList<Integer> route, int priority, String alias){

		//Creates the dummy arrivals for all vertices
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
		for(int i=0; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Creates the array of priorities
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		for(int i=0; i < route.size(); i++){
			priorities.add(priority);
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
		flows.put(FLOW_ID, flow);

		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		//increments the flow count
		incrementFLOW_ID();
	}
	
	/**
	 * Creates a new flow with no initial arrival, but a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow).
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices.
	 */
	public static void addFlow(ArrayList<Integer> route, ArrayList<Integer> priorities){

		//Creates the dummy arrivals for all vertices
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
		for(int i=0; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities);
		flows.put(FLOW_ID, flow);

		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Creates a new flow with no initial arrival, but a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow).
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices.
	 * @param alias the alias of the new flow
	 */
	public static void addFlow(ArrayList<Integer> route, ArrayList<Integer> priorities, String alias){

		//Creates the dummy arrivals for all vertices
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
		for(int i=0; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
		flows.put(FLOW_ID, flow);

		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	
	/**
	 * Creates a new flow with an initial arrival and a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow). The priorities at the vertices
	 * is identical to <code>priority</code>.
	 * @param initial_arrival the arrival at the first node
	 * @param vertices the  vertices the flow traverses
	 * @param priority the priorities of the flow at the 
	 * corresponding vertices
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(Arrival initial_arrival, ArrayList<Integer> route, int priority) throws ArrivalNotAvailableException{

		//Creates the dummy arrivals for all vertices after the first
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(1);
		arrivals.add(0, initial_arrival);
		for(int i=1; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Creates the priority-array
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		for(int i=0; i < route.size(); i++){
			priorities.add(priority);
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route,arrivals,priorities);
		flows.put(FLOW_ID, flow);

		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		Vertex first_vertex = vertices.get(route.get(0));
		
		//Initializes the first arrival at the first vertex
		first_vertex.learnArrival(FLOW_ID, initial_arrival);
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Creates a new flow with an initial arrival and a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow). The priorities at the vertices
	 * is identical to <code>priority</code>.
	 * @param initial_arrival the arrival at the first node
	 * @param vertices the  vertices the flow traverses
	 * @param priority the priorities of the flow at the 
	 * corresponding vertices
	 * @param alias the alias of the new flow
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(Arrival initial_arrival, ArrayList<Integer> route, int priority, String alias) 
			throws ArrivalNotAvailableException{

		//Creates the dummy arrivals for all vertices after the first
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(1);
		arrivals.add(0, initial_arrival);
		for(int i=1; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Creates the priority-array
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		for(int i=0; i < route.size(); i++){
			priorities.add(priority);
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
		flows.put(FLOW_ID, flow);

		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		Vertex first_vertex = vertices.get(route.get(0));
		
		//Initializes the first arrival at the first vertex
		first_vertex.learnArrival(FLOW_ID, initial_arrival);
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Creates a new flow with an initial arrival and a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow).
	 * @param initial_arrival the arrival at the first node
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(Arrival initial_arrival, ArrayList<Integer> route, ArrayList<Integer> priorities) throws ArrivalNotAvailableException{

		// Creates the dummy arrivals for all vertices after the first
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(1);
		arrivals.add(0, initial_arrival);
		for(int i=1; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route,arrivals,priorities);
		flows.put(FLOW_ID, flow);

		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		Vertex first_vertex = vertices.get(route.get(0));
		
		//Initializes the first arrival at the first vertex
		first_vertex.learnArrival(FLOW_ID, initial_arrival);
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Creates a new flow with an initial arrival and a complete
	 * description of its route through the network (in expression
	 * all of its vertices and the corresponding priorities at 
	 * these vertices of the flow).
	 * @param initial_arrival the arrival at the first node
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices
	 * @param alias the alias of the new flow
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(Arrival initial_arrival, ArrayList<Integer> route, ArrayList<Integer> priorities,
								String alias) throws ArrivalNotAvailableException{

		// Creates the dummy arrivals for all vertices after the first
		ArrayList<Arrival> arrivals = new ArrayList<Arrival>(1);
		arrivals.add(0, initial_arrival);
		for(int i=1; i < route.size(); i++){
			arrivals.add(new Arrival());
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
		flows.put(FLOW_ID, flow);

		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
		}
		
		Vertex first_vertex = vertices.get(route.get(0));
		
		//Initializes the first arrival at the first vertex
		first_vertex.learnArrival(FLOW_ID, initial_arrival);
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Adds a flow with all its arrivals, priorities and vertices
	 * to the network
	 * @param arrivals the arrivals at the vertices. Normally only
	 * the initial arrival is needed and hence all other arrivals
	 * will be overwritten by the analysis.
	 * @param vertices the  vertices the flow traverses
	 * @param priority the priorities of the flow at the 
	 * corresponding vertices
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(ArrayList<Arrival> arrivals, ArrayList<Integer> route, int priority) throws ArrivalNotAvailableException{

		//Constructs the priorities array
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		for(int i=0; i < route.size(); i++){
			priorities.add(priority);
		}
		
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities);
		
		//Adds the flow into the flow list
		flows.put(FLOW_ID, flow);
		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < vertices.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
			vertex.learnArrival(FLOW_ID, arrivals.get(i));
		}
	}

		/**
		 * Adds a flow with all its arrivals, priorities and vertices
		 * to the network
		 * @param arrivals the arrivals at the vertices. Normally only
		 * the initial arrival is needed and hence all other arrivals
		 * will be overwritten by the analysis.
		 * @param vertices the  vertices the flow traverses
		 * @param priority the priorities of the flow at the 
		 * corresponding vertices
		 * @param alias the alias of the new flow
		 * @throws ArrivalNotAvailableException
		 */
		public static void addFlow(ArrayList<Arrival> arrivals, ArrayList<Integer> route, int priority, 
									String alias) throws ArrivalNotAvailableException{

			//Constructs the priorities array
			ArrayList<Integer> priorities = new ArrayList<Integer>(0);
			for(int i=0; i < route.size(); i++){
				priorities.add(priority);
			}
			
			Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
			
			//Adds the flow into the flow list
			flows.put(FLOW_ID, flow);
			
			//Writes the flow in its corresponding vertices
			for(int i=0; i < vertices.size(); i++){
				Vertex vertex;
				vertex = vertices.get(route.get(i));
				vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
				vertex.learnArrival(FLOW_ID, arrivals.get(i));
			}

		//Increments the flow count
		incrementFLOW_ID();
	}
	
	/**
	 * Adds a flow with all its arrivals, priorities and vertices
	 * to the network
	 * @param arrivals the arrivals at the vertices. Normally only
	 * te initial arrival is needed and hence all other arrivaly
	 * will be overwritten by the analysis.
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(ArrayList<Arrival> arrivals, ArrayList<Integer> route, ArrayList<Integer> priorities) throws ArrivalNotAvailableException{

		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities);
		
		//Adds the flow into the flow list
		flows.put(FLOW_ID, flow);
		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
			vertex.learnArrival(FLOW_ID, arrivals.get(i));
		}
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Adds a flow with all its arrivals, priorities and vertices
	 * to the network
	 * @param arrivals the arrivals at the vertices. Normally only
	 * te initial arrival is needed and hence all other arrivaly
	 * will be overwritten by the analysis.
	 * @param vertices the  vertices the flow traverses
	 * @param priorities the priorities of the flow at the 
	 * corresponding vertices
	 * @param alias the alias of the new flow
	 * @throws ArrivalNotAvailableException
	 */
	public static void addFlow(ArrayList<Arrival> arrivals, ArrayList<Integer> route, 
								ArrayList<Integer> priorities, String alias) throws ArrivalNotAvailableException{

		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias);
		
		//Adds the flow into the flow list
		flows.put(FLOW_ID, flow);
		
		//Writes the flow in its corresponding vertices
		for(int i=0; i < route.size(); i++){
			Vertex vertex;
			vertex = vertices.get(route.get(i));
			vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
			vertex.learnArrival(FLOW_ID, arrivals.get(i));
		}
		
		//Increments the flow count
		incrementFLOW_ID();
	}

	/**
	 * Appends a node to an already existing flow. The arrival at
	 * this appended node is non established.
	 * @param flow_id the flow ot which the node is appended
	 * @param vertex_id the vertex which is appended
	 * @param priority the priority the flow has at the appended
	 * vertex.
	 */
	public static void appendNode(int flow_id, int vertex_id, int priority){
		
		//Adds the vertex to the path of the flow
		flows.get(flow_id).addNodetoPath(vertex_id, priority);
		
		//Adds a non-established arrival to the appended vertex
		vertices.get(vertex_id).addUnknownArrival(priority, flow_id);
	}
	
	/**
	 * Appends a node to an already existing flow. The arrival at
	 * this appended node is non established.
	 * @param flow_alias the flows, to which the node is appended, alias
	 * @param vertex_id the vertex which is appended
	 * @param priority the priority the flow has at the appended
	 * vertex.
	 */
	public static void appendNode(String flow_alias, int vertex_id, int priority){
		Flow flow = null;
		for(Map.Entry<Integer, Flow> entry : flows.entrySet()){
			if(entry.getValue().getAlias() == flow_alias) flow = entry.getValue();
		}
		if(flow != null){
			//Adds the vertex to the path of the flow
			flow.addNodetoPath(vertex_id, priority);
			
			//Adds a non-established arrival to the appended vertex
			vertices.get(vertex_id).addUnknownArrival(priority, flow.getFlow_ID());
		}
		else throw new NullPointerException("There exists no flow with this alias.");
	}
	
	/**
	 * Appends a node to an already existing flow. The arrival at
	 * this appended node is non established.
	 * @param flow_id the flows, to which the node is appended, id
	 * @param vertex_alias the alias of the vertex which is appended
	 * @param priority the priority the flow has at the appended
	 * vertex.
	 */
	public static void appendNode(int flow_id, String vertex_alias, int priority){
		Vertex vertex = null;
		for(Map.Entry<Integer, Vertex> entry : vertices.entrySet()){
			if(entry.getValue().getAlias() == vertex_alias) vertex = entry.getValue();
		}
		if(vertex != null){
			//Adds the vertex to the path of the flow
			flows.get(flow_id).addNodetoPath(vertex.getVertexID(), priority);
			
			//Adds a non-established arrival to the appended vertex
			vertices.get(vertex.getVertexID()).addUnknownArrival(priority, flow_id);
		}
		else throw new NullPointerException("The vertex with the given alias does not exist.");
	}
	
	/**
	 * Appends a node to an already existing flow. The arrival at
	 * this appended node is non established.
	 * @param flow_alias the flows, to which the node is appended, alias
	 * @param vertex_alias the alias of the vertex which is appended
	 * @param priority the priority the flow has at the appended
	 * vertex.
	 */
	public static void appendNode(String flow_alias, String vertex_alias, int priority){
		Vertex vertex = null;
		Flow flow = null;
		for(Map.Entry<Integer, Vertex> entry : vertices.entrySet()){
			if(entry.getValue().getAlias() == vertex_alias) vertex = entry.getValue();
		}
		for(Map.Entry<Integer, Flow> entry : flows.entrySet()){
			if(entry.getValue().getAlias() == flow_alias) flow = entry.getValue();
		}
		if(vertex != null && flow != null){
			//Adds the vertex to the path of the flow
			flow.addNodetoPath(vertex.getVertexID(), priority);
			
			//Adds a non-established arrival to the appended vertex
			vertex.addUnknownArrival(priority, flow.getFlow_ID());
		}
		else if(vertex == null) throw new NullPointerException("The vertex with the given alias does not exist.");
		else throw new NullPointerException("The flow with the given alias does not exist.");
	}
	
	/**
	 * Sets the initial arrival of some flow
	 * @param flow_id the flow-id of the flow, which is initialized
	 * @param arrival the initial arrival
	 * @throws ArrivalNotAvailableException
	 */
	public static void setInitialArrival(int flow_id, Arrival arrival) throws ArrivalNotAvailableException{
		
		//initializes the arrival at the flow
		flows.get(flow_id).setInitialArrival(arrival);

		//the arrival is established at the vertex
		Vertex vertex = vertices.get(flows.get(flow_id).getFirstVertexID());
		vertex.learnArrival(flow_id, arrival);
	}
	
	/**
	 * Sets the initial arrival of some flow
	 * @param alias the alias of the flow, which is initialized
	 * @param arrival the initial arrival
	 * @throws ArrivalNotAvailableException
	 */
	public static void setInitialArrival(String alias, Arrival arrival) throws ArrivalNotAvailableException{
		Flow flow = null;
		for(Map.Entry<Integer, Flow> entry : flows.entrySet()){
			if(entry.getValue().getAlias() == alias) flow = entry.getValue();
		}
		if(flow != null){
			//initializes the arrival at the flow
			flows.get(flow.getFlow_ID()).setInitialArrival(arrival);

			//the arrival is established at the vertex
			Vertex vertex = vertices.get(flows.get(flow.getFlow_ID()).getFirstVertexID());
			vertex.learnArrival(flow.getFlow_ID(), arrival);
		}
		else throw new NullPointerException("There is no flow with the given alias.");
	}
	
	/**
	 * Removes a flow from the network. This is done by 
	 * deleting the corresponding entries in the vertices, 
	 * which lie on the route of the flow. After this the
	 * flow itself is removed from <code>flows</code>.
	 * @param flow the <code>Flow</code> to be removed
	 * @return returns if removing the flow was successful
	 */
	public static boolean removeFlow(Flow flow) {
		boolean success = false;
		if(flows.containsKey(flow.getFlow_ID())){
			for(int i : flow.getVerticeIDs()){
				vertices.get(i).removeFlow(flow.getFlow_ID());
			}
			flows.remove(flow.getFlow_ID());
			success = true;
			
		}
		return success;
	}
	
	private static void incrementFLOW_ID(){
		FLOW_ID++;
	}
	
	private static void incrementVERTEX_ID(){
		VERTEX_ID++;
	}
		
	private static void incrementHOELDER_ID(){
		HOELDER_ID++;
	}
	
	public static void resetFLOW_ID(int reset){
		FLOW_ID = reset;
	}
	
	public static void resetVERTEX_ID(int reset){
		VERTEX_ID = reset;
	}
	
	public static void resetHOELDER_ID(int reset){
		HOELDER_ID = reset;
	}
	
	/**
	 * Returns a string representation of the network.
	 * This is done by first listing the vertices aliases
	 * or if not existend their IDs.
	 * @return
	 */
	public static String getStringRepresentation(){
		String result_string = "List of vertices:\n";
		for(Map.Entry<Integer, Vertex> entry : vertices.entrySet()){
			result_string = result_string+"Vertex-ID: "+entry.getValue().getVertexID()+
					"\t Vertex-Alias: "+entry.getValue().getAlias()+"\n";
		}
		result_string = result_string+"List of flows:\n";
		for(Map.Entry<Integer, Flow> entry : flows.entrySet()){
			result_string = result_string+"Flow-ID: "+entry.getValue().getFlow_ID()+
					"\t Flow-Alias: "+entry.getValue().getAlias()+"\t route:\n"+entry.getValue().getVerticeIDs().toString()+"\n";			
		}
		
		return result_string+"Number of Hoelder parameters: "+(HOELDER_ID-1);
	}
	
	//Getter and Setter
		
	public static int getVERTEX_ID(){
		return VERTEX_ID;
	}
	
	public static int getFLOW_ID(){
		return FLOW_ID;
	}
	
	public static int getHOELDER_ID(){
		return HOELDER_ID;
	}
	
	public static HashMap<Integer, Vertex> getVertices(){
		return vertices;
	}
	
	public static void setVertices(HashMap<Integer, Vertex> newVertices){
		Network.vertices = newVertices;
	}
	
	public static HashMap<Integer, Flow> getFlows(){
		return flows;
	}
	
	public static void setFlows(HashMap<Integer, Flow> newFlows){
		Network.flows = newFlows;
	}
	
	public static HashMap<Integer, Hoelder> getHoelders(){
		return hoelders;
	}
	
	public static void setHoelders(HashMap<Integer, Hoelder> newHoelders){
		Network.hoelders = newHoelders;
	}

}
