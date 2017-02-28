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
package unikl.disco.calculator.optimization;

import java.util.Map;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;

/**
 * This interface specifies the functions that must be available
 * in order to minimize a function by the provided optimizers.
 * 
 * @author Sebastian Henningsen
 */
public interface Optimizable {

    /**
     * Called at the beginning of optimization, used for parameter extraction
     * in the @link BacklogBound
     */
    public void prepare();

    /**
     * Evaluates the function at value theta
     * @param theta
     * @return 
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     */
    public double evaluate(double theta) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;

    /**
     * Returns all hoelder parameters (if any)
     * @return
     */
    public Map<Integer, Hoelder> getHoelderParameters();

    /**
     * Returns the maximum value for theta 
     * @return
     */
    public double getMaximumTheta();
}
