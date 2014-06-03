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

import java.util.HashMap;
import java.util.Map;

import unikl.disco.misc.SetUtils;

/**
 * A class representing a function, which is the result of taking
 * the negative absolute difference between two theta-dependent 
 * functions. Given by:<br>
 * -|f(t)-g(t)|<br>
 * The calculation aims at calculating MGF-bounds, hence a 
 * stochastically dependent version of it is implemented.
 * The original functions (called atom-functions) are denoted by
 * <code>first</code> and <code>second</code> and are 
 * {@link SymbolicFunction}-objects. The maximal theta for which 
 * the resulting function is defined, is given by the minimum of 
 * the maximal thetas of the atom-functions. To calculate values
 * of the resulting functions knowledge about the parameter-sets 
 * of the atom-functions is needed and represented in <code>
 * firstparameterIDs</code> and <code>secondparameterIDs</code>.
 * The integer <code>HoelderID</code> decides whether 
 * taking the negative absolute difference is calculated normally 
 * (stochastically independent case: <code>HoelderID = 0</code>) or
 * with an Hölder-coefficient (stochastically dependent case: 
 * <code>HoelderID != 0</code>).
 * @author Michael Beck
 * @see SymbolicFunction
 */
public class NegativeAbsoluteDifference extends BinaryFunction implements SymbolicFunction{
	
	//Members

	/**
	 * 
	 */
	private static final long serialVersionUID = -3367229262199782289L;
	
	//Constructors
	
	/**
	 * Constructs an <code>NegAbsDiffFunction</code> entity. If 
	 * <code>independent</code> is set <code>true</code>, the 
	 * independent version is constructed.
	 * If <code>independent</code> is set <code>false</code>, the 
	 * stochastically dependent version is invoked, by using the 
	 * next HoelderID of the {@link Analysis}-Class.
	 * In the stochastically dependent case the <code>
	 * first</code> atom-function uses the Hoelder-Coefficient p
	 * directly, while the <code>second</code> atom-function uses
	 * the corresponding coefficient given by: 1/(1-1/p)). 
	 * @param first the first atom function
	 * @param second the second atom function
	 * @param independent determines if the stochastically dependent
	 * or independent version is invoked.
	 */
	public NegativeAbsoluteDifference(SymbolicFunction first, SymbolicFunction second){
	    super(first, second);
	}
	
	/**
	 * Constructs an <code>MaximumFunction</code> entity. If 
	 * <code>HoelderID</code> equals <code>0</code>, the 
	 * independent version is constructed.
	 * If <code>HoelderID</code> takes another value, the 
	 * stochastically dependent version is invoked with the given
	 * HoelderID.
	 * In the case of stochastically dependent adding, the <code>
	 * first</code> atom-function uses the Hoelder-Coefficient p
	 * directly, while the <code>second</code> atom-function uses
	 * the corresponding coefficient given by: 1/(1-1/p)). 
	 * @param first the first atom function
	 * @param second the second atom function
	 * @param HoelderID determines if the stochastically dependent
	 * or independent version is invoked.
	 */	
	public NegativeAbsoluteDifference(SymbolicFunction first, SymbolicFunction second, Hoelder hoelder){
	    super(first, second, hoelder);
	}

	//Methods
	
	/**
	 * Calculates the value of the resulting function at theta 
	 * (first entry in <code>parameters</code>), with given 
	 * <code>parameters</code>.
	 * @param parameters contains the needed parameters (including
	 * theta, as first entry) to calculate the value of the 
	 * function. Entries indexed from 1 to 
	 * {@link firstparameterIDs.size()} belong to the first atom-
	 * function, following entries to the second atom-function.
	 * @return the value of the added functions, evaluated at the
	 * given parameters.
	 * @throws ServerOverloadException 
	 */
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
	    /*
		//Checks for a mismatch in number of given and needed parameters
		if((hoelder == null && parameters.size() != SetUtils.getUnion(firstparameters.keySet(), secondparameters.keySet()).size()) 
				|| (hoelder != null && parameters.size() != SetUtils.getUnion(firstparameters.keySet(), secondparameters.keySet()).size() +1)){
			throw new ParameterMismatchException("Total number of parameters does not match for atom functions (added)");
		}
		
		//Constructs the parameter-arrays, serving as input for the atom-functions
		HashMap<Integer, Hoelder> given1 = new HashMap<Integer, Hoelder>();
		HashMap<Integer, Hoelder> given2 = new HashMap<Integer, Hoelder>();
	
		for(Map.Entry<Integer, Hoelder> entry : firstparameters.entrySet()){
			// WRONG  if(parameters.containsKey(entry.getKey())) given1.put(entry.getKey(), entry.getValue());
			if(parameters.containsKey(entry.getKey())) given1.put(entry.getKey(), parameters.get(entry.getKey()));
			else throw new ParameterMismatchException("Needed hoelder_id is not found in given parameters.");
		}
		for(Map.Entry<Integer, Hoelder> entry : secondparameters.entrySet()){
			//WRONG  if(parameters.containsKey(entry.getKey())) given2.put(entry.getKey(), entry.getValue());
			if(parameters.containsKey(entry.getKey())) given2.put(entry.getKey(), parameters.get(entry.getKey()));
			else throw new ParameterMismatchException("Needed hoelder_id is not found in given parameters.");
		}
	
		//Introduces H�lder-coefficients, if needed
		//Multiplies the H�lder-coefficients to theta, if needed
		double theta1 = (hoelder == null) ? theta : theta*hoelder.getPValue();
		double theta2 = (hoelder == null) ? theta : theta*hoelder.getQValue();

		if(first.getValue(theta1, given1) > second.getValue(theta2, given2)){
			return second.getValue(theta2, given2) - first.getValue(theta1, given1);
		}
		else return first.getValue(theta1, given1) - second.getValue(theta2, given2);*/
	    if(checkForParameterMismatch(parameters))
		throw new ParameterMismatchException("Total number of parameters does not match for atom functions (added)");
	    
	    double value1 = getValueOfFunction(theta, parameters, 1);
	    double value2 = getValueOfFunction(theta, parameters, 2);
	    return (value1 > value2 ? value1 - value2 : value2 - value1);
	}

	/**
	 * Returns string representation of the function in polish
	 * notation. Given by:<br>
	 * <code>-|f(t)-g(t)|</code><br>
	 * @return a String representation of the function.
	 */
	@Override
	public String toString(){
	    return "-| "+first.toString()+" - "+second.toString()+" |";
	}
}
