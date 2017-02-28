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

import java.util.HashMap;
import java.util.Map;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;

/**
 * Represents a backlog bound for the given @link Arrival.
 * The arrival and a given backlog value are wrapped into this class which then
 * in turn can be optimized by the provided optimization techniques.
 * @author Sebastian Henningsen
 * @author Michael Beck
 */
public class BacklogBound implements Optimizable {
    
    private Arrival input;
    private double bound;
    private Map<Integer, Hoelder> allHoelders;
    
    /**
     * Creates a backlog bound 
     * @param input The arrival which shall be bounded
     * @param bound The bound value for backlog
     */
    public BacklogBound(Arrival input, double bound) {
        this.input = input;
        this.bound = bound;
        this.allHoelders = new HashMap<>(0);
        allHoelders.putAll(input.getSigma().getParameters());
        allHoelders.putAll(input.getRho().getParameters());

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
        return (input.evaluate(theta, 0, 0));
    }

    /**
     *
     */
    @Override
    public void prepare() {
        // Remove the parameter that represents the backlog from the other Hoelder parameters
        allHoelders.get(allHoelders.size()).setPValue(bound);
	allHoelders.remove(allHoelders.size());
    }

    /**
     *
     * @return
     */
    @Override
    public Map<Integer, Hoelder> getHoelderParameters() {
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
