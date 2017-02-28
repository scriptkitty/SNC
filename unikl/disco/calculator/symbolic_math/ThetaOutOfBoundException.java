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
 * Exception, which should be thrown, if the calculation of the
 * value of some {@link FunctionIF} fails, due to the parameter
 * theta being too large. For example calling 
 * {@link ExponentialSigma.getValue} with a theta larger lambda
 * results in a non-existent MGF. Hence a ThetaOutOfBoundException
 * is thrown.
 * 
 * @author Michael Beck
 * @see FunctionIF
 */
public class ThetaOutOfBoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3205298027476314370L;

    /**
     *
     */
    public ThetaOutOfBoundException(){
		
	}
	
    /**
     *
     * @param s
     */
    public ThetaOutOfBoundException(String s){
		super(s);
	}
}
