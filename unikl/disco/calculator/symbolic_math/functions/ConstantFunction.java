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
import unikl.disco.calculator.symbolic_math.Hoelder;
import java.util.HashMap;
import java.util.Map;

/** 
 * Class representing a zero function. Regardless of the input it
 * just returns zero as value. There is no maximal theta.
 * This function is needed for initializing the sigma-part
 * of {@link Service} or {@link Arrival} elements, such as constant
 * rate servers or arrivals not containing "bursts".
 * @author Michael Beck
 * @see SymbolicFunction
 */
public class ConstantFunction implements SymbolicFunction {

	
	private static final long serialVersionUID = 1154257189928764896L;
	private final double rate;
	private final double maxTheta;
	
    /**
     *
     * @param rate
     */
    public ConstantFunction(double rate) {
	    this.rate = rate;
	    this.maxTheta = Double.POSITIVE_INFINITY;
	}
	
	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters) throws ParameterMismatchException {
	    if(!parameters.isEmpty()) {
		throw new ParameterMismatchException("Constant function has no Hoelder parameters");
	    }
	    return rate;
	}
	
	@Override
	public String toString(){
		return Double.toString(rate);
	}
	
    /**
     *
     * @return
     */
    @Override
	public double getmaxTheta(){
		return maxTheta;
	}

    /**
     *
     * @return
     */
    @Override
	public Map<Integer, Hoelder> getParameters() {
	    return new HashMap<>(0);
	}
	
}
