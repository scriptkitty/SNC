package unikl.disco.calculator.symbolic_math;

import unikl.disco.calculator.SNC;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;

/**
 * @author Michael Beck
 * @author Sebastian Henningsen
 */
public class ServiceFactory {
    public static Service buildConstantRate(double rate){
        SymbolicFunction sigma = new ConstantFunction(0);
        //Important note: a service with constant rate "r" has as rho-function "-r"!
        SymbolicFunction rho = new ConstantFunction(-rate);
        return new Service(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }
    /**
     * This server is used in systems in which the data arrives over an interval instead of 
     * single bursts at each time unit. The analysis effectively reduces to have one
     * additional time-frame of service available as the server starts working already when 
     * the first arrivals drip in, instead of waiting for the whole batch to arrive instantaneously.
     * @param rate
     * @return
     * @throws BadInitializationException
     */
    public static Service buildContApproximation(double rate) throws BadInitializationException {
        if (rate < 0) {
            throw new BadInitializationException("Constant rate server: Rate needs to be greater than zero.", rate);
        }
        SymbolicFunction sigma = new ConstantFunction(-rate);
        SymbolicFunction rho = new ConstantFunction(-rate);
        return new Service(sigma, rho, SNC.getInstance().getCurrentNetwork());
    }
}
