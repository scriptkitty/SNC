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
package unikl.disco.mgf.optimization;

import java.util.HashMap;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.Hoelder;
import unikl.disco.mgf.ParameterMismatchException;
import unikl.disco.mgf.ServerOverloadException;
import unikl.disco.mgf.ThetaOutOfBoundException;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.Network;

/**
 * This performs the numerical part of optimizing the bound
 * found by the analysis. For this a good combination of parameter-
 * values for the <code>input</code> is found and by this a 
 * near optimal bound on violation probabilities calculated.
 * Important is also the "reverse" operation, which finds the
 * nearly optimal bound for a given violation probability.
 * A calculation of output bounds is not needed.
 * @author Michael Beck
 *
 */
public abstract class AbstractOptimizer {
	
	protected Optimizable bound;
	protected AbstractAnalysis.Boundtype boundtype;
	protected double maxTheta;
        protected Network nw;
	
	public AbstractOptimizer(Optimizable input, AbstractAnalysis.Boundtype boundtype, Network nw){
		this.bound = input;
		this.boundtype = boundtype;
                this.nw = nw;
	}
	
        public abstract double minimize(double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
	/**
	 * Computes a bound on the violation probability that a 
	 * given backlog or delay is broken.
	 * @param input the bound in arrival-representation
	 * @param boundtype either BACKLOG or DELAY
	 * @param bound the bound on the backlog or delay
	 * @return a bound on the violation probability
	 * @throws ParameterMismatchException 
	 * @throws ThetaOutOfBoundException 
	 * @throws ServerOverloadException 
	 */
	public abstract double Bound(Arrival input, AbstractAnalysis.Boundtype boundtype, double bound, double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
	
	/**
	 * Computes the reverse bound for delay of backlog. The 
	 * reverse bound is the smallest possible backlog or delay, 
	 * such that a given violation probability is not broken. 
	 * @param input the bound in arrival-representation
	 * @param boundtype either BACKLOG or DELAY
	 * @param violation_probability the violation probability,
	 * which must not been exceeded by the bound. 
	 * @throws ServerOverloadException 
	 * @throws ParameterMismatchException 
	 * @throws ThetaOutOfBoundException 
	 */
	public abstract double ReverseBound(Arrival input, AbstractAnalysis.Boundtype boundtype, 
										double violation_probability, double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
	
}
