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
package unikl.disco.calculator.symbolic_math.functions;

import unikl.disco.calculator.symbolic_math.SymbolicFunction;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.Hoelder;
import java.util.HashMap;
import java.util.Map;

/** Class representing the MGF-Bound of an exponentially
 * distributed increment. The parameter of the underlying 
 * exponential distribution is given by <code>lambda</code>.
 * <code>lambda</code> needs to be a positive quantity to define
 * exponentially distributed increments.
 * The function represents the sigma(theta)-term in the (sigma,rho)-
 * notation. To obtain the MGF of an exponentially distributed 
 * increment, an exponentation and multiplication with theta
 * is needed.
 * 
 * @author Michael Beck
 * @see SymbolicFunction
 * @see Arrival
 * @see BadInitializationException
 */
public class ExponentialSigma implements SymbolicFunction{
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5146196878977702835L;
	private double lambda;
	
	//Constructor
	
    /**
     *
     * @param lambda
     * @throws BadInitializationException
     */
    	
	public ExponentialSigma(double lambda) throws BadInitializationException{
		if(lambda < 0 || lambda == 0){
			throw new BadInitializationException("lambda must be larger zero", lambda);
		}
		this.lambda = lambda;
	}

	//Methods
	
	/**
	 * Calculates the value of the exponential increment in the 
	 * sigma-rho-notation. 
     * @param theta
	 * @param parameters its first parameter must be theta. Further
	 * it must consist of only one parameter.
     * @throws unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException
     * @throws unikl.disco.calculator.symbolic_math.ParameterMismatchException
	 */
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException {
		
		//Checks if only one parameter is given
		if(parameters.size() != 0){
			throw new ParameterMismatchException("exponentialsigma needs exactly one parameter");
		}
		
		//Checks if theta is smaller lambda
		if(theta >= lambda){
			throw new ThetaOutOfBoundException("theta ("+theta+") larger lambda ("+lambda+") in exponential distribution");
		}
		
		return 1/theta * Math.log(lambda/(lambda-theta));
	}
	
	/**
	 * Returns a string representation of the exponential 
	 * increments in the form <code>exp_arr(lambda)</code>
     * @return 
	 */
	@Override
	public String toString(){
		String output = "exp_arr("+Double.toString(lambda)+")";
		return output;
	}
	
	//Getter and Setter
	
    /**
     *
     * @return
     */
    	
	@Override
	public double getmaxTheta() {
		return lambda;
	}

    /**
     *
     * @return
     */
    @Override
	public Map<Integer, Hoelder> getParameters() {
		return new HashMap<Integer, Hoelder>(0);
	}
}
