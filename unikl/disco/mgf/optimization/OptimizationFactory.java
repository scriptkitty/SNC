/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unikl.disco.mgf.optimization;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.Network;

/**
 *
 * @author sebi
 */
public class OptimizationFactory {
    
    public static AbstractOptimizer getOptimizer(Network nw, Arrival bound, AbstractAnalysis.Boundtype boundtype, OptimizationType type) {
        switch(type) {
            case SIMPLE_OPT:
                return new SimpleOptimizer(bound, boundtype, nw);
            case GRADIENT_OPT:
                return new SimpleGradient(bound, boundtype, nw);
            default:
                throw new IllegalArgumentException("Optimization Type: " + type.toString() + " not known.");
        }
    }
}
