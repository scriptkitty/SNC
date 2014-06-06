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
package unikl.disco.calculator.optimization;

import java.util.HashMap;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;

/**
 * Represents an inverse backlog bound for the given @link Arrival.
 * The arrival and a given violation probability are wrapped into this class which then
 * in turn can be optimized by the provided optimization techniques.
 * @author Sebastian Henningsen
 */
public class InverseBacklogBound implements Optimizable {
    private Arrival input;
    private double violationProb;
    private HashMap<Integer, Hoelder> allHoelders;
    
    /**
     * Creates an inverse backlog bound
     * @param input The arrival to-be-bounded
     * @param violationProb The desired violation probability
     */
    public InverseBacklogBound(Arrival input, double violationProb) {
        this.input = input;
        this.violationProb = violationProb;
        this.allHoelders = new HashMap<>(0);
        allHoelders.putAll(input.getSigma().getParameters());
        allHoelders.putAll(input.getRho().getParameters());
    }
        
    /**
     *
     */
    @Override
    public void prepare() {
        // Remove the parameter that represents the backlog from the other Hoelder parameters
        // TODO: Check this for correctness!
        allHoelders.get(allHoelders.size()).setPValue(0);
	allHoelders.remove(allHoelders.size());
    }

    /**
     *
     * @param theta
     * @return
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     */
    @Override
    public double evaluate(double theta) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
        return ( (-Math.log(violationProb)/theta) + 1/theta*Math.log(input.evaluate(theta, 0, 0)) );
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<Integer, Hoelder> getHoelderParameters() {
        return allHoelders;
    }

    /**
     *
     * @return
     */
    @Override
    public double getMaximumTheta() {
        return input.getThetastar();
    }
    
}
