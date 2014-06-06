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

package unikl.disco.calculator.symbolic_math;

import java.util.HashMap;
import java.util.Map;

import unikl.disco.calculator.network.Network;

/**
 * This introduces a new parameter in form of a {@link SymbolicFunction}.
 * It is needed for calculating the backlog-bound for a certain
 * backlog <code>x</code>, which has its own <code>parameter_id
 * </code>. The function acts as identity-function and just returns
 * <code>x</code>, when invoked by {@link getValue}.
 * A <code>NewParameter</code>-object can be thought of an identity-
 * function.
 * @author Michael Beck
 * @see Analysis
 * @see SymbolicFunction
 */
public class NewParameter implements SymbolicFunction {

	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8098072874403146679L;
	private Hoelder hoelder;
	
	//Constructors
	
	/**
	 * A new parameter always gets a fresh parameter_id from the 
	 * {@link Analysis}-Class.
	 */
	public NewParameter(Hoelder hoelder){
		this.hoelder = hoelder;
	}
	
	//Methods 
	
	/**
	 * Just returns the value of <code>parameters[1]</code>. The
	 * first entry in <code>parameters</code> should be theta, but
	 * is however not used. The special form of the input <code>
	 * parameters</code> is just needed to serve the overall 
	 * structure of {@link SymbolicFunction}s.
	 * @param parameters an array consisting of theta (first entry)
	 * and the new parameter (second entry).
	 * @return the new parameter as given.
	 */
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters) throws ParameterMismatchException{
		if(parameters.size() != 1) throw new ParameterMismatchException("NewParameter has exactly one parameter.");
		return hoelder.getPValue();
	}

	
	/**
	 * Returns a string representation of the new parameter, by:<br>
	 * <code>parameter_id</code>
	 * where <code>parameter</code> is just the parameter-id as 
	 * given by the constructor.
	 * @return a String representation of the new parameter.
	 */
	@Override
	public String toString(){
		String output = Integer.toString(hoelder.getHoelderID())+"_id";
		return output;
	}
	
	@Override
	public double getmaxTheta() {
		double maxtheta = Double.POSITIVE_INFINITY;
		return maxtheta;
	}

	@Override
	public Map<Integer, Hoelder> getParameters() {
		HashMap<Integer, Hoelder> parameter_ids = new  HashMap<Integer, Hoelder>(0);
		parameter_ids.put(hoelder.getHoelderID(), hoelder);
		return parameter_ids;
	}

}
