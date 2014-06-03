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

/** Class representing the MGF-Bound of a constant rate
 * server. The constant rate is given by the parameter <code>
 * c</code>.
 * The function represents sigma(theta) in the (sigma,rho)-
 * -notation. To obtain the MGF of a constat rate service 
 * increment, another exponentation and multiplication with theta
 * is needed.
 * A <code>rateSigma</code>-object can be thought of a constant
 * function, with value <code>c</code>.
 * Note: A server with constant rate c is represented by a
 * negative value. Hence in the constructor <code>c</code> should
 * be negative.
 * @author Michael Beck
 * @see SymbolicFunction
 * @see Vertex
 * @see BadInitializationException
 */
public class rateSigma implements SymbolicFunction {
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4847989259142766217L;
	private double c;
	
	//Constructors
	
	public rateSigma(double c) throws BadInitializationException{
		if(c > 0) throw new BadInitializationException("Constant rate server should be initialised with a negative value", c);
		this.c = c;
	}
	
	//Methods
	
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException {
		
		//Checks for a mismatch of given and needed parameters
		if(parameters.size() != 0) throw new ParameterMismatchException("rateSigma has no parameters.");
		
		return c;
	}

	@Override
	public String toString(){
		String output = Double.toString(c);
		return output;
	}

	//Getter and Setter
	
	@Override
	public double getmaxTheta() {
		double maxtheta = Double.POSITIVE_INFINITY;
		return maxtheta;
	}

	@Override
	public Map<Integer, Hoelder> getParameters() {
		return new HashMap<>(0);
	}
}
