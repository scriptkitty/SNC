package unikl.disco.mgf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import unikl.disco.mgf.GUI.GUI;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.ArrivalNotAvailableException;
import unikl.disco.mgf.network.DeadlockException;
import unikl.disco.mgf.network.Flow;
import unikl.disco.mgf.network.Network;
import unikl.disco.mgf.network.SimpleAnalysis;
import unikl.disco.mgf.network.Vertex;
import unikl.disco.mgf.optimization.SimpleGradient;
import unikl.disco.mgf.optimization.SimpleOptimizer;

/**
 * This class contains the main method, which starts and prepares the GUI.
 * It also serves as interface relaying the commands from the user to the 
 * corresponding classes {@link Network}, {@SimpleAnalysis} etc. Alternatively
 * the implemented methods can be used directly in the main-method to 
 * construct a network and perform calculations on it.
 * @author Michael Beck
 *
 */
public class SNC {

	//Members
	private static HashMap<Integer, Flow> flows;
	private static HashMap<Integer, Vertex> vertices;
	
	public enum OptimizationType{
		SIMPLE_OPT{
			public String toString(){
				return "Simple Optimization";
			}
		},
		
		GRADIENT_OPT{
			public String toString(){
				return "Gradient Heuristic";
			}
		}
	};
	
	public enum AnalysisType{
		SIMPLE_ANA{
			public String toString(){
				return "Simple Analysis";
			}
		}
	};
	
	private static GUI gui;
	
	//Main-Method
	public static void main(String[] args) throws InvocationTargetException, InterruptedException, 
	ArrivalNotAvailableException, BadInitializationException, DeadlockException, ThetaOutOfBoundException, 
	ParameterMismatchException, ServerOverloadException{
		
		SNC snc = new SNC();
		
		vertices = Network.getVertices();
		
		flows = Network.getFlows();
		
		gui = new GUI(snc);
		SwingUtilities.invokeLater(gui);
		
	}
		
	//Methods
	
	/**
	 * Loads a network, which is given in <code>file</code>. Can only read networks, which
	 * had been saved by a simple <code>ObjectOutputStream</code>. The order of saved objects
	 * (and its corresponding type) is: 
	 * vertices (HashMap<Integer, Vertex>
	 * flows (HashMap<Integer, Flow>) 
	 * hoelders (HashMap<Integer, Hoelder>)
	 */
	public void loadNetwork(File file){
		
		try{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			@SuppressWarnings("unchecked")
			HashMap<Integer, Vertex> newVertices = (HashMap<Integer, Vertex>) ois.readObject();
			@SuppressWarnings("unchecked")
			HashMap<Integer, Flow> newFlows = (HashMap<Integer, Flow>) ois.readObject();
			@SuppressWarnings("unchecked")
			HashMap<Integer, Hoelder> newHoelders = (HashMap<Integer, Hoelder>) ois.readObject();
			
			Network.setVertices(newVertices);
			Network.setFlows(newFlows);
			Network.setHoelders(newHoelders);
			
			vertices = Network.getVertices();
			flows = Network.getFlows();
			
			ois.close();
		}
		catch(Exception exc){
			System.out.println(exc.getMessage());
		}
		
	}
	
	/**
	 * Saves the network in the given <code>file</code>. This is done by using a 
	 * simple ObjectOutputStream. The order of saved objects (and its corresponding 
	 * type) is: 
	 * vertices (HashMap<Integer, Vertex>
	 * flows (HashMap<Integer, Flow>) 
	 * hoelders (HashMap<Integer, Hoelder>)
	 */
	public void saveNetwork(File file){
		
		try{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		
			oos.writeObject(vertices);
			oos.writeObject(flows);
			oos.writeObject(Network.getHoelders());
			
			oos.close();
		}
		catch(Exception exc){
			System.out.println(exc.getMessage());
		}
	}
	
	/**
	 * Relays the command of removing a given <code>flow</code> from the
	 * network to the {@link Network}-class.  
	 * @param flow the <code>Flow</code> being removed.
	 * @return Returns <code>true</code> if the flow has been successfully
	 * removed and <code>false</code> otherwise.
	 */
	public boolean removeFlow(Flow flow) {
		return Network.removeFlow(flow);
	}
	
	/**
	 * Relays the command of adding a given <code>flow</code> to the network
	 * to the {@link Network}-class. Pay attention to the stochastic dependencies
	 * the given <code>flow</code> might have been initialized with. They will be
	 * forwarded to the flow added to the network. 
	 * @param flow the flow, which will be added to the network.
	 * @return For the case that the flow was successfully added to the network the
	 * flow-id will be returned. In the case that the flow could not been added 
	 * the return will be <code>-1</code> instead.
	 */
	public int addFlow(Flow flow){
	
		int id = -1;
		
		try{
			Network.addFlow(flow.getInitialArrival(), flow.getVerticeIDs(), 
					flow.getPriorities(), flow.getAlias());
			id = Network.getFLOW_ID()-1;
		}
		catch(IndexOutOfBoundsException e){
			System.out.println(e.getMessage());
			System.out.println("Flow has not been added. Possible reason: No initial arrival specified.");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Flow has not been added. Possible reason: No initial arrival specified.");
		}
		
		return id;
	}
	
	/**
	 * Relays the command of removing a given <code>vertex</code> from the
	 * network to the {@link Network}-class.  
	 * @param vertex the <code>Vertex</code> being removed.
	 * @return Returns <code>true</code> if the vertex has been successfully
	 * removed and <code>false</code> otherwise.
	 */
	public boolean removeVertex(Vertex vertex) {
		return Network.removeVertex(vertex);
	}
	
	/**
	 * Relays the command of adding a given <code>vertex</code> to the network
	 * to the {@link Network}-class. Pay attention to the stochastic dependencies
	 * the given <code>vertex</code> might have been initialized with. They will be
	 * forwarded to the vertex added to the network. 
	 * @param vertex the <code>Vertex</code>, which will be added to the network.
	 * @return For the case that the vertex was successfully added to the network the
	 * vertex-id will be returned. In the case that the vertex could not been added 
	 * the return will be <code>-1</code> instead.
	 */
	public int addVertex(Vertex vertex){
		
		int id = -1;
		
		try{
			Network.addVertex(vertex.getService(), vertex.getAlias());
			id = Network.getVERTEX_ID()-1;
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Vertex has not been added.");
		}
		
		return id;
	}
	
	/**
	 * This relays the command of calculating a symbolic (not optimized) 
	 * bound to the corresponding {@link AbstractAnalysis}-subclass. The 
	 * result is returned in arrival-representation.
	 * @param flow the <code>Flow</code> of interest.
	 * @param vertex the <code>Vertex</code> of interest.
	 * @param analyzer the type of analysis used
	 * @param boundtype the type of bound, which needs to be computed.
	 * @return the result of the analysis in arrival-representation.
	 */
	public Arrival analyzeNetwork(Flow flow, Vertex vertex, SNC.AnalysisType analyzer, AbstractAnalysis.Boundtype boundtype){
		
		//Preparations
		Arrival bound = null;
		
		HashMap<Integer, Vertex> givenVertices = new HashMap<Integer, Vertex>();
		for(Entry<Integer, Vertex> entry : vertices.entrySet()){
			givenVertices.put(entry.getKey(), entry.getValue().copy());
		}
		
		HashMap<Integer, Flow> givenFlows = new HashMap<Integer, Flow>();
		for(Entry<Integer, Flow> entry : flows.entrySet()){
			givenFlows.put(entry.getKey(), entry.getValue().copy());
		}
		
		int resetFlowID = Network.getFLOW_ID();
		int resetHoelderID = Network.getHOELDER_ID();
		int resetVertexID = Network.getVERTEX_ID();
		
		//Relays command to the corresponding analysis class
		switch(analyzer){
		case SIMPLE_ANA:
			SimpleAnalysis analysis = new SimpleAnalysis(givenVertices, givenFlows, flow.getFlow_ID(), vertex.getVertexID(), boundtype);
			try {
				bound = analysis.analyze();
			} catch (ArrivalNotAvailableException e) {
				e.printStackTrace();
			} catch (DeadlockException e) {
				e.printStackTrace();
			} catch (BadInitializationException e) {
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Unknown analysis type.");	
		}
		
		
		//Resets the network
		Network.resetFLOW_ID(resetFlowID);
		Network.resetHOELDER_ID(resetHoelderID);
		Network.resetVERTEX_ID(resetVertexID);
		
		return bound;
	}
	
	/**
	 * This relays the command of calculating a numerical (to some extent optimized) 
	 * bound to the corresponding {@link AbstractOptimizer}-subclass.
	 * @param flow the <code>Flow</code> of interest
	 * @param vertex the <code>Vertex</code> of interest
	 * @param thetaGran the step-size by which different values of theta are 
	 * allowed to differ.
	 * @param hoelderGran the step-size by which different values for hoelder 
	 * parameters are allowed to differ. 
	 * @param analyzer the type of analysis used
	 * @param optimizer the type of optimization used
	 * @param boundtype the type of bound searched for
	 * @param value the value of the delay or backlog bound
	 * @return the best probability found for the given delay or backlog bound
	 */
	public double calculateBound(Flow flow, Vertex vertex, double thetaGran, 
			double hoelderGran, SNC.AnalysisType analyzer, SNC.OptimizationType optimizer, AbstractAnalysis.Boundtype boundtype, double value){

		//Preparations
		//Backlog values are represented by negative values in the arrival representation
		if(boundtype == AbstractAnalysis.Boundtype.BACKLOG && value > 0) value = -value;
		
		double probability = 1;
		
		HashMap<Integer, Vertex> givenVertices = new HashMap<Integer, Vertex>();
		for(Entry<Integer, Vertex> entry : vertices.entrySet()){
			givenVertices.put(entry.getKey(), entry.getValue().copy());
		}
		
		HashMap<Integer, Flow> givenFlows = new HashMap<Integer, Flow>();
		for(Entry<Integer, Flow> entry : flows.entrySet()){
			givenFlows.put(entry.getKey(), entry.getValue().copy());
		}
		
		int resetFlowID = Network.getFLOW_ID();
		int resetHoelderID = Network.getHOELDER_ID();
		int resetVertexID = Network.getVERTEX_ID();
		
		switch(analyzer){
		case SIMPLE_ANA:
			//Computes the bound in arrival-representation
			SimpleAnalysis analysis = new SimpleAnalysis(givenVertices, givenFlows, flow.getFlow_ID(), vertex.getVertexID(), boundtype);
			Arrival bound = null;
			try {
				bound = analysis.analyze();
			} catch (ArrivalNotAvailableException e) {
				e.printStackTrace();
			} catch (DeadlockException e) {
				e.printStackTrace();
			} catch (BadInitializationException e) {
				e.printStackTrace();
			}
			
			//Optimizes the bound
			switch(optimizer){
			case SIMPLE_OPT:
				SimpleOptimizer simple = new SimpleOptimizer(bound, boundtype);
				try {
					probability = simple.Bound(bound, boundtype, value, thetaGran, hoelderGran);
				} catch (ThetaOutOfBoundException e) {
					e.printStackTrace();
				} catch (ParameterMismatchException e) {
					e.printStackTrace();
				} catch (ServerOverloadException e) {
					e.printStackTrace();
				}
				break;
			
			case GRADIENT_OPT:
				SimpleGradient gradient = new SimpleGradient(bound, boundtype);
				try{
					probability = gradient.Bound(bound, boundtype, value, thetaGran, hoelderGran);
				} catch (ThetaOutOfBoundException e) {
					e.printStackTrace();
				} catch (ParameterMismatchException e) {
					e.printStackTrace();
				} catch (ServerOverloadException e) {
					e.printStackTrace();
				}
				break;
			
			default:
				System.out.println("Optimization type not known.");
				break;
			}	
			break;
		
		default:
			System.out.println("Analysis type not known.");
			break;
		}
		
		//Resets the network
		Network.resetFLOW_ID(resetFlowID);
		Network.resetHOELDER_ID(resetHoelderID);
		Network.resetVERTEX_ID(resetVertexID);
		
		return probability;
	}
	
	/**
	 * This relays the command of calculating a numerical (to some extent optimized) 
	 * inverse bound to the corresponding {@link AbstractOptimizer}-subclass.
	 * @param flow the <code>Flow</code> of interest
	 * @param vertex the <code>Vertex</code> of interest
	 * @param thetaGran the step-size by which different values of theta are 
	 * allowed to differ.
	 * @param hoelderGran the step-size by which different values for hoelder 
	 * parameters are allowed to differ. 
	 * @param boundGran the step-size by which different values of bounds are 
	 * allowed to differ.
	 * @param analyzer the type of analysis used
	 * @param optimizer the type of optimization used
	 * @param boundtype the type of bound searched for
	 * @param probability the probability for which the best bound is searched
	 * @return the best value of delay or backlog found, which still suffices the 
	 * given probability.
	 */
	public double calculateInverseBound(Flow flow, Vertex vertex, double thetaGran, 
			double hoelderGran, double boundGran, SNC.AnalysisType analyzer, SNC.OptimizationType optimizer, 
			AbstractAnalysis.Boundtype boundtype, double probability){

		//Preparations
		double value = Double.NaN;
		
		HashMap<Integer, Vertex> givenVertices = new HashMap<Integer, Vertex>();
		for(Entry<Integer, Vertex> entry : vertices.entrySet()){
			givenVertices.put(entry.getKey(), entry.getValue().copy());
		}
		
		HashMap<Integer, Flow> givenFlows = new HashMap<Integer, Flow>();
		for(Entry<Integer, Flow> entry : flows.entrySet()){
			givenFlows.put(entry.getKey(), entry.getValue().copy());
		}
		
		int resetFlowID = Network.getFLOW_ID();
		int resetHoelderID = Network.getHOELDER_ID();
		int resetVertexID = Network.getVERTEX_ID();
		
		switch(analyzer){
		case SIMPLE_ANA:
			//Computes the bound in arrival representation
			SimpleAnalysis analysis = new SimpleAnalysis(givenVertices, givenFlows, flow.getFlow_ID(), vertex.getVertexID(), boundtype);
			Arrival bound = null;
			try {
				bound = analysis.analyze();
			} catch (ArrivalNotAvailableException e) {
				e.printStackTrace();
			} catch (DeadlockException e) {
				e.printStackTrace();
			} catch (BadInitializationException e) {
				e.printStackTrace();
			}
			switch(optimizer){
			case SIMPLE_OPT:
				SimpleOptimizer simple = new SimpleOptimizer(bound, boundtype);
				try {
					value = simple.ReverseBound(bound, boundtype, probability, thetaGran, hoelderGran);
				} catch (ThetaOutOfBoundException e) {
					e.printStackTrace();
				} catch (ParameterMismatchException e) {
					e.printStackTrace();
				} catch (ServerOverloadException e) {
					e.printStackTrace();
				}
				break;
			
			case GRADIENT_OPT:
				SimpleGradient gradient = new SimpleGradient(bound, boundtype);
				try {
					value = gradient.ReverseBound(bound, boundtype, probability, thetaGran, hoelderGran);
				} catch (ThetaOutOfBoundException e) {
					e.printStackTrace();
				} catch (ParameterMismatchException e) {
					e.printStackTrace();
				} catch (ServerOverloadException e) {
					e.printStackTrace();
				}
				break;
				
			default:
				System.out.println("Optimization type not known.");
				break;
			}
			break;
		
		default:
			System.out.println("Analysis type not known.");
			break;
		}
		
		//Resets the network
		Network.resetFLOW_ID(resetFlowID);
		Network.resetHOELDER_ID(resetHoelderID);
		Network.resetVERTEX_ID(resetVertexID);
		
		return value;
	}
	
	//Getter and Setter
	
	public HashMap<Integer, Flow> getFlows(){
		return flows;
	}

	public Flow getFlow(int id){
		return flows.get(id);
	}
	
	public HashMap<Integer, Vertex> getVertices() {
		return vertices;
	}
	
}
