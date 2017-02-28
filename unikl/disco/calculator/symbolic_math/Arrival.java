/*
 *  (c) 2017 Michael A. Beck, Sebastian Henningsen
 *  		disco | Distributed Computer Systems Lab
 *  		University of Kaiserslautern, Germany
 *  All Rights Reserved.
 *
 * This software is work in progress and is released in the hope that it will
 * be useful to the scientific community. It is provided "as is" without
 * express or implied warranty, including but not limited to the correctness
 * of the code or its suitability for any particular purpose.
 *
 * This software is provided under the MIT License, however, we would 
 * appreciate it if you contacted the respective authors prior to commercial use.
 *
 * If you find our software useful, we would appreciate if you mentioned it
 * in any publication arising from the use of this software or acknowledge
 * our work otherwise. We would also like to hear of any fixes or useful
 */
package unikl.disco.calculator.symbolic_math;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import unikl.disco.calculator.symbolic_math.functions.BFunction;
import unikl.disco.calculator.symbolic_math.functions.scaledFunction;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;

import unikl.disco.calculator.network.Network;
import unikl.disco.misc.SetUtils;

/**
 * Class representing the stochastic bound of an arrival. The 
 * arrival is bounded by its moment generating function (MGF).
 * Every MGF-Bound consists of three parameters, sigma, rho and 
 * theta, where sigma and rho are dependent on theta. Hence sigma
 * and rho are represented by {@link SymbolicFunction} objects. 
 * Usually there is a maximal possible value for theta, which is 
 * given in <code>thetastar</code>. The  functions <code>rho</code> 
 * and <code>sigma</code> are only the variables inside the 
 * (sigma,rho)-notation. For calculating the MGF-bound they need 
 * to be exponentiated and multiplied by theta.
 * Further every arrival may be stochastically dependent on a 
 * number of other arrival- or service-processes. These
 * dependencies are enlisted and differentiated in 
 * <code>Arrivaldependencies</code> and 
 * <code>Servicedependencies</code>.
 * 
 * Further this class can be used for a compact representation of
 * backlog- and delay-bounds. For this usage see {@link Analysis}.
 * @author Michael Beck
 * @see Analysis
 * @see SymbolicFunction
 */

public class Arrival implements Serializable {
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1079479343537123673L;
	private SymbolicFunction rho;
	private SymbolicFunction sigma;
	private Set<Integer> Arrivaldependencies;
	private Set<Integer> Servicedependencies;
        private Network nw; // TODO: Maybe exchange for Node/Flow
	
	//Constructors
	
	/**
	 * Creates an <code>Arrival</code> instance, with 
	 * <code>rho</code> and <code>sigma</code> being 
	 * {@link ZeroFunction}s.
     * @param nw
	 */
	public Arrival(Network nw){ 
		rho = new ConstantFunction(0);
		sigma = new ConstantFunction(0);
		Arrivaldependencies = new HashSet<>();
		Servicedependencies = new HashSet<>();
                this.nw = nw;
	}

	/**
	 * Creates an <code>Arrival</code> instance with functions 
	 * <code>rho</code> and <code>sigma</code>. 
	 * <code>thetastar</code> is deduced from the two functions.
	 * @param rho the time dependent part of the MGF-bound.
	 * @param sigma the time independent part of the MGF-bound.
     * @param nw
	 * @see SymbolicFunction
	 */
	public Arrival(SymbolicFunction sigma, SymbolicFunction rho, Network nw) {
		this.rho = rho;
		this.sigma = sigma;
		Arrivaldependencies = new HashSet<Integer>();
		Servicedependencies = new HashSet<Integer>();
                this.nw = nw;
	}
	
	/**
	 * Creates an <code>Arrival</code> instance with functions 
	 * <code>rho</code> and <code>sigma</code>.
	 * <code>thetastar</code> is deduced from the two functions.
	 * Further the arrival is dependent on the given <code>flow_id
	 * </code>. The purpose of this constructor is to easily 
	 * associate the arrival as initial arrival in a <code>Flow
	 * </code>, which is dependent on itself.
	 * @param rho the time dependent part of the MGF-bound.
	 * @param sigma the time independent part of the MGF-bound.
	 * @param flow_id id of the flow, which has this arrival as
	 * initial arrival
     * @param nw
	 * @see SymbolicFunction
	 * @see Flow
	 */
	public Arrival(SymbolicFunction sigma, SymbolicFunction rho, int flow_id, Network nw) {
		this.rho = rho;
		this.sigma = sigma;
		Arrivaldependencies = new HashSet<Integer>();
		Arrivaldependencies.add(flow_id);
		Servicedependencies = new HashSet<Integer>();
                this.nw = nw;
	}
	
	//Methods
	
	/**
	 * Adds a stochastic dependency between this instance and some 
	 * {@link Flow}.
	 * @param flow_id the flow-id, which is stochastically dependent
	 * of the instance.
	 */
	public void addArrivalDependency(int flow_id){
		Arrivaldependencies.add(flow_id);
	}
	
	/**
	 * Adds multiple stochastically dependent {@link Flow}s to the
	 * set of stochastically dependent arrivals.
	 * @param flow_ids the set of flow-ids, which are stochastically
	 * dependent of the instance.
	 */
	public void addArrivalDependency(Set<Integer> flow_ids){
		Arrivaldependencies.addAll(flow_ids);
	}
	
	/**
	 * Adds a stochastic dependency between the instance and some 
	 * {@link Vertex}.
	 * @param vertex_id the vertex-id, which is stochastically 
	 * dependent of the instance.
	 */
	public void addServiceDependency(int vertex_id){
		Servicedependencies.add(vertex_id);
	}
	
	/**
	 * Adds multiple stochastically dependent {@link Vertex} to 
	 * the set of stochastically dependent vertices.
	 * @param vertex_ids the set of vertex-ids processes, which are 
	 * stochastically dependent of the instance.
	 */
	public void addServiceDependency(Set<Integer> vertex_ids){
		Servicedependencies.addAll(vertex_ids);
	}
	
	/**
	 * The return is the evaluation of the MGF-bound at the points 
	 * <code>sigmaparameters</code> and <code>rhoparameters</code>,
	 * within the time interval (m,n].
     * @param theta
	 * @param sigmaparameters the point at which the MGF is
	 * evaluated. It has to match the number of parameters needed 
	 * to calculate sigma. The first parameter needs to be theta.
	 * @param rhoparameters the point at which the MGF is evaluated. 
	 * It has to match the number of parameters needed to calculate 
	 * rho. The first parameter needs to be theta.
	 * @param n the end of the time interval (m,n]
	 * @param m the beginning of the time interval (m,n]
	 * @return the value of the MGF at the given point
     * @throws unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException
     * @throws unikl.disco.calculator.symbolic_math.ParameterMismatchException
	 * @throws ServerOverloadException 
	 */
	public double evaluate(double theta, Map<Integer, Hoelder> sigmaparameters, Map<Integer, Hoelder> rhoparameters, int n, int m)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		
		double value;
		
		try{
			value = Math.exp(theta*sigma.getValue(theta, sigmaparameters) + theta*rho.getValue(theta, rhoparameters)*(n-m));
		}
		catch (ParameterMismatchException e){
			value = Double.NaN;
			System.out.println("Parameter Mismatch Error: "+e.getMessage());
			System.out.println("Possible reasons: The network is not stable, " +
					"\ni.e. at least one node has not enough capacity to serve its arrivals.");
		}
		return value;
	}
	
    /**
     *
     * @param theta
     * @param n
     * @param m
     * @return
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     */
    public double evaluate(double theta, int n, int m) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException{
		double value = evaluate(theta, sigma.getParameters(), rho.getParameters(), n, m);
		return value;
	}
	
	/**
	 * Returns a flow {@link arrrival}, which is the result
	 * of multiplexing <code>arrival1</code> and <code>arrival2
	 * </code>. Stochastic dependencies are taken into account.
	 * In the stochastic dependent case this leads to the 
	 * introduction of a new Hoelder-coefficient.
	 * @param arrival1 the first flow being multiplexed
	 * @param arrival2 the second flow being multiplexed 
	 * @return the multiplexed arrival
	 */
	public Arrival multiplex(Arrival arrival1, Arrival arrival2){
		
		Arrival arrival;
		
		//Dependent case
		if(!SetUtils.getIntersection(arrival1.getServicedependencies(),arrival2.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(arrival1.getArrivaldependencies(), arrival2.getArrivaldependencies()).isEmpty()){
			Hoelder hoelder = nw.createHoelder();
			SymbolicFunction givensigma = new AdditiveComposition(arrival1.getSigma(),arrival2.getSigma(),hoelder);
			SymbolicFunction givenrho = new AdditiveComposition(arrival1.getRho(), arrival2.getRho(), hoelder);
			arrival = new Arrival(givensigma, givenrho, nw);
		}
		
		//Independent case
		else{
			SymbolicFunction givensigma = new AdditiveComposition(arrival1.getSigma(),arrival2.getSigma());
			SymbolicFunction givenrho = new AdditiveComposition(arrival1.getRho(), arrival2.getRho());
			arrival = new Arrival(givensigma, givenrho, nw);
		}
		
		//Keeps track of stochastic dependencies
		arrival.addArrivalDependency(arrival1.getArrivaldependencies());
		arrival.addArrivalDependency(arrival2.getArrivaldependencies());
		arrival.addServiceDependency(arrival2.getServicedependencies());
		arrival.addServiceDependency(arrival1.getServicedependencies());
		
		return arrival;
	}
	
	/**
	 * Returns the <code>output</code>, resulting from a server
	 * with some {@link service}, working on an {@link Arrival}.
	 * Stochastic dependencies are taken into account.
	 * In the stochastic dependent case this leads to the 
	 * introduction of a new Hoelder-coefficient.
	 * @param arrival the arrival being served
	 * @param service the service of the service element
	 * @return the output of the service element
	 */
	public Arrival output(Arrival arrival, Service service){
		
		Arrival output;

		//Dependent case
		if(!SetUtils.getIntersection(arrival.getServicedependencies(),service.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service.getArrivaldependencies(), arrival.getArrivaldependencies()).isEmpty()){
			Hoelder hoelder = nw.createHoelder();
			SymbolicFunction givensigma = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma(),hoelder),new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho(),hoelder)));
			SymbolicFunction givenrho = new scaledFunction(arrival.getRho(),hoelder, false);
			output = new Arrival(givensigma, givenrho, nw);
			//System.out.println("Dependent Case Output calculated");
		}
		
		//Independent case
		else{
			SymbolicFunction givensigma = new AdditiveComposition(new AdditiveComposition(arrival.getSigma(),service.getSigma()),new BFunction(new AdditiveComposition(arrival.getRho(),service.getRho())));
			SymbolicFunction givenrho = arrival.getRho();
			output = new Arrival(givensigma, givenrho, nw);
			//System.out.println("Independent Case Output calculated");
		}
		
		//Keeps track of stochastic dependencies
		output.addArrivalDependency(arrival.getArrivaldependencies());
		output.addArrivalDependency(service.getArrivaldependencies());
		output.addServiceDependency(service.getServicedependencies());
		output.addServiceDependency(arrival.getServicedependencies());
		
		return output;
	}
	
	/**
	 * Returns a String representation of the arrival in MGF-
	 * Notation. This means, if the MGF of an arrival in an
	 * intervall (m,n] is bounded by: 
	 * <code>exp(t*f + t*g*(n-m))</code>
	 * the corresponding bound is expressed by:
	 * <code>(f,g)</code>
     * @return 
	 */
	@Override
	public String toString(){
		
		String first = sigma.toString();
		String second = rho.toString();
		
		String output = "("+first+","+second+")";
		return output;
	}
	
	//Getter and Setter
	
    /**
     *
     * @return
     */
    	
	public double getThetastar() {
		return Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
	}

    /**
     *
     * @return
     */
    public SymbolicFunction getRho() {
		return rho;
	}

    /**
     *
     * @param rho
     */
    public void setRho(SymbolicFunction rho) {
		this.rho = rho;
		
	}

    /**
     *
     * @return
     */
    public SymbolicFunction getSigma() {
		return sigma;
	}

    /**
     *
     * @param sigma
     */
    public void setSigma(SymbolicFunction sigma) {
		this.sigma = sigma;
	}

    /**
     *
     * @return
     */
    public Set<Integer> getArrivaldependencies() {
		return Arrivaldependencies;
	}

    /**
     *
     * @return
     */
    public Set<Integer> getServicedependencies() {
		return Servicedependencies;
	}

}
