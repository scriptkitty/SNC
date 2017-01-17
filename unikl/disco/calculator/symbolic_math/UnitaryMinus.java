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

}
