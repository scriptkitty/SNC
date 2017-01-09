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
package unikl.disco.calculator.symbolic_math;

/**
 * A list of available arrival types
 * @author Sebastian Henningsen
 */
public enum ArrivalType {
    
    CONSTANT_RATE {
       @Override
       public String toString() {
           return "Constant Rate Arrival";
       }
    },
    
    EXPONENTIAL {
        @Override
        public String toString() {
            return "Exponential distributed increments";
        }
    },
    
    POISSON {
        @Override
        public String toString() {
            return "Compound Poisson Arrival";
        }
    },
    
    EBB {
    	@Override
    	public String toString() {
    		return "EBB Arrival";
    	}
    },
    
    STATIONARYTB {
    	@Override
    	public String toString() {
    		return "Stationary TB";
    	}
    }
}
