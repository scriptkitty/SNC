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
package unikl.disco.mgf.optimization;

import java.util.HashMap;
import unikl.disco.mgf.Hoelder;
import unikl.disco.mgf.ParameterMismatchException;
import unikl.disco.mgf.ServerOverloadException;
import unikl.disco.mgf.ThetaOutOfBoundException;

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
    public HashMap<Integer, Hoelder> getHoelderParameters();

    /**
     * Returns the maximum value for theta 
     * @return
     */
    public double getMaximumTheta();
}
