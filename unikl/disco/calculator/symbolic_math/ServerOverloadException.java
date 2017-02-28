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

/**
 * Exception, which should be thrown, if the argument in a BFunction
 * leads to giving the logarithm-function a non-positive argument. 
 * This might happen, even if an allowed set of theta and Hoelder-
 * coefficients is chosen (i.e. the network is just not stable).
 * 
 * @author Michael Beck
 */

public class ServerOverloadException extends Exception {

	
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3067169808121540318L;
	
	//Constructor
	
    /**
     *
     * @param s
     */
    	
	public ServerOverloadException(String s){
		super(s);
	}

}
