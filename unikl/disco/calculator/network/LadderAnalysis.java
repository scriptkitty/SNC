/*
 *  (c) 2017 Michael A. Beck, disco | Distributed Computer Systems Lab
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
import java.util.Stack;
import java.util.List;

import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.AdditiveComposition;
import unikl.disco.calculator.symbolic_math.SymbolicFunction;
import unikl.disco.calculator.symbolic_math.functions.BFunction;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.NewParameter;
import unikl.disco.calculator.symbolic_math.functions.scaledFunction;
import unikl.disco.calculator.symbolic_math.Service;
import unikl.disco.misc.SetUtils;

/**
 * This analysis gives an end-to-end performance bound for ladder
 * networks. A ladder network consists of the flow of interest and
 * two types of crossflows: The first type share the entire path of
 * the flow of interest. The second type shares exactly one node
 * with the flow of interest (in the following called "rung-flows").
 * For the analysis the rung-flows are subtracted from each node.
 * After that the convolution and subtraction of the remaining 
 * crossflows is performed in a single step. This is done by using
 * Theorem 3.1 in the PhD-thesis of Michael Beck, which is related
 * to the results established in the paper: "An End-to-End 
 * Probabilistic Network Calculus with Moment Generating Functions"
 * by Markus Fidler.
 * The analysis checks for each crossflow that is not a rung-flow
 * whether it passes through the preceeding and succeding node as
 * well. If it does not the given topology is not a ladder 
 * network. The arrivals for all rung-flows must be known for this
 * analysis to be performed.
 * @author Michael Beck
 * @author Sebastian Henningsen
 * @see AbstractAnalysis
 *
 */
public class LadderAnalysis extends AbstractAnalysis {
	
	//Members
	
	//Constructors
	
	/**
	 * Constructs the Analysis-Object, with all information needed
	 * to give the wished performance bound for the flow.
     * @param nw
	 * @param vertices the set of vertices in the network
	 * @param flows the set of flows in the network
	 * @param end_node the last vertex on the path for which an end
	 * to end bound is calculated. The starting node is always the 
	 * ingress node of the flow.
	 * @param flow_of_interest the flow for which the performance
	 * bound is calculated
	 * @param boundtype the type of bound, which should be 
	 * calculated. Note: All performance bounds are given in 
	 * {@Arrival}-representation.
	 */
	public LadderAnalysis(Network nw, Map<Integer, Vertex> vertices, Map<Integer, Flow> flows, int flow_of_interest, int end_node,  Boundtype boundtype){
		super(nw, vertices, flows, flow_of_interest, end_node, boundtype);
	}
	
	//Methods
	
	/**
	 * Analyzes the network in three steps: First the rung-flows
	 * are subtracted. Then the remaining crossflows are aggregated.
	 * Finally the end-to-end performance bound is calculated using a
	 * concatenation theorem.
	 * @return The bound in the {@link Arrival}-representation.
	 */
	@Override
	public Arrival analyze() {
		
		Arrival bound = new Arrival(nw);
		
		List<Integer> path = flows.get(flow_of_interest).getVerticeIDs();
		
		//First Step: Subtraction of rung-flows.
		//TODO:(Sebastian) Subtract all rung-flows from each node, if it has higher priority than the flow of interest.
		List<Service> list_of_leftover_services;
		for(int node : path) {
			for(int flowID : vertices.get(node).getAllFlowIDs()){
				if(flows.get(flowID).getPriorities() > flows.get(flow_of_interest).getPriorities()){
					//TODO:(Sebastian) Check for being a rung-flow: 1) Must have arrival known, 2) Successor and
					// Predecessor must not lie on flow of interest's path.
					//TODO:(Sebastian) Subtract the rung-flow: i.e. calculate leftover service
				}
			}
			//TODO:(Sebastian) Add the above calculated leftover services into a list of leftover services
			list_of_leftover_services.add(leftover_service);
		}
		
		//Second Step: Aggregation of higher priority through-flows.
		//TODO:(Sebastian) Aggregate through-flows.
		Arrival aggregated_through;
		for(int flow : list_hp_through_flows){
			//TODO:(Sebastian) Multiplex the arrivals with higher priority. Write corresponding sigma and rho into Arrival "through_flow".
		}
		
		//Third Step: Using the concatenation result.
		bound = calculateBound(flows.get(flow_of_interest).getInitialArrival(), list_of_leftover_services, aggregated_through);
		return bound;
	}
	
	/**
	 * Helper function for {@link analysis()}. It takes the FoI and
	 * above information to calculate end to end performance bounds. 
	 * Stochastic dependencies are taken into account and might 
	 * increase the number of Hoelder-coefficients.
	 * @param arrival the {@link Arrival}-description of the FoI
	 * @param service the {@link Service}-description of the SoI
	 * @return the bound in the {@link Arrival}-description
	 * @throws BadInitializationException
	 */
	
	//TODO:(Michael) Get Arrival representation for the delay- (and backlog-)bound. 
	private Arrival calculateBound(Arrival arrival, List<Service> leftover_services, Arrival aggregated_through) {
            
		Arrival result;
		
		//The result is dependent on the wished performance-bound
		switch(getBoundtype()){
		//TODO:(Michael) Update this to end-to-end
		case BACKLOG:
			
			SymbolicFunction preparation;
			
			//Dependent Case
			if(!SetUtils.getIntersection(arrival.getServicedependencies(),service.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service.getArrivaldependencies(), arrival.getArrivaldependencies()).isEmpty()){
				Hoelder hoelder = nw.createHoelder();
				preparation = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma(),hoelder), 
						new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho(),hoelder)));
			}
			
			//Independent Case
			else{
				preparation = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma()), 
												new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho())));	
			}
			
			//introduces the backlog-part in the backlog-bound as new variable. The sign must be negative!
			SymbolicFunction backlog_part = new NewParameter(nw.createHoelder());
			SymbolicFunction function = new AdditiveComposition(preparation, backlog_part);

			//In the vector of variables the backlog has Hoelder_ID equal to Network.HOELDER_ID-1.
			result = new Arrival(function,new ConstantFunction(0), nw);
			
			break;
		//TODO:(Michael) Update this to end-to-end
		case DELAY:
			
			SymbolicFunction sigma;
			SymbolicFunction rho;
			
			/* Debbugging:
			System.out.println("Service dependencies of AoI:"+arrival.getServicedependencies().toString());
			System.out.println("Service dependencies of SoI:"+service.getServicedependencies().toString());
			System.out.println("Arrival dependencies of AoI:"+arrival.getArrivaldependencies().toString());
			System.out.println("Arrival dependencies of SoI:"+service.getArrivaldependencies().toString());*/
			
			//Dependent Case
			if(!SetUtils.getIntersection(arrival.getServicedependencies(),service.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service.getArrivaldependencies(), arrival.getArrivaldependencies()).isEmpty()){
				Hoelder hoelder = nw.createHoelder();
				SymbolicFunction prep1 = new AdditiveComposition(arrival.getSigma(),service.getSigma(),hoelder);
				SymbolicFunction prep2 = new AdditiveComposition(arrival.getRho(),service.getRho(),hoelder);
				
				sigma = new AdditiveComposition(prep1,new BFunction(prep2));
				rho = new scaledFunction(service.getRho(), hoelder, false);
				System.out.println("Dependent case");
			}
			
			//Independent Case
			else{
				sigma = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma()),
						new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho())));
				rho = service.getRho();
				System.out.println("Independent Case");
			} 
			
			result = new Arrival(sigma, rho, nw);
			
			break;
		//TODO: (Michael) Update this to end-to-end (if possible)
		case OUTPUT:
		
			SymbolicFunction givensigma;
			SymbolicFunction givenrho;
			
			//Dependent Case
			if(!SetUtils.getIntersection(arrival.getServicedependencies(),service.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service.getArrivaldependencies(), arrival.getArrivaldependencies()).isEmpty()){
				Hoelder hoelder = nw.createHoelder();
				givensigma = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma(),hoelder),new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho(),hoelder)));
				givenrho = new scaledFunction(arrival.getRho(),hoelder, false);
			}
			
			//Independent Case
			else{
				givensigma = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma()),new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho())));
				givenrho = arrival.getRho();
			}
			
			result = new Arrival(givensigma, givenrho, nw);
			
			break;
		
		default:
			
			result = new Arrival(nw);
		}
		
		return result;
	}

}
