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
import java.util.HashMap;
import java.util.Map;


/**
 * Interface representing a function in theta. 
 * <code>getValue(double[] parameters</code> returns the value of
 * the function at theta and needs to be defined by the implementing
 * class. Also a maximal value of theta and a string representation
 * must be specified, which are accessed via <code>maxTheta()
 * </code> and <code>toString</code> respectively.
 * 
 * @author Michael Beck
 */

public abstract interface SymbolicFunction extends Serializable {

	//Methods
	
	/**
	 * Returns the value of the theta-dependent function at theta 
	 * (theta must be the first of the parameters).
     * @param theta
	 * @param parameters the set of parameters, including theta and 
	 * further parameters on which the function depends.
	 * @return the value of the theta-dependent function at theta.
     * @throws unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException
     * @throws unikl.disco.calculator.symbolic_math.ParameterMismatchException
	 * @throws ServerOverloadException 
	 */
	double getValue(double theta, Map<Integer, Hoelder> parameters) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
	
	/**
	 * Returns a string representation of the function
     * @return 
	 */
	@Override
	String toString();
		
	//Getter and Setter
	
    /**
     *
     * @return
     */
    	
	double getmaxTheta();
	
    /**
     *
     * @return
     */
    Map<Integer, Hoelder> getParameters();
	
}
