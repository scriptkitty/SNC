package unikl.disco.mgf;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import unikl.disco.mgf.gui.GUI;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.AnalysisFactory;
import unikl.disco.mgf.network.ArrivalNotAvailableException;
import unikl.disco.mgf.network.DeadlockException;
import unikl.disco.mgf.network.Flow;
import unikl.disco.mgf.network.Network;
import unikl.disco.mgf.network.Vertex;
import unikl.disco.mgf.network.AnalysisType;
import unikl.disco.mgf.network.Analyzer;
import unikl.disco.mgf.optimization.AbstractOptimizer;
import unikl.disco.mgf.optimization.BoundFactory;
import unikl.disco.mgf.optimization.BoundType;
import unikl.disco.mgf.optimization.Optimizable;
import unikl.disco.mgf.optimization.OptimizationFactory;
import unikl.disco.mgf.optimization.OptimizationType;
import unikl.disco.mgf.optimization.Optimizer;
import unikl.disco.misc.UndoRedoStack;
import unikl.disco.misc.commands.Command;

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

        // TODO
	private static GUI gui;
        private static Network nw;
        private static UndoRedoStack undoRedoStack;
	
	//Main-Method
	public static void main(String[] args) throws InvocationTargetException, InterruptedException, 
	ArrivalNotAvailableException, BadInitializationException, DeadlockException, ThetaOutOfBoundException, 
	ParameterMismatchException, ServerOverloadException{
		
                // TODO
		SNC snc = new SNC();
		nw = new Network();
		undoRedoStack = new UndoRedoStack();
                
		gui = new GUI(snc);
		SwingUtilities.invokeLater(gui);
		
	}
		
	//Methods
	public void undo() {
            undoRedoStack.undo();
        }
        
        public void redo() {
            undoRedoStack.redo();
        }
        
        public void invokeCommand(Command c) {
            undoRedoStack.insertIntoStack(c);
            c.execute();
        }
        
        public Network getCurrentNetwork() {
            return nw;
        }
	
        public void saveNetwork(File file) {
            getCurrentNetwork().save(file);
        }
        
        public void loadNetwork(File file) {
            nw = Network.load(file);
        }
	/**
	 * Relays the command of removing a given <code>flow</code> from the
	 * network to the {@link Network}-class.  
	 * @param flow the <code>Flow</code> being removed.
	 * @return Returns <code>true</code> if the flow has been successfully
	 * removed and <code>false</code> otherwise.
	 */
	public boolean removeFlow(Flow flow, Network nw) {
		return nw.removeFlow(flow);
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
	public int addFlow(Flow flow, Network nw) throws ArrivalNotAvailableException {
	
            nw.addFlow(flow.getInitialArrival(), flow.getVerticeIDs(), 
					flow.getPriorities(), flow.getAlias());
            return nw.getFLOW_ID()-1;
	}
	
	/**
	 * Relays the command of removing a given <code>vertex</code> from the
	 * network to the {@link Network}-class.  
	 * @param vertex the <code>Vertex</code> being removed.
	 * @return Returns <code>true</code> if the vertex has been successfully
	 * removed and <code>false</code> otherwise.
	 */
	public boolean removeVertex(Vertex vertex, Network nw) {
		return nw.removeVertex(vertex);
	}
        
        public boolean removeVertex(int vertexID, int networkID) {
            return getNetwork(networkID).removeVertex(vertexID);
        }
        
        public Network getNetwork(int id) {
            return nw;
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
	public int addVertex(Vertex vertex, Network nw){
            nw.addVertex(vertex.getService(), vertex.getAlias());
            return nw.getVERTEX_ID()-1;
	}
	
	/**
	 * This relays the command of calculating a symbolic (not optimized) 
	 * bound to the corresponding {@link AbstractAnalysis}-subclass. The 
	 * result is returned in arrival-representation.
	 * @param flow the <code>Flow</code> of interest.
	 * @param vertex the <code>Vertex</code> of interest.
	 * @param anaType the type of analysis used
	 * @param boundtype the type of bound, which needs to be computed.
	 * @return the result of the analysis in arrival-representation.
	 */
	public Arrival analyzeNetwork(Flow flow, Vertex vertex, AnalysisType anaType, AbstractAnalysis.Boundtype boundtype, Network nw){
		
		//Preparations
		Arrival bound = null;
		
		HashMap<Integer, Vertex> givenVertices = new HashMap<Integer, Vertex>();
		for(Entry<Integer, Vertex> entry : nw.getVertices().entrySet()){
			givenVertices.put(entry.getKey(), entry.getValue().copy());
		}
		
		HashMap<Integer, Flow> givenFlows = new HashMap<Integer, Flow>();
		for(Entry<Integer, Flow> entry : nw.getFlows().entrySet()){
			givenFlows.put(entry.getKey(), entry.getValue().copy());
		}
		
		int resetFlowID = nw.getFLOW_ID();
		int resetHoelderID = nw.getHOELDER_ID();
		int resetVertexID = nw.getVERTEX_ID();
		
		Analyzer analyzer = AnalysisFactory.getAnalyzer(anaType, nw, givenVertices, givenFlows, flow.getFlow_ID(), vertex.getVertexID(), boundtype);
                try {
                        bound = analyzer.analyze();
                } catch (ArrivalNotAvailableException e) {
                        e.printStackTrace();
                } catch (DeadlockException e) {
                        e.printStackTrace();
                } catch (BadInitializationException e) {
                        e.printStackTrace();
                }
		
		//Resets the network
		nw.resetFLOW_ID(resetFlowID);
		nw.resetHOELDER_ID(resetHoelderID);
		nw.resetVERTEX_ID(resetVertexID);
		
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
	 * @param anaType the type of analysis used
	 * @param optType the type of optimization used
	 * @param boundtype the type of bound searched for
	 * @param value the value of the delay or backlog bound
	 * @return the best probability found for the given delay or backlog bound
	 */
	public double calculateBound(Flow flow, Vertex vertex, double thetaGran, 
			double hoelderGran, AnalysisType anaType, OptimizationType optType, AbstractAnalysis.Boundtype boundtype, double value, Network nw){

		//Preparations
		//Backlog values are represented by negative values in the arrival representation
		if(boundtype == AbstractAnalysis.Boundtype.BACKLOG && value > 0) value = -value;
		
		double debugProb = 1;
                double prob = 1;
		
                Arrival symbolicBound = analyzeNetwork(flow, vertex, anaType, boundtype, nw);
                // Temporary fix:
                BoundType optBoundType;
                if(boundtype == AbstractAnalysis.Boundtype.BACKLOG) {
                    optBoundType = BoundType.BACKLOG;
                } else if(boundtype == AbstractAnalysis.Boundtype.DELAY) {
                    optBoundType = BoundType.DELAY;
                } else {
                    throw new IllegalArgumentException("No such boundtype");
                }
                Optimizable bound = BoundFactory.createBound(symbolicBound, optBoundType, value);
                Optimizer optimizer = OptimizationFactory.getOptimizer(nw, bound, boundtype, optType);
                try {
                    debugProb = optimizer.Bound(symbolicBound, boundtype, value, thetaGran, hoelderGran);
                    prob = optimizer.minimize(thetaGran, hoelderGran);
                } catch (    ThetaOutOfBoundException | ParameterMismatchException | ServerOverloadException e) {
                    e.printStackTrace();
                }
		
		// For debugging purposes
                if(prob != debugProb) {
                    throw new RuntimeException("[DEBUG] Optimization results do not match!");
                }
		return prob;
	}
        
        /**
         * Computes the End-to-End Delay bound for the given flow from vertex1 to vertex2. Temporary solution.
         * @param flow
         * @param vertex1
         * @param vertex2
         * @param thetaGran
         * @param hoelderGran
         * @param analyzer
         * @param optimizer
         * @param value
         * @return the probability, that the E2E exceeds the parameter "value"
         */
        public double calculateE2EBound(Flow flow, Vertex vertex1, Vertex vertex2, double thetaGran, double hoelderGran, AnalysisType analyzer, OptimizationType optimizer, double value, Network nw) {
            double probability = 0;
            
            // For every node in between vertex1 and vertex2 along the flow, compute the bound and return the sum
            // TODO: Introduce Error Handling
            List<Integer> vlist = flow.getVerticeIDs();
            int pos1 = vlist.indexOf(vertex1.getVertexID());
            int pos2 = vlist.indexOf(vertex2.getVertexID());
            System.out.println("E2E Pos Beginning - End:" + pos1 + " - " + pos2);
            if(pos1 > pos2) {
                int tmp = pos1;
                pos1 = pos2;
                pos2 = tmp;
            }
            vlist = vlist.subList(pos1, pos2 > vlist.size() ? vlist.size() : pos2 + 1);
            
            int len = vlist.size();
            for (Integer vid : vlist) {
                probability += calculateBound(flow, nw.getVertices().get(vid), thetaGran, hoelderGran, analyzer, optimizer, AbstractAnalysis.Boundtype.DELAY, value/len, nw);
            }
            return probability;
        }
        
        /**
         * Computes the End-To-End Reverse Delay bound, i.e. a concrete value for the delay.
         * CAUTION: At the moment the returned value may not be correct!
         */
        
        public double calculateInverseE2EBound(Flow flow, Vertex vertex1, Vertex vertex2, double thetaGran, double hoelderGran, double boundGran, AnalysisType analyzer, OptimizationType optimizer, double probability, Network nw) {
            double value = 0;
            // For every node in between vertex1 and vertex2 along the flow, compute the bound and return the sum
            // TODO: Introduce Error Handling
            List<Integer> vlist = flow.getVerticeIDs();
            int pos1 = vlist.indexOf(vertex1.getVertexID());
            int pos2 = vlist.indexOf(vertex2.getVertexID());
            System.out.println("InverseE2E Pos Beginning - End:" + pos1 + " - " + pos2);
            if(pos1 > pos2) {
                int tmp = pos1;
                pos1 = pos2;
                pos2 = tmp;
            }
            vlist = vlist.subList(pos1, pos2 > vlist.size() ? vlist.size() : pos2 + 1);
            
            int len = vlist.size();
            for (Integer vid : vlist) {
                value += calculateInverseBound(flow, nw.getVertices().get(vid), thetaGran, hoelderGran, boundGran, analyzer, optimizer, AbstractAnalysis.Boundtype.BACKLOG, probability/len, nw);
            }
            return value;
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
	 * @param anaType the type of analysis used
	 * @param optType the type of optimization used
	 * @param boundtype the type of bound searched for
	 * @param probability the probability for which the best bound is searched
	 * @return the best value of delay or backlog found, which still suffices the 
	 * given probability.
	 */
	public double calculateInverseBound(Flow flow, Vertex vertex, double thetaGran, 
			double hoelderGran, double boundGran, AnalysisType anaType, OptimizationType optType, 
			AbstractAnalysis.Boundtype boundtype, double probability, Network nw){

		//Preparations
		double value = Double.NaN;
                double debugVal = Double.NaN;
		
		Arrival symbolicBound = analyzeNetwork(flow, vertex, anaType, boundtype, nw);
                
                // Temporary fix:
                BoundType optBoundType;
                if(boundtype == AbstractAnalysis.Boundtype.BACKLOG) {
                    optBoundType = BoundType.INVERSE_BACKLOG;
                } else if(boundtype == AbstractAnalysis.Boundtype.DELAY) {
                    optBoundType = BoundType.INVERSE_DELAY;
                } else {
                    throw new IllegalArgumentException("No such boundtype");
                }
                
                Optimizable bound = BoundFactory.createBound(symbolicBound, optBoundType, probability);
                Optimizer optimizer = OptimizationFactory.getOptimizer(nw, bound, boundtype, optType);
                try {
                    value = optimizer.minimize(thetaGran, hoelderGran);
                    debugVal = optimizer.ReverseBound(symbolicBound, boundtype, probability, thetaGran, hoelderGran);
                } catch (    ThetaOutOfBoundException | ParameterMismatchException | ServerOverloadException e) {
                    e.printStackTrace();
                }
                // For debuggin purposes
		if(value != debugVal) {
                    throw new RuntimeException("[DEBUG] Optimization results do not match!");
                }
		return value;
	}
	
	//Getter and Setter
	public Map<Integer, Flow> getFlows(Network nw) {
            return nw.getFlows();
        }
        
        public Map<Integer, Vertex> getVertices(Network nw) {
            return nw.getVertices();
        }
	public Map<Integer, Flow> getFlows(){
		return getFlows(getCurrentNetwork());
	}
        
	public Flow getFlow(int id) {
		return getCurrentNetwork().getFlow(id);
	}
	
	public Map<Integer, Vertex> getVertices() {
		return getVertices(getCurrentNetwork());
	}
        
        public Vertex getVertex(int id) {
            return getCurrentNetwork().getVertex(id);
        }
}
