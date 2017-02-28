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

import unikl.disco.calculator.SNC;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;
import unikl.disco.calculator.symbolic_math.functions.ExponentialSigma;
import unikl.disco.calculator.symbolic_math.functions.PoissonRho;
import unikl.disco.calculator.symbolic_math.functions.EBBSigma;
import unikl.disco.calculator.symbolic_math.functions.StationaryTBSigma;

/**
 * A class which builds appropriate (sigma, rho) representations based on the
 * respective arrival parameters. Each arrival type (described in @link
 * ArrivalType) has its own function, thus a new ArrivalType has to be added
 * here as well.
 *
 * @author Sebastian Henningsen
 */
public class ArrivalFactory {

    public static Arrival buildConstantRate(double rate) {
        SymbolicFunction sigma = new ConstantFunction(0);
        SymbolicFunction rho = new ConstantFunction(rate);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }

    public static Arrival buildExponentialRate(double rate) throws BadInitializationException {
        SymbolicFunction sigma = new ConstantFunction(0);
        SymbolicFunction rho = new ExponentialSigma(rate);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }

    public static Arrival buildPoissonRate(SymbolicFunction increment_rho, double mu) {
        SymbolicFunction sigma = new ConstantFunction(0);
        SymbolicFunction rho = new PoissonRho(increment_rho, mu);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }

    public static Arrival buildEBB(double rate, double decay, double prefactor) throws BadInitializationException {
        SymbolicFunction sigma = new EBBSigma(decay, prefactor);
        SymbolicFunction rho = new ConstantFunction(rate);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }

    /**
     * See the paper: "Stochastic Majorization of Aggregates of Leaky
     * Bucket-Constrained Traffic Streams" by Laurent Massouli� , Anthony
     * Busson.
     *
     * @param rate: token generation rate of the (aggregated) token bucket
     * @param bucket: bucket size of the (aggregated) token bucket
     * @param maxTheta: (Optional) Gives the maximal theta derived from the
     * original arrivals (before they pass the token bucket). If not used it is
     * set to <code>POSITIVE_INFINITY</code>.
     * @return A bound on (an aggregate of) stationary arrivals passing through
     * a token bucket.
     * @throws BadInitializationException
     */
    public static Arrival buildStationaryTB(double rate, double bucket) throws BadInitializationException {
        SymbolicFunction rho = new ConstantFunction(rate);
        SymbolicFunction sigma = new StationaryTBSigma(bucket);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }

    public static Arrival buildStationaryTB(double rate, double bucket, double maxTheta) throws BadInitializationException {
        SymbolicFunction rho = new ConstantFunction(rate);
        SymbolicFunction sigma = new StationaryTBSigma(bucket, maxTheta);
        return new Arrival(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }
}
