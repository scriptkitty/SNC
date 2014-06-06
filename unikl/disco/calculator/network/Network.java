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

package unikl.disco.calculator.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.Service;

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
	
	private int FLOW_ID;
	private int VERTEX_ID;
	private int HOELDER_ID;
	private Map<Integer,Flow> flows;
	private Map<Integer,Vertex> vertices;
	private Map<Integer,Hoelder> hoelders;
        private List<NetworkListener> listeners;
	
	// Constructor
	
	public Network(){
            this(null, null, null);
        }
        
        public Network(Map<Integer, Vertex> vertices, Map<Integer, Flow> flows, Map<Integer, Hoelder> hoelders) {
            this.flows = (flows != null) ? flows : new HashMap<Integer, Flow>();
            this.vertices = (vertices != null) ? vertices : new HashMap<Integer, Vertex>();
            this.hoelders = (hoelders != null) ? hoelders : new HashMap<Integer, Hoelder>();
            FLOW_ID = this.flows.size() + 1;
            VERTEX_ID = this.vertices.size() + 1;
            HOELDER_ID = this.hoelders.size() + 1;
            this.listeners = new ArrayList();
        }
	
	//Methods
	
        public boolean addListener(NetworkListener l) {
            return listeners.add(l);
        }
        
        public boolean removeListener(NetworkListener l) {
            return listeners.remove(l);
        }
	/**
	 * Creates a new Hoelder-Object and returns its id.
	 * @return the newly created Hoelder-Object.
	 */
	public Hoelder createHoelder(){
		Hoelder hoelder = new Hoelder(HOELDER_ID);
		hoelders.put(hoelder.getHoelderID(), hoelder);
		incrementHOELDER_ID();
		return hoelder;
	}
	
	/**
	 * Adds a new dummy vertex with alias 
	 */
	public void addVertex(String alias){
		Vertex vertex = new Vertex(VERTEX_ID, alias, this);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
                for (NetworkListener l : listeners) {
                    l.vertexAdded(vertex);
                
            }
	}
	
	/**
	 * Adds a new vertex with predefined service to the network
	 * @param service the service the new vertex possesses
	 */
	public void addVertex(Service service){
            addVertex(service, "");
	}
	
	/**
	 * Adds a new vertex with predefined service and alias
	 * @param service
	 */
	public Vertex addVertex(Service service, String alias){
		Vertex vertex = new Vertex(VERTEX_ID, service, alias, this);
		vertices.put(VERTEX_ID, vertex);
		incrementVERTEX_ID();
                for (NetworkListener l : listeners) {
                    l.vertexAdded(vertex);
            }
            return vertex;
	}
        
        public int convolute(int vertex1ID, int vertex2ID) {
            // Compute convoluted service, add a new vertex with that service
            // and redirect all flows
            Vertex v1 = getVertex(vertex1ID);
            Vertex v2 = getVertex(vertex2ID);
            Service convService = v1.getService().concatenate(v1.getService(), v2.getService());
            Vertex convVertex = addVertex(convService, vertex1ID + " conv. " + vertex2ID);
            Set<Integer> v1Flows = v1.getAllFlowIDs();
            Set<Integer> v2Flows = v2.getAllFlowIDs();
            v1Flows.addAll(v2Flows);
            
            for (Integer flowID : v1Flows) {
                //convVertex.addArrival(priority, flowID, );
            }
            return convVertex.getVertexID();
        }
	
	/**
	 * Sets the service at a specific node in the network
	 * @param vertex the vertex to be altered
	 * @param service the new service at the specific vertex
	 */
	public void setServiceAt(Vertex vertex, Service service){
		vertex.setMGFService(service);
	}
	
	/**
	 * Sets the service at the vertex with the given id
	 * @param vertex_id the id of the vertex to be altered
	 * @param service the new service at the specific vertex
	 */
	public void setServiceAt(int vertex_id, Service service){
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
	public boolean removeVertex(Vertex vertex) {
            return removeVertex(vertex.getVertexID());
	}
        
        public boolean removeVertex(int id) {
            boolean success = false;
		if(vertices.containsKey(id)){
			for(int i : getVertex(id).getAllFlowPriorities().keySet()){
				flows.get(i).removeVertex(id);
			}
			vertices.remove(id);
			success = true;
		}
                // Notify listeners
                for (NetworkListener l : listeners) {
                    l.vertexRemoved(getVertex(id));
                
            }
            return success;
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
	public int addFlow(Arrival initial_arrival, List<Integer> route, List<Integer> priorities,
								String alias) throws ArrivalNotAvailableException{

		// Creates the dummy arrivals for all vertices after the first
		List<Arrival> arrivals = new ArrayList<Arrival>(1);
		arrivals.add(0, initial_arrival);
		for(int i=1; i < route.size(); i++){
			arrivals.add(new Arrival(this));
		}
		
		//Adds the flow into the flow list
		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias, this);
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
                
                // Notify the listeners
                for (NetworkListener l : listeners) {
                    l.flowAdded(flow);
            }
	    return flow.getFlowID();
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
	public void addFlow(List<Arrival> arrivals, List<Integer> route, 
								List<Integer> priorities, String alias) throws ArrivalNotAvailableException{

		Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias, this);
		
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
                
                // Notify the listeners
                for (NetworkListener l : listeners) {
                    l.flowAdded(flow);
                
            }
	}

	/**
	 * Appends a node to an already existing flow. The arrival at
	 * this appended node is non established.
	 * @param flow_id the flow ot which the node is appended
	 * @param vertex_id the vertex which is appended
	 * @param priority the priority the flow has at the appended
	 * vertex.
	 */
	public void appendNode(int flow_id, int vertex_id, int priority){
		
		//Adds the vertex to the path of the flow
		flows.get(flow_id).addNodetoPath(vertex_id, priority);
		
		//Adds a non-established arrival to the appended vertex
		vertices.get(vertex_id).addUnknownArrival(priority, flow_id);
	}
	
	/**
	 * Sets the initial arrival of some flow
	 * @param flow_id the flow-id of the flow, which is initialized
	 * @param arrival the initial arrival
	 * @throws ArrivalNotAvailableException
	 */
	public void setInitialArrival(int flow_id, Arrival arrival) throws ArrivalNotAvailableException{
		
		//initializes the arrival at the flow
		flows.get(flow_id).setInitialArrival(arrival);

		//the arrival is established at the vertex
		Vertex vertex = vertices.get(flows.get(flow_id).getFirstVertexID());
		vertex.learnArrival(flow_id, arrival);
	}
	
	/**
	 * Removes a flow from the network. This is done by 
	 * deleting the corresponding entries in the vertices, 
	 * which lie on the route of the flow. After this the
	 * flow itself is removed from <code>flows</code>.
	 * @param flow the <code>Flow</code> to be removed
	 * @return returns if removing the flow was successful
	 */
	public boolean removeFlow(Flow flow) {
		boolean success = false;
		if(flows.containsKey(flow.getFlowID())){
			for(int i : flow.getVerticeIDs()){
				vertices.get(i).removeFlow(flow.getFlowID());
			}
			flows.remove(flow.getFlowID());
			success = true;
			
		}
                // Notify the listeners
                for (NetworkListener l : listeners) {
                    l.flowRemoved(flow);
                
            }
		return success;
	}
	
	private void incrementFLOW_ID(){
		FLOW_ID++;
	}
	
	private void incrementVERTEX_ID(){
		VERTEX_ID++;
	}
		
	private void incrementHOELDER_ID(){
		HOELDER_ID++;
	}
	
	public void resetFLOW_ID(int reset){
		FLOW_ID = reset;
	}
	
	public void resetVERTEX_ID(int reset){
		VERTEX_ID = reset;
	}
	
	public void resetHOELDER_ID(int reset){
		HOELDER_ID = reset;
	}
	
	/**
	 * Returns a string representation of the network.
	 * This is done by first listing the vertices aliases
	 * or if not existend their IDs.
	 * @return
	 */
	public String getStringRepresentation(){
		String result_string = "List of vertices:\n";
		for(Map.Entry<Integer, Vertex> entry : vertices.entrySet()){
			result_string = result_string+"Vertex-ID: "+entry.getValue().getVertexID()+
					"\t Vertex-Alias: "+entry.getValue().getAlias()+"\n";
		}
		result_string = result_string+"List of flows:\n";
		for(Map.Entry<Integer, Flow> entry : flows.entrySet()){
			result_string = result_string+"Flow-ID: "+entry.getValue().getFlowID()+
					"\t Flow-Alias: "+entry.getValue().getAlias()+"\t route:\n"+entry.getValue().getVerticeIDs().toString()+"\n";			
		}
		
		return result_string+"Number of Hoelder parameters: "+(HOELDER_ID-1);
	}
	
	//Getter and Setter
	public Vertex getVertex(int id) {
            return vertices.get(id);
        }
        
        public Flow getFlow(int id) {
            return flows.get(id);
        }
        
	public int getVERTEX_ID(){
		return VERTEX_ID;
	}
	
	public int getFLOW_ID(){
		return FLOW_ID;
	}
	
	public int getHOELDER_ID(){
		return HOELDER_ID;
	}
	
	public Map<Integer, Vertex> getVertices(){
		return vertices;
	}
        
        public Network copy() {
           return new Network();
        }
	
	
    /**
     * Loads a network, which is given in <code>file</code>. Can only read networks, which
     * had been saved by a simple <code>ObjectOutputStream</code>. The order of saved objects
     * (and its corresponding type) is:
     * vertices (HashMap<Integer, Vertex>
     * flows (HashMap<Integer, Flow>)
     * hoelders (HashMap<Integer, Hoelder>)
     */
    public static Network load(File file) {
        Map<Integer, Vertex> newVertices = null;
        Map<Integer, Flow> newFlows = null;
        Map<Integer, Hoelder> newHoelders = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            newVertices = (Map<Integer, Vertex>) ois.readObject();
            newFlows = (Map<Integer, Flow>) ois.readObject();
            newHoelders = (Map<Integer, Hoelder>) ois.readObject();
            ois.close();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        Network nw = new Network(newVertices, newFlows, newHoelders);
        return nw;
    }

    /**
     * Saves the network in the given <code>file</code>. This is done by using a
     * simple ObjectOutputStream. The order of saved objects (and its corresponding
     * type) is:
     * vertices (HashMap<Integer, Vertex>
     * flows (HashMap<Integer, Flow>)
     * hoelders (HashMap<Integer, Hoelder>)
     */
    public void save(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.getVertices());
            oos.writeObject(this.getFlows());
            oos.writeObject(this.getHoelders());
            oos.close();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }
    
    public Map<Integer, Flow> getFlows(){
        return flows;
    }
	
    public Map<Integer, Hoelder> getHoelders(){
        return hoelders;
    }
	
}
