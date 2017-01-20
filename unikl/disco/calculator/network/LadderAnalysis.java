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
import java.util.Set;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.AdditiveComposition;
import unikl.disco.calculator.symbolic_math.SymbolicFunction;
import unikl.disco.calculator.symbolic_math.UnitaryMinus;
import unikl.disco.calculator.symbolic_math.functions.BFunction;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.NewParameter;
import unikl.disco.calculator.symbolic_math.functions.scaledFunction;
import unikl.disco.calculator.symbolic_math.Service;
import unikl.disco.misc.SetUtils;

/**
 * This analysis gives an end-to-end performance bound for ladder networks. A
 * ladder network consists of the flow of interest and two types of crossflows:
 * The first type share the entire path of the flow of interest. The second type
 * shares exactly one node with the flow of interest (in the following called
 * "rung-flows"). For the analysis the rung-flows are subtracted from each node.
 * After that the convolution and subtraction of the remaining crossflows is
 * performed in a single step. This is done by using Theorem 3.1 in the
 * PhD-thesis of Michael Beck, which is related to the results established in
 * the paper: "An End-to-End Probabilistic Network Calculus with Moment
 * Generating Functions" by Markus Fidler. The analysis checks for each
 * crossflow that is not a rung-flow whether it passes through the preceeding
 * and succeding node as well. If it does not the given topology is not a ladder
 * network. The arrivals for all rung-flows must be known for this analysis to
 * be performed.
 *
 * @author Michael Beck
 * @author Sebastian Henningsen
 * @see AbstractAnalysis
 *
 */
public class LadderAnalysis extends AbstractAnalysis {

    //Members
    //Constructors
    /**
     * Constructs the Analysis-Object, with all information needed to give the
     * wished performance bound for the flow.
     *
     * @param nw
     * @param vertices the set of vertices in the network
     * @param flows the set of flows in the network
     * @param end_node the last vertex on the path for which an end to end bound
     * is calculated. The starting node is always the ingress node of the flow.
     * @param flow_of_interest the flow for which the performance bound is
     * calculated
     * @param boundtype the type of bound, which should be calculated. Note: All
     * performance bounds are given in {
     * @Arrival}-representation.
     */
    public LadderAnalysis(Network nw, Map<Integer, Vertex> vertices, Map<Integer, Flow> flows, int flow_of_interest, int end_node, Boundtype boundtype) {
        super(nw, vertices, flows, flow_of_interest, end_node, boundtype);
    }

    //Methods
    public boolean isRungFlow(Flow flow) {
        // Check whether the flow is a rung flow and has the highest priority at the node
        boolean isRung = true;
        Flow foi = flows.get(flow_of_interest);
        List<Integer> foiRoute = foi.getVerticeIDs();
        List<Integer> route = flow.getVerticeIDs();
        int establishedVertex = flow.getCurrentVertexID();
        // First we check whether the xflow has an established arrival at the intersection with the foi
        // TODO: Use the java 8 streaming api.
        // Since we have a feed forward network there can be no duplicates on the path
        // -> the simple or-check is sufficient
        boolean initial = false;
        for (Integer v : foiRoute) {
            initial |= v == establishedVertex;
        }
        if (!initial) {
            System.out.println("No initial arrival");
            return false;
        }
        // Next we check whether the xflow and the foi intersect more than once
        List<Integer> intersections = getIntersectingNodes(flow, foi);
        if (intersections.size() > 1) {
            System.out.println("More than one intersection");
            return false;
        }
        // Next we check the priority of the crossflow. Due to assumption (2) we require
        // it to have the highest priority at the intersection
        Vertex intersectNode = vertices.get(intersections.get(0));
        isRung = intersectNode.getPrioritizedFlow() == flow.getID();
        return isRung;
    }

    public List<Integer> getIntersectingNodes(Flow rungFlow, Flow flowOfInterest) {
        List<Integer> intersections = new LinkedList<>();
        for (int foiVertex : flowOfInterest.getVerticeIDs()) {
            for (int v : rungFlow.getVerticeIDs()) {
                if (v == foiVertex) {
                    intersections.add(v);
                }
            }
        }
        return intersections;
    }
    
    public boolean hasCrossflow(int vertexID, List<Flow> rungFlows) {
        boolean hasCrossflow = false;
        for(Flow rf : rungFlows) {
            for (int v : rf.getVerticeIDs()) {
                hasCrossflow |= v == vertexID;
            }
        }
        return hasCrossflow;
    }
    
    public boolean isAggregateFlow(Flow flow) {
        // Check whether the flow has the same path as the flow of interest. TODO: Priority checking, initial arrival
        boolean isAggregate = true;
        List<Integer> route = flow.getVerticeIDs();
        Flow foi = flows.get(flow_of_interest);
        List<Integer> foiRoute = foi.getVerticeIDs();
        // Routes must have the same length
        if (route.size() != foiRoute.size()) {
            isAggregate = false;
            return isAggregate;
        }
        for (int i = 0; i < foiRoute.size(); i++) {
            isAggregate &= route.get(i).intValue() == foiRoute.get(i).intValue();
        }
        return isAggregate;
    }

    /**
     * Analyzes the network in three steps: First the rung-flows are subtracted.
     * Then the remaining crossflows are aggregated. Finally the end-to-end
     * performance bound is calculated using a concatenation theorem.
     *
     * @return The bound in the {@link Arrival}-representation.
     * @throws unikl.disco.calculator.network.ArrivalNotAvailableException
     */
    @Override
    public Arrival analyze() throws ArrivalNotAvailableException {
        // We make three assumptions: 
        // (1) The relative priorities of the aggregate flows stay the same
        // (2) Crossflows always have the highest priority at the intersection with the aggregate flows
        // (3) The initial arrival of crossflows at the intersection with the flow of interest is known.
        // First: Test whether the network is a ladder network.
        List<Flow> rungFlows = new LinkedList<>();
        List<Flow> aggregateFlows = new LinkedList<>();

        for (Flow f : flows.values()) {
            if (f.getID() == flow_of_interest) {
                // Ignore the flow of interest
            } else if (isRungFlow(f)) {
                rungFlows.add(f);
                System.out.println("Flow " + f.getAlias() + " is a rung Flow.");
            } else if (isAggregateFlow(f)) {
                aggregateFlows.add(f);
                System.out.println("Flow " + f.getAlias() + " is an aggregate Flow.");
            } else {
                System.out.println("Flow " + f.getAlias() + " does not fit!.");
                throw new IllegalArgumentException("Network is not in ladder format.");
            }
        }

        // Now we successfully checked for the validity of the network.
        // First step of the analysis: Subtract all the rung flows
        // and gather the leftover services in a list for later use
        List<Service> leftoverServices = new LinkedList<>();
        Flow foi = flows.get(flow_of_interest);
        List<Integer> foiRoute = foi.getVerticeIDs();
        for (int node : foiRoute) {
            if(hasCrossflow(node, rungFlows)) {
                System.out.println("Subtracting xf at node " + vertices.get(node).getAlias());
                vertices.get(node).serve();
                leftoverServices.add(vertices.get(node).getService());
            }
            else {
            	System.out.println("Nothing to subtract at node "+ vertices.get(node).getAlias());
            	leftoverServices.add(vertices.get(node).getService());
            }
        }

        // Second step of the analysis: Aggregation of higher priority through-flows.
        // Therefore we multiplex the initial arrivals of all aggregate flows and remove them from the network
        // The resulting arrival is stored in 
        Arrival aggregatedThrough = new Arrival(nw);
        if (aggregateFlows.size() == 0 ){
        	//do nothing
        }
        else if (aggregateFlows.size() <= 1) {
            aggregatedThrough = aggregateFlows.get(0).getInitialArrival();
            nw.removeFlow(aggregateFlows.get(0));
        } else {
            Flow f1 = aggregateFlows.get(0);
            Flow f2 = aggregateFlows.get(1);
            aggregatedThrough = aggregatedThrough.multiplex(f1.getInitialArrival(), f2.getInitialArrival());
            nw.removeFlow(f1);
            nw.removeFlow(f2);
            for (int i = 2; i < aggregateFlows.size(); i++) {
                f1 = aggregateFlows.get(i);
                aggregatedThrough = aggregatedThrough.multiplex(aggregatedThrough, f1.getInitialArrival());
                nw.removeFlow(f1);
            }
        }
        System.out.println("Aggregate Arrival: " + aggregatedThrough);
        //Third Step: Using the concatenation result.
        Arrival bound = null;
        bound = calculateBound(flows.get(flow_of_interest).getInitialArrival(), leftoverServices, aggregatedThrough);
        return bound;
    }

    /**
     * Helper function for {@link analysis()}. It takes the FoI and above
     * information to calculate end to end performance bounds. Stochastic
     * dependencies are taken into account and might increase the number of
     * Hoelder-coefficients.
     *
     * @param arrival the {@link Arrival}-description of the FoI
     * @param service the {@link Service}-description of the SoI
     * @return the bound in the {@link Arrival}-description
     * @throws BadInitializationException
     */
    //TODO:(Michael) Get Arrival representation for the delay- (backlog-, and output-)bound. 
    private Arrival calculateBound(Arrival arrival, List<Service> leftover_services, Arrival aggregated_through) {

        Arrival result;

        //The result is dependent on the wished performance-bound
        switch (getBoundtype()) {
            //TODO:(Michael) Update this to end-to-end
            case BACKLOG:
                System.out.println("Ladder Analysis for output-bound not implemented, yet.");
                result = new Arrival(nw);
                break;

            case DELAY:

                //TODO: This dependent case needs thorough testing! Not recommended to use before that.
                //the sigma and rho of the bound:
                /*SymbolicFunction sigma;
			SymbolicFunction rho;
			
			//Intermediate sigmas and rhos used to construct above
			SymbolicFunction sigma_A;
			
			SymbolicFunction rho_agg = aggregated_through.getRho();
			SymbolicFunction sigma_agg = aggregated_through.getSigma();
			
			
			//Preparations to process dependencies
			List<Set<Integer>> agg_and_lo_arrival = new ArrayList<Set<Integer>>();
			agg_and_lo_arrival.add(aggregated_through.getArrivaldependencies());		
			List<Set<Integer>> agg_and_lo_service = new ArrayList<Set<Integer>>();
			agg_and_lo_service.add(aggregated_through.getServicedependencies());
			
			List<Set<Integer>> lo_arrival = new ArrayList<Set<Integer>>();
			List<Set<Integer>> lo_service = new ArrayList<Set<Integer>>();
			List<Set<Integer>> reduced_lo_arrival = new ArrayList<Set<Integer>>();
			List<Set<Integer>> reduced_lo_service = new ArrayList<Set<Integer>>();
			
			for(Service leftover_service : leftover_services){
				agg_and_lo_arrival.add(leftover_service.getArrivaldependencies());
				agg_and_lo_service.add(leftover_service.getServicedependencies());
				
				lo_arrival.add(leftover_service.getArrivaldependencies());
				lo_service.add(leftover_service.getServicedependencies());
				reduced_lo_arrival.add(leftover_service.getArrivaldependencies());
				reduced_lo_service.add(leftover_service.getServicedependencies());
			}
			//Processing of dependencies: Hoelder parameters are introduced for each dependency encountered.
			//Splitting off the flow of interest from the calculations
			if(!SetUtils.getIntersection(arrival.getArrivaldependencies(),SetUtils.getUnion(agg_and_lo_arrival)).isEmpty() 
					|| !SetUtils.getIntersection(arrival.getServicedependencies(),SetUtils.getUnion(agg_and_lo_service)).isEmpty())
					{
				Hoelder hoelder = nw.createHoelder();
				rho = new UnitaryMinus(new scaledFunction(arrival.getRho(),hoelder,true));
				sigma_A = new scaledFunction(arrival.getSigma(),hoelder,true);
				
				rho_agg = new scaledFunction(aggregated_through.getRho(),hoelder,false);
				sigma_agg = new scaledFunction(aggregated_through.getSigma(),hoelder,false);
				for(Service leftover_service : leftover_services){
					leftover_service.setRho(new scaledFunction(leftover_service.getRho(),hoelder,false));
					leftover_service.setSigma(new scaledFunction(leftover_service.getSigma(),hoelder,false));
				}
			}
			else{
				rho = new UnitaryMinus(arrival.getRho());
				sigma_A = arrival.getSigma();
			}
			//Splitting off the aggregated through-flow from the calculations
			if(!SetUtils.getIntersection(aggregated_through.getArrivaldependencies(), SetUtils.getUnion(lo_arrival)).isEmpty()
					|| !SetUtils.getIntersection(aggregated_through.getArrivaldependencies(), SetUtils.getUnion(lo_service)).isEmpty()){
				Hoelder hoelder = nw.createHoelder();
				rho_agg = new scaledFunction(rho_agg,hoelder,true);
				sigma_agg = new scaledFunction(sigma_agg,hoelder,true);
				
				for(Service leftover_service : leftover_services){
					leftover_service.setRho(new scaledFunction(leftover_service.getRho(),hoelder,false));
					leftover_service.setSigma(new scaledFunction(leftover_service.getSigma(),hoelder,false));
				}
			}
			//Splitting off each leftover service from the calculations
			List<Service> sublist_leftover_services = leftover_services.subList(1, leftover_services.size()); 
			for(Service leftover_service : leftover_services){
				reduced_lo_arrival.remove(leftover_service.getArrivaldependencies());
				reduced_lo_service.remove(leftover_service.getServicedependencies());
				if(!SetUtils.getIntersection(leftover_service.getArrivaldependencies(), SetUtils.getUnion(reduced_lo_arrival)).isEmpty()
					||	!SetUtils.getIntersection(leftover_service.getServicedependencies(), SetUtils.getUnion(reduced_lo_service)).isEmpty()){
					Hoelder hoelder = nw.createHoelder();
					leftover_service.setRho(new scaledFunction(leftover_service.getRho(),hoelder,true));
					leftover_service.setSigma(new scaledFunction(leftover_service.getSigma(),hoelder,true));
					for(Service other_service : sublist_leftover_services){
						other_service.setRho(new scaledFunction(other_service.getRho(),hoelder,false));
						other_service.setSigma(new scaledFunction(other_service.getSigma(),hoelder,false));
					}
					sublist_leftover_services = sublist_leftover_services.subList(1,sublist_leftover_services.size());
				}
			}*/
                //INDEPENDENT CASE
                SymbolicFunction rho = new UnitaryMinus(arrival.getRho());
                SymbolicFunction sigma = arrival.getSigma();
                SymbolicFunction rho_through_total = arrival.getRho();
                if(aggregated_through != null){
                	sigma = new AdditiveComposition(arrival.getSigma(), aggregated_through.getSigma());
                	rho_through_total = new AdditiveComposition(arrival.getRho(), aggregated_through.getRho());
                }
                
                for (Service leftover_service : leftover_services) {
                    sigma = new AdditiveComposition(sigma, leftover_service.getSigma());
                    sigma = new AdditiveComposition(sigma, new BFunction(new AdditiveComposition(leftover_service.getRho(), rho_through_total)));
                }

                System.out.println("Ladder Analysis (Independent Case):");
                //Debugging
                System.out.println("Delay-Form Sigma: "+sigma.toString());
                System.out.println("Delay-Form Rho: "+rho.toString());

                result = new Arrival(sigma, rho, nw);

                break;
            //TODO: (Michael) Update this to end-to-end (if possible)
            case OUTPUT:
                System.out.println("Ladder Analysis for output-bound not implemented, yet.");
                result = new Arrival(nw);
                break;

            default:

                result = new Arrival(nw);
        }

        return result;
    }

}
