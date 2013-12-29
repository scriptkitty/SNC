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

import unikl.disco.mgf.network.Network;

/** 
 * This alters an exisiting {@link FunctionIF} (called atom-function
 * in the sense that the theta is scaled by <code>
 * scale_parameter_ID</code>. In expression if <code>f(theta)</code>
 * is a theta-dependent function the result of this FunctionIF is:
 * <br><code>f(theta*scale_parameter_ID)</code>. This is needed for
 * performing the stochastically dependent versions of several other
 * operators, like {@link AddedFunctions} or 
 * {@link MaximumFunction}.
 * Normally the scaling takes place in two different ways, depending
 * which "part" of a Hoelder-coefficient is needed (p or q). Which
 * kind of scaling is used is determined by the boolean <code>
 * p_scale</code>.
 * @author Michael Beck
 * @see FunctionIF
 * @see BadInitializationException
 */
public class scaledFunction implements FunctionIF {
	
	//Members
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546822282037420484L;
	private FunctionIF original;
	private Hoelder hoelder;
	private boolean p_scale;
	
	//Constructors
	
	/**
	 * Constructs an <code>scaledFunction</code> entity. If 
	 * <code>p_scale</code> is set <code>true</code> the 
	 * normal scaling version is constructed, as described above.
	 * If <code>p_scale</code> is set <code>false</code> the 
	 * second part of a Hoelder-coefficient is used to scale the 
	 * atom function.
	 * This constructor gets the next HoelderID of the 
	 * {@link Analysis}-Class.
	 * @param function the atom function
	 * @param p_scale wether the first or second part of a Hoelder-
	 * coefficient should be used for scaling.
	 */
	/*public scaledFunction(FunctionIF function, boolean p_scale){
		this.original = function;
		this.hoelder = Network.createHoelder();
		this.p_scale = p_scale;
	}*/
	
	/**
	 * Constructs an <code>scaledFunction</code> entity. If 
	 * <code>p_scale</code> is set <code>true</code> the 
	 * normal scaling version is constructed, as described above.
	 * If <code>p_scale</code> is set <code>false</code> the 
	 * second part of a Hoelder-coefficient is used to scale the 
	 * atom function.
	 * @param function the atom function
	 * @param hoelder_id the ID of the used Hoelder-coefficent
	 * @param p_scale wether the first or second part of a Hoelder-
	 * coefficient should be used for scaling.
	 */
	public scaledFunction(FunctionIF function, Hoelder hoelder, boolean p_scale){
		this.original = function;
		this.hoelder = hoelder;
		this.p_scale = p_scale;
	}
	
	//Methods
	
	/**
	 * Calculates the value of the resulting function at theta 
	 * (first entry in <code>parameters</code>), with given 
	 * <code>parameters</code>. It is important to note that the 
	 * last entry in <code>parameters</code> is the parameter, 
	 * which is used for scaling theta.
	 * @param parameters contains the needed parameters (including
	 * theta, as first entry). The last entry in parameters gives
	 * the value of the Hoelder-coefficient, which is used for 
	 * scaling theta.
	 * @return the value of the scaled function.
	 * @throws ServerOverloadException 
	 */
	@Override
	public double getValue(double theta, HashMap<Integer, Hoelder> parameters)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		
		//Checks if number of given and needed parameters matches
		if(parameters.size() != original.getParameters().size()+1 && !original.getParameters().containsKey(hoelder)
				|| parameters.size() != original.getParameters().size() && original.getParameters().containsKey(hoelder)){
			throw new ParameterMismatchException("Number of parameters for scaled function does not match");
		}
		
		//Constructs the parameter-array needed for calculating the scaled atom-function
		HashMap<Integer, Hoelder> givenparameters = new HashMap<Integer, Hoelder>(0);
		
		for(Map.Entry<Integer, Hoelder> entry : original.getParameters().entrySet()){
			if(parameters.containsKey(entry.getKey())) givenparameters.put(entry.getKey(), parameters.get(entry.getKey()));
			else throw new ParameterMismatchException("Needed hoelder_id is not found in given parameters.");
		}
		
		//Scales theta
		
		if(p_scale)	theta = theta*parameters.get(hoelder.getHoelderID()).getPValue();
		else theta = theta*parameters.get(hoelder.getHoelderID()).getQValue();

		return original.getValue(theta, givenparameters);
	}

	/**
	 * Gives a String representation of the scaled function by<br>
	 * <code>scaled(f(t),scale_parameter_ID)</code><br>
	 */
	@Override
	public String toString(){
		if(p_scale)	return "scaled("+original.toString()+","+Integer.toString(hoelder.getHoelderID())+")";
		else return "scaled("+original.toString()+","+Integer.toString(hoelder.getHoelderID())+",q)";
	}

	//Getter and Setter
	
	@Override
	public double getmaxTheta() {
		if(p_scale)	return original.getmaxTheta()/hoelder.getPValue();
		else return original.getmaxTheta()/hoelder.getQValue();
	}

	@Override
	public HashMap<Integer, Hoelder> getParameters() {
		HashMap<Integer, Hoelder> copy = original.getParameters();
		copy.put(hoelder.getHoelderID(), hoelder);
		return copy;
	}
	
	
}
