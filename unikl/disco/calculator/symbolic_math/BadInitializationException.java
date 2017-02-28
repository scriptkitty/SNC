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
 * Exception, which should be thrown, if the initialization of 
 * some {@link Arrival} or {@link Service} fails, due to a 
 * prohibited choice of parameters. For example initializing an
 * arrival with exponentially distributed arrivals needs a rate
 * parameter lambda larger zero. Initializing 
 * {@link ExponentialSigma} with a non-positive parameter hence
 * throws a <code>BadInitializationException</code>.
 * The field <code>initvalue</code> can be used to give the invalid
 * initial value, causing the exception.
 * 
 * @author Michael Beck
 */

public class BadInitializationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2904109072521845677L;
	
	//Members
	
	double initvalue;
	
	//Constructor
	
    /**
     *
     * @param s
     * @param initvalue
     */
    	
	public BadInitializationException(String s, double initvalue){
		super(s);
		this.initvalue = initvalue;
	}

}
