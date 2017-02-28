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

import java.util.Map;

/**
 * A class effectively applying the unitary minus operation, i.e.
 * when f(t) is the atom function this class will give -f(t) as 
 * result.
 * @author Michael Beck
 * @see SymbolicFunction
 *
 */
public class UnitaryMinus implements SymbolicFunction {

	//Members
	SymbolicFunction atom;
	Map<Integer,Hoelder> parameters;
	
	public UnitaryMinus(SymbolicFunction atom){
		this.atom = atom;
		parameters = atom.getParameters();
	}

	@Override
	public double getValue(double theta, Map<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		return -atom.getValue(theta, parameters);
	}

	@Override
	public double getmaxTheta() {
		return atom.getmaxTheta();
	}

	@Override
	public Map<Integer, Hoelder> getParameters() {
		return atom.getParameters();
	}
	
	@Override
	public String toString(){
		
		String output = "-"+atom.toString();
		
		return output;
	}

}
