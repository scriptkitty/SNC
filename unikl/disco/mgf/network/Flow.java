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

import java.io.Serializable;
import java.util.ArrayList;

import unikl.disco.mgf.Arrival;

/**
 * Describes a flow through the network and gives it an initial
 * arrival. The route through the network is given by an ArrayList
 * of {@link Vertex}-ids. At each vertice the flow has a priority
 * given in the ArrayList <code>priorities</code>. Further <code>
 * flow</code> has a third ArrayList, which consists of {@link 
 * Arrival}s. Initially at most the first arrival in that array is
 * given, further arrivals are initialized by calculating output-
 * bounds in the {@Vertex}-class. Calculated arrivals are called
 * established.
 * @author Michael Beck
 * @see Analysis
 * @see Vertex
 * @see Arrival
 */
public class Flow implements Serializable {
	
	//Members
	
	private static final long serialVersionUID = 7989846738211040015L;
	private ArrayList<Integer> vertices;
	private ArrayList<Arrival> arrivals;
	private ArrayList<Integer> priorities;
	
	private int flow_ID;
	private String alias;
	
	private int established_arrivals;
        
        private Network nw;
	
	//Constructors
	
	public Flow(int flow_ID, Network nw){
		vertices = new ArrayList<Integer>(0);
		arrivals = new ArrayList<Arrival>(0);
		priorities = new ArrayList<Integer>(0);
		established_arrivals = 0;
		this.flow_ID= flow_ID;
                this.nw = nw;
	}
	
	public Flow(int flow_ID, String alias, Network nw){
		vertices = new ArrayList<Integer>(0);
		arrivals = new ArrayList<Arrival>(0);
		priorities = new ArrayList<Integer>(0);
		established_arrivals = 0;
		this.flow_ID= flow_ID;
		this.setAlias(alias);
                this.nw = nw;
	}
	/**
	 * Constructs a flow, with the complete route through the 
	 * network and priorities at the vertices given.
	 * @param vertices the vertex-ids, through which the flow
	 * traverses the network. vertices[0] denotes the id of the 
	 * first vertex the flow traverses.
	 * @param arrivals the arrivals of the flow, including the 
	 * initial arrival at the first flow, given by arrivals[0]. 
	 * Further arrivals might be overwritten by invoking {@link 
	 * Analysis}.
	 * @param priorities the priorities of the flows at the 
	 * different vertices. priorities[0] describes the priority of 
	 * the flow at the first vertex. The flows are served in 
	 * descending priority numbers, in expression the flow with the
	 * highest priority number is served first.
	 */
	public Flow(int flow_ID, ArrayList<Integer> vertices, ArrayList<Arrival> arrivals, 
				ArrayList<Integer> priorities, Network nw){
		this.vertices = vertices;
		this.arrivals = arrivals;
		this.priorities = priorities;
		established_arrivals = 1;
		this.flow_ID = flow_ID;
		arrivals.get(0).addArrivalDependency(flow_ID);
                this.nw = nw;
	}
	
	public Flow(int flow_ID, ArrayList<Integer> vertices, ArrayList<Arrival> arrivals, 
			ArrayList<Integer> priorities, String alias, Network nw){
		this.vertices = vertices;
		this.arrivals = arrivals;
		this.priorities = priorities;
		established_arrivals = 1;
		this.flow_ID = flow_ID;
		this.setAlias(alias);
		arrivals.get(0).addArrivalDependency(flow_ID);
                this.nw = nw;
	}

	
	//Methods
	
	/**
	 * Adds a node to this flow. A new dummy arrival is associated 
	 * to this new node. 
	 * @param vertex_ID the node, which is added to the flow
	 * @param priority the priority, which the flow has at this new
	 * node.
	 */
	public void addNodetoPath(int vertex_ID, int priority){
		vertices.add(vertex_ID);
		arrivals.add(new Arrival(nw));
		priorities.add(priority);
	}
	
	/**
	 * The flow learns its next arrival. This means, that the next
	 * dummy arrival is overwritten by the input and the number of
	 * established arrivals is increased by one.
	 * @param arrival overwrites the next dummy arrival in the flow
	 */
	public void learnArrival(Arrival arrival){
		arrivals.set(established_arrivals, arrival);
		established_arrivals++;
	}
	
	/**
	 * Removes a vertex form this flow, i.e. removes it from the 
	 * route together with its corresponding priority and arrival
	 * entries. If the vertex appears several times in the route 
	 * all entries containing this vertex will be deleted.
	 * @param vertexID
	 */
	public void removeVertex(int vertexID){
		while(true){
			if(vertices.contains(vertexID)){
				int index = vertices.indexOf(vertexID);
				vertices.remove(index);
				arrivals.remove(index);
				priorities.remove(index);
				if(established_arrivals-1 == index) established_arrivals--;

			}
			else break;
		}
		
	}
	
	/**
	 * Returns a copy of this flow.
	 */
	public Flow copy(){
		Flow copy = new Flow(flow_ID, vertices, arrivals, priorities, alias, nw);
		copy.setEstablishedArrivals(getNumberOfEstablishedArrivals());
		return copy;
	}
	
	//Getter and Setter
	
	/**
	 * Sets the initial arrival of the flow. This will overwrite 
	 * any initial arrivals, which may have been given before. 
	 * Further the number of established arrivals is set to 1. This
	 * means all arrivals, which may have been established before
	 * are now again non-established and will be overwritten by 
	 * an analysis.
	 * @param arrival the new initial arrival
	 */
	public void setInitialArrival(Arrival arrival){
		arrivals.add(0, arrival);
		arrivals.get(0).addArrivalDependency(flow_ID);
		established_arrivals = 1;
	}
	
	/**
	 * returns the next vertex_id, for which its arrival is not 
	 * yet established.
	 * @return the vertex_id of the vertex, which has the first 
	 * non-established arrival of this flow.
	 */
	public int getNextVertexID(){
		return vertices.get(established_arrivals);
	}
	
	/**
	 * returns the vertext_id of the node, whose arrival was the 
	 * last established one.
	 */
	public int getCurrentVertexID(){
		if(established_arrivals > 1){
			return vertices.get(established_arrivals-1);
		}
		else
			return 0;
	}

	/**
	 * returns the vertex_id of the first node of the flow.
	 */
	public int getFirstVertexID(){
		return vertices.get(0);
	}
	
	/**
	 * Returns the last established arrival.
	 */
	public Arrival getLastArrival(){
		return arrivals.get(established_arrivals-1);
	}
	
	/**
	 * Returns the number of established arrivals.
	 */
	public int getNumberOfEstablishedArrivals(){
		return established_arrivals;
	}
	
	/**
	 * Needed for copy-operation
	 */
	private void setEstablishedArrivals(int i){
		established_arrivals = i;
	}

	public int getFlow_ID() {
		return flow_ID;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public ArrayList<Integer> getVerticeIDs(){
		return vertices;
	}

	public ArrayList<Integer> getPriorities() {
		return priorities;
	}

	public Arrival getInitialArrival() throws IndexOutOfBoundsException {
		return arrivals.get(0);
	}
}
