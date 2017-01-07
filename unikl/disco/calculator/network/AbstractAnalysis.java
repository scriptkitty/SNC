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

import java.util.Map;

import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.BadInitializationException;

/**
 * This performs the analytical part of determining the sigma- and 
 * rho-parts of the flow of interest (FoI) and the service of 
 * interest (SoI).
 * The analysis is initialized with all information needed to 
 * compute leftover-service- and output-bounds until a
 * (sigma,rho)-representation of the FoI and SoI is known. After 
 * this a description of the performance-bound is calculated and
 * returned in a compact {@link Arrival}-representation. This class
 * does not complete the task of optimizing the parameters, which
 * appear in the performance-bound. 
 * If the network can not be resolved by successive computations of
 * leftover-service- and output-bounds, it is non-feed-forward and 
 * instead of a result a {@link DeadlockException} is thrown.
 * @author Michael Beck
 * @author Sebastian Henningsen
 */
public abstract class AbstractAnalysis implements Analyzer {
	
    /**
     *
     */
    protected Map<Integer, Vertex> vertices;

    /**
     *
     */
    protected Map<Integer, Flow> flows;
	
    /**
     *
     */
    protected int flow_of_interest;

    /**
     *
     */
    protected int vertex_of_interest;
        
    /**
     *
     */
    protected Network nw;
	
    /**
     *
     */
    public enum Boundtype{

            /**
             *
             */
            BACKLOG,

            /**
             *
             */
            DELAY,

            /**
             *
             */
            OUTPUT
	};

    /**
     *
     */
    protected Boundtype boundtype;
	
        
	// TODO
 
    /**
     *
     * @param nw
     * @param vertices
     * @param flows
     * @param flow_of_interest
     * @param vertex_of_interest
     * @param boundtype
     */
    	public AbstractAnalysis(Network nw, Map<Integer, Vertex> vertices, Map<Integer, Flow> flows, 
							int flow_of_interest, int vertex_of_interest, Boundtype boundtype){
		this.vertices = vertices;
		this.flows = flows;
		this.flow_of_interest = flow_of_interest; 
		this.vertex_of_interest = vertex_of_interest;
		this.boundtype = boundtype;
                this.nw = nw;
	}
	
    /**
     *
     * @return
     * @throws ArrivalNotAvailableException
     * @throws DeadlockException
     * @throws BadInitializationException
     */
    @Override
	public abstract Arrival analyze() throws ArrivalNotAvailableException, DeadlockException, BadInitializationException;

    /**
     *
     * @return
     */
    public int getVertexOfInterest() {
		return vertex_of_interest;
	}

    /**
     *
     * @return
     */
    public Map<Integer, Vertex> getVertices() {
		return vertices;
	}

    /**
     *
     * @return
     */
    public Map<Integer, Flow> getFlows() {
		return flows;
	}

    /**
     *
     * @return
     */
    public int getFlowOfInterest() {
		return flow_of_interest;
	}

    /**
     *
     * @return
     */
    public Boundtype getBoundtype() {
		return boundtype;
	}

}
