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

/** 
 * Class representing a zero function. Regardless of the input it
 * just returns zero as value. There is no maximal theta.
 * This function is needed for initializing the sigma-part
 * of {@link Service} or {@link Arrival} elements, such as constant
 * rate servers or arrivals not containing "bursts".
 * @author Michael Beck
 * @see FunctionIF
 */
public class ZeroFunction implements FunctionIF {

	//Methods
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1154257189928764896L;

	@Override
	public double getValue(double theta, HashMap<Integer, Hoelder> parameters) {
		return 0;
	}
	
	@Override
	public String toString(){
		String output = "0";
		return output;
	}
	
	//Getter and Setter
	
	@Override
	public double getmaxTheta(){
		double maxtheta = Double.POSITIVE_INFINITY;
		return maxtheta;
	}

	@Override
	public HashMap<Integer, Hoelder> getParameters() {
		return new HashMap<Integer, Hoelder>(0);
	}
	
}
