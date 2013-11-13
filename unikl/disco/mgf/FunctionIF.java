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

public abstract interface FunctionIF extends Serializable {

	//Methods
	
	/**
	 * Returns the value of the theta-dependent function at theta 
	 * (theta must be the first of the parameters).
	 * @param parameters the set of parameters, including theta and 
	 * further parameters on which the function depends.
	 * @return the value of the theta-dependent function at theta.
	 * @throws ServerOverloadException 
	 */
	double getValue(double theta, HashMap<Integer, Hoelder> parameters) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
	
	/**
	 * Returns a string representation of the function
	 */
	@Override
	String toString();
		
	//Getter and Setter
	
	double getmaxTheta();
	
	HashMap<Integer, Hoelder> getParameters();
	
}
