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
import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.Hoelder;
import unikl.disco.mgf.ParameterMismatchException;
import unikl.disco.mgf.ServerOverloadException;
import unikl.disco.mgf.ThetaOutOfBoundException;

/**
 *
 * @author Sebastian Henningsen
 */
public class InverseDelayBound implements Optimizable {
    private Arrival input;
    private int violationProb;
    private HashMap<Integer, Hoelder> allHoelders;
    
    public InverseDelayBound(Arrival input, double bound) {
        this.input = input;
        this.violationProb = (int)Math.round(Math.ceil(bound));
        this.allHoelders = new HashMap<>(0);
        allHoelders.putAll(input.getSigma().getParameters());
        allHoelders.putAll(input.getRho().getParameters());
    }
    
    @Override
    public void prepare() {
        // Nothing to do
    }

    @Override
    public double evaluate(double theta) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
        double sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
        double rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
	return -1/rhopart*(-Math.log(violationProb)/theta + sigmapart);
    }

    @Override
    public HashMap<Integer, Hoelder> getHoelderParameters() {
        return allHoelders;
    }

    @Override
    public double getMaximumTheta() {
        return input.getThetastar();
    }
    
}
