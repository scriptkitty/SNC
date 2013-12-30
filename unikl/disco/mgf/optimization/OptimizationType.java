/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unikl.disco.mgf.optimization;

/**
 *
 * @author sebi
 */
public enum OptimizationType {
    GRADIENT_OPT {
        @Override
        public String toString() {
            return "Gradient Heuristic";
	}
    },
    SIMPLE_OPT {
        @Override
	public String toString() {
            return "Simple Optimization";
	}
    }
		
}
