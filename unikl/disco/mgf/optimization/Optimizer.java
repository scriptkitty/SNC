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

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.ParameterMismatchException;
import unikl.disco.mgf.ServerOverloadException;
import unikl.disco.mgf.ThetaOutOfBoundException;
import unikl.disco.mgf.network.AbstractAnalysis;

/**
 *
 * @author Sebastian Henningsen
 */
public interface Optimizer {

    /**
     *
     * @param thetagranularity
     * @param hoeldergranularity
     * @return
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     */
    public double minimize(double thetagranularity, double hoeldergranularity) 
            throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;

    /**
     *
     * @param input
     * @param boundtype
     * @param bound
     * @param thetagranularity
     * @param hoeldergranularity
     * @return
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     * @deprecated
     */
    @Deprecated
    public double Bound(Arrival input, AbstractAnalysis.Boundtype boundtype, 
            double bound, double thetagranularity, double hoeldergranularity) 
            throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;

    /**
     *
     * @param input
     * @param boundtype
     * @param violation_probability
     * @param thetagranularity
     * @param hoeldergranularity
     * @return
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     * @deprecated
     */
    @Deprecated
    public abstract double ReverseBound(Arrival input, AbstractAnalysis.Boundtype boundtype, 
            double violation_probability, double thetagranularity, double hoeldergranularity) 
            throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException;
}
