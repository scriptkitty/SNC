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

package unikl.disco.mgf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import unikl.disco.mgf.network.Network;
import unikl.disco.misc.SetUtils;


/**
 * Class representing the stochastic bound of a service. The 
 * (negative) service is bounded by its moment generating function 
 * (MGF). Every MGF-Bound consists of three parameters, sigma, rho
 * and theta, where sigma and rho are dependent on theta. Hence 
 * sigma and rho are represented by {@link FunctionIF} objects. 
 * Usually there is a maximal possible value for theta, which is 
 * given in <code>thetastar</code>. The  functions <code>rho</code> 
 * and <code>sigma</code> are only the variables inside the 
 * (sigma,rho)-notation. For calculating the MGF-bound they need 
 * to be exponentiated and multiplied by theta.
 * Further every service may be stochastically dependent on a 
 * number of other arrival- or service-processes. These
 * dependencies are enlisted and differentiated in 
 * <code>Arrivaldependencies</code> and 
 * <code>Servicedependencies</code>.
 * 
 * @author Michael Beck
 * @see Analysis
 * @see FunctionIF
 */
public class Service implements Serializable {
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3149240364532210267L;
	private double thetastar;
	private FunctionIF rho;
	private FunctionIF sigma;
	private Set<Integer> Arrivaldependencies;
	private Set<Integer> Servicedependencies;
        private Network nw; // TODO: Maybe exchange this for Node/Flow later on
	//Constructor
	
	/**
	 * Creates a <code>Service</code> instance, with  <code>rho
	 * </code> and <code>sigma</code> being the 
	 * {@link ZeroFunctions}s.
	 */	
	public Service(Network nw){ 
		rho = new ZeroFunction();
		sigma = new ZeroFunction();
		thetastar = Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
		Arrivaldependencies = new HashSet<Integer>();
		Servicedependencies = new HashSet<Integer>();
                this.nw = nw;
	}
	
	
	/**
	 * Creates a <code>Service</code> instance with functions 
	 * <code>rho</code> and <code>sigma</code>. 
	 * <code>thetastar</code> is deduced from the two functions.
	 * @param sigma the time independent part of the MGF-bound.
 	 * @param rho the time dependent part of the MGF-bound.
	 * @see FunctionIF
	 */
	public Service(FunctionIF sigma, FunctionIF rho, Network nw) {
		this.rho = rho;
		this.sigma = sigma;
		thetastar = Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
		Arrivaldependencies = new HashSet<Integer>();
		Servicedependencies = new HashSet<Integer>();
                this.nw = nw;
	}
	
	/**
	 * Creates a <code>Service</code> instance with functions 
	 * <code>rho</code> and <code>sigma</code>.
	 * <code>thetastar</code> is deduced from the two functions.
	 * Further the service is dependent on some <code>vertex_id
	 * </code>The purpose of this constructor is to easily 
	 * associate the service as with a vertex, which results in the
	 * service being dependent on iteself.
	 * @param sigma the time independent part of the MGF-bound
	 * @param rho the time dependent part of the MGF-bound
	 * @param vertex_id the id of the vertex, with which this
	 * service is associated.
	 */
	public Service(FunctionIF sigma, FunctionIF rho, int vertex_id, Network nw) {
		this.rho = rho;
		this.sigma = sigma;
		thetastar = Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
		Arrivaldependencies = new HashSet<Integer>();
		Servicedependencies = new HashSet<Integer>();
		Servicedependencies.add(vertex_id);
                this.nw = nw;
	}
	
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
	 * @param sigmaparameters the point at which the MGF is
	 * evaluated. It has to match the number of parameters needed 
	 * to calculate sigma. The first parameter needs to be theta.
	 * @param rhoparameters the point at which the MGF is evaluated. 
	 * It has to match the number of parameters needed to calculate 
	 * rho. The first parameter needs to be theta.
	 * @param n the end of the time interval (m,n]
	 * @param m the beginning of the time interval (m,n]
	 * @return the value of the MGF at the given point
	 * @throws ServerOverloadException 
	 */
	public double evaluate(double theta, HashMap<Integer, Hoelder> sigmaparameters, HashMap<Integer, Hoelder> rhoparameters, int n, int m)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		double value = Math.exp(theta*sigma.getValue(theta, sigmaparameters) + theta*rho.getValue(theta, rhoparameters)*(n-m));
		return value;
	}
	
	/**
	 * Returns the <code>Service</code>, which is the result
	 * of concatenating <code>service1</code> with <code>service2
	 * </code>. Stochastic dependencies are taken into account.
	 * In the stochastic dependent case this leads to the 
	 * introduction of a new Hoelder-coefficient.
	 * @param service1 the first flow being multiplexed
	 * @param service2 the second flow being multiplexed 
	 * @return the multiplexed arrival
	 */
	public Service concatenate(Service service1, Service service2){
		Service service;
		
		//Dependent Case
		if(!SetUtils.getIntersection(service1.getServicedependencies(),service2.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service1.getArrivaldependencies(), service2.getArrivaldependencies()).isEmpty()){
			Hoelder hoelder = nw.createHoelder();
			FunctionIF givensigma = new AddedFunctions(new AddedFunctions(service1.getSigma(),service2.getSigma(),hoelder),new BFunction(new NegAbsDiffFunction(service1.getRho(),service2.getRho(),hoelder)));
			FunctionIF givenrho = new MaximumFunction(service1.getRho(), service2.getRho(), hoelder);
			service = new Service(givensigma, givenrho, nw);
		}
		
		//Independent Case
		else{
			FunctionIF givensigma = new AddedFunctions(new AddedFunctions(service1.getSigma(),service2.getSigma()),new BFunction(new NegAbsDiffFunction(service1.getRho(),service2.getRho())));
			FunctionIF givenrho = new MaximumFunction(service1.getRho(), service2.getRho());
			service = new Service(givensigma, givenrho, nw);
		}
		
		//Keeps track of stochastic dependencies
		service.addArrivalDependency(service1.getArrivaldependencies());
		service.addArrivalDependency(service2.getArrivaldependencies());
		service.addServiceDependency(service2.getServicedependencies());
		service.addServiceDependency(service1.getServicedependencies());
		
		return service;
	}
	
	/**
	 * Returns the leftover-<code>service</code>, resulting from a 
	 * server with some <code>Service</code>, working on an 
	 * {@link Arrival}. Stochastic dependencies are taken into 
	 * account. 
	 * In the stochastic dependent case this leads to the 
	 * introduction of a new Hoelder-coefficient.
	 * @param arrival the arrival being served
	 * @param service the service of the service element
	 * @return the leftover service of the service element
	 */
	public Service leftover(Arrival arrival, Service service){
		Service leftoverservice;

		//Dependent Case
		if(!SetUtils.getIntersection(arrival.getServicedependencies(),service.getServicedependencies()).isEmpty() || !SetUtils.getIntersection(service.getArrivaldependencies(), arrival.getArrivaldependencies()).isEmpty()){
			Hoelder hoelder = nw.createHoelder();
			FunctionIF givensigma = new AddedFunctions(arrival.getSigma(),service.getSigma(),hoelder);
			FunctionIF givenrho = new AddedFunctions(arrival.getRho(),service.getRho(),hoelder);
			leftoverservice = new Service(givensigma, givenrho, nw);
			System.out.println("Dependent Case Leftover calculated");
		}
		
		//Independent Case
		else{
			FunctionIF givensigma = new AddedFunctions(arrival.getSigma(),service.getSigma());
			FunctionIF givenrho = new AddedFunctions(arrival.getRho(),service.getRho());
			leftoverservice = new Service(givensigma, givenrho, nw);
			System.out.println("Independent Case Leftover calculated");
		}
		
		//Keeps track of stochastic dependencies
		leftoverservice.addArrivalDependency(arrival.getArrivaldependencies());
		leftoverservice.addServiceDependency(service.getServicedependencies());
		leftoverservice.addArrivalDependency(service.getArrivaldependencies());
		leftoverservice.addServiceDependency(arrival.getServicedependencies());
		
		return leftoverservice;
	}
	
	/**
	 * Returns a String representation of the service in MGF-
	 * Notation. This means, if the MGF of a service in an
	 * interval (m,n] is bounded by: 
	 * <code>exp(t*f + t*g*(n-m))</code>
	 * the corresponding bound is expressed by:
	 * <code>(f,g)</code>
	 */
	@Override
	public String toString(){
		String first = sigma.toString();
		String second = rho.toString();
		String output = "("+first+","+second+")";
		return output;
	}
	
	//Getter and Setter
	
	public double getThetastar() {
		return thetastar;
	}

	public FunctionIF getRho() {
		return rho;
	}

	public void setRho(FunctionIF rho) {
		this.rho = rho;
		thetastar = Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
	}

	public FunctionIF getSigma() {
		return sigma;
	}

	public void setSigma(FunctionIF sigma) {
		this.sigma = sigma;
		thetastar = Math.min(rho.getmaxTheta(), sigma.getmaxTheta());
	}

	public Set<Integer> getArrivaldependencies() {
		return Arrivaldependencies;
	}

	public Set<Integer> getServicedependencies() {
		return Servicedependencies;
	}

}