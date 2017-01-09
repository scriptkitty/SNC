/*
 *  (c) 2016 Michael A. Beck, City University of Hong Kong, Hong Kong
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

package unikl.disco.calculator.symbolic_math.functions;

import java.util.Map;
import java.util.HashMap;

import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.SymbolicFunction;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;

/**
 * A class representing the sigma-term in MGF-bounds derived
 * for stationary arrivals sent through a token bucket. In this class, if t is the input-
 * parameter theta the resulting funciton is:<br>
 * TBSigma(t) = 1/t*log(0.5*exp(t*B) + 0.5*exp(-t*B))<br>
 * Here "B" is the (combined) bucket size of the (aggregated) token buckets.
 * 
 * The existence of the arrivals' MGF (before they enter the token-bucket) determines the 
 * maximal value for thete. However, as there are no assumptions on the arrivals made,
 * except being stationary, the value cannot be derived from any other parameters.
 * Hence, it must be given by hand. It's default value is 
 * <code>POSITIVE_INFINITY.</code>
 * 
 * @author Michael Beck
 * @see FunctionIF
 * @see ArrivalFactory
 */
public class StationaryTBSigma implements SymbolicFunction {

	//Members
	/**
	 * 
	 */
	private static final long serialVersionUID = 3048571928799189089L;
	double bucket;
	double maxTheta = Double.POSITIVE_INFINITY;
	
	//Constructor
	public StationaryTBSigma(double bucket) throws BadInitializationException{
		if(!(bucket > 0)){
			throw new BadInitializationException("Bucket size of arrivals must be positive", bucket);
		}
		this.bucket = bucket;
	}	
	
	public StationaryTBSigma(double bucket, double maxTheta) throws BadInitializationException{
		if(!(bucket > 0)){
			throw new BadInitializationException("Bucket size of arrivals must be positive", bucket);
		}
		this.bucket = bucket;
		this.maxTheta = maxTheta;
	}
	
	//Methods

	/**
	 * Calculates the value of the resulting sigma-function at theta 
	 * with given <code>parameters</code>.
	 * @param parameters contains the needed parameters (none are needed
	 * as there are no atom-functions involved).
	 * @return the value of the sigma-function
	 * @throws ParameterMismatchException 
	 */
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ServerOverloadException, ParameterMismatchException {

		//Checks for a mismatch of given and needed parameters
		if(parameters.size() != 0) throw new ParameterMismatchException("StationaryTBSigma has only (modified) theta as parameters.");
		
		//Checks if theta is larger the decay-rate (in which case the integral appearing in the conversion theorem is indefinite)
		if(theta > maxTheta){
			throw new ThetaOutOfBoundException("The given theta exceeds the maximal theta given here. theta: "+theta+". maxTheta: "+maxTheta);
		}
		
		else {
			return 1/theta*Math.log(0.5* Math.exp(theta*bucket) + 0.5* Math.exp(-theta*bucket));
		}
	}

	/**
	 * Returns a string representation of the EBB-function. In the 
	 * form <code>EBB(rate,decay,prefactor)</code>.
	 */
	@Override
	public String toString(){
		String output = "StatTB("+bucket+")";
		return output;
	}
	
	//Getter and Setter
	@Override
	public double getmaxTheta() {
		return maxTheta;
	}

	@Override
	public Map<Integer, Hoelder> getParameters() {
		return new HashMap<Integer, Hoelder>(0);
	}
	


}
