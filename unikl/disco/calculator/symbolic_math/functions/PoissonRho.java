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

package unikl.disco.calculator.symbolic_math.functions;

import unikl.disco.calculator.symbolic_math.SymbolicFunction;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;
import unikl.disco.calculator.symbolic_math.Hoelder;
import java.util.HashMap;
import java.util.Map;

/** Class representing the rho-part of the MGF-Bound of an arrival
 * with exponentially distributed interarrival times. The parameter of the underlying 
 * exponential distribution is given by <code>mu</code> and is called intensity.
 * <code>mu</code> needs to be a positive quantity to define
 * exponentially distributed interarrival times.
 * The increments are represented by their corresponding MGF-Bound as a theta-dependent
 * function.
 * The function represents:
 * 1/t * mu * ( phi(t) - 1 )
 * with t being theta and phi being the MGF-Bound of the increment.
 * 
 * @author Michael Beck
 * @see SymbolicFunction
 * @see Arrival
 * @see BadInitializationException
 */public class PoissonRho implements SymbolicFunction {
	 
	//Members

	/**
	 * 
	 */
	private static final long serialVersionUID = -3592398716087106351L;
	private SymbolicFunction rho;
	private Map<Integer, Hoelder> rhoParameters;
	private double mu;	

	//Constructors
	
	/**
	 * Constructs an <code>PoissonRho</code> entity.
	 * @param mu the the intensity of the underlying Poisson process
	 * @param rho the MGF bound on the increment
	 */
	public PoissonRho(SymbolicFunction rho, double mu){
		this.rho = rho;
		this.mu= mu;
		this.rhoParameters= rho.getParameters();
	}

	/**
	 * Calculates the value of the rho-part of the arrival with
	 * exponentially distributed interarrival times.
     * @param theta
	 * @param parameters its first parameter must be theta. 
     * @throws unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException 
     * @throws unikl.disco.calculator.symbolic_math.ParameterMismatchException 
	 */
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {

		//Checks for a mismatch in number of given and needed parameters
		if(parameters.size() != rhoParameters.size())
			throw new ParameterMismatchException("Number of parameters does not match for atom functions (Poisson)");
			
		return mu/theta*(Math.exp(rho.getValue(theta, parameters)*theta) - 1 );
	}
		
	public String toString(){
		String output = "Poi_arr("+rho.toString()+")";
		return output;
	}
		
	//Getter and Setter
	
    /**
     *
     * @return
     */
    	
	@Override
	public double getmaxTheta() {
		return rho.getmaxTheta();
	}

    /**
     *
     * @return
     */
    @Override
	public Map<Integer, Hoelder> getParameters() {
		return rhoParameters;
	}

}
