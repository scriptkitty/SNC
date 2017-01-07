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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;
import unikl.disco.calculator.network.AbstractAnalysis;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.AbstractAnalysis.Boundtype;

/**
 * This is a simple Brute-Force optimization. It just tests
 * all possible values for every involved Hoelder coefficient
 * and theta. During runtime the current best found bound is
 * given out, as well as information about the progress. To
 * test all possible values for an arbitrary number of 
 * parameters the helper class {@link IncrementList} is used,
 * which extends {@link ArrayList}.
 * @author Michael Beck
 * @author Sebastian Henningsen
 *
 */
public class SimpleOptimizer extends AbstractOptimizer {

    /**
     * Creation is delegated to the @link AbstractOptimizer subclass.
     * @param input
     * @param boundtype
     * @param nw
     */
    public SimpleOptimizer(Optimizable input, Boundtype boundtype, Network nw) {
		super(input, boundtype, nw);
	}
	
        @Override
        public double minimize(double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
            bound.prepare();
            // Initilializes the list of Hoelder-Parameters
            Map<Integer, Hoelder> allparameters = bound.getHoelderParameters();
            IncrementList hoelderlist = new IncrementList(hoeldergranularity);
            for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
                hoelderlist.add(entry.getValue());
            }
            
            for(int i=0; i < hoelderlist.size(); i++){
                hoelderlist.get(i).setPValue(2);
            }
            
            //Initializes further values
            maxTheta = bound.getMaximumTheta();
            System.out.println("Max Theta: " + maxTheta);
            double theta = thetagranularity;

            boolean breakCondition = false;
            
            //Computes initial value
            double optValue;
            double newOptValue;
            try {
                optValue = bound.evaluate(theta);
            } catch(ServerOverloadException e) {
                optValue = Double.POSITIVE_INFINITY;
            }

            while(theta < maxTheta) {
                try {
                    optValue = Math.min(bound.evaluate(theta), optValue);
                    theta += thetagranularity;
                } catch(ServerOverloadException e) {
                    theta += thetagranularity;
                }
            }

            //Resets
            theta = thetagranularity;
            breakCondition = false;

            //Tests Hoelder coefficients in one direction

            while(!breakCondition) {
                breakCondition = !hoelderlist.PDecrement();
                maxTheta = bound.getMaximumTheta();			

                while(theta < maxTheta) {
                    try {
                        optValue = Math.min(optValue, bound.evaluate(theta));
                        theta = theta+thetagranularity;
                    } catch(ServerOverloadException | ThetaOutOfBoundException e) {
                        theta = theta+thetagranularity;
                    }
                }
                theta = thetagranularity;
            }

            //Resets
            breakCondition = false;

            //Tests Hoelder coefficients in other direction

            while(!breakCondition) {
                breakCondition = !hoelderlist.QDecrement();
                maxTheta = bound.getMaximumTheta();

                while(theta < maxTheta) {
                    try {
                        Math.min(optValue, bound.evaluate(theta));
                        theta = theta+thetagranularity;
                    } catch(ServerOverloadException | ThetaOutOfBoundException e) {
                        theta = theta+thetagranularity;
                    }
                }
                theta = thetagranularity;
            }
            return optValue;
        }
        
	@Override
	public double ReverseBound(Arrival input, Boundtype boundtype, double violation_probability, double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		
		double result;
		
		//Initializes the IncrementList of Hoelder-Parameters...
		HashMap<Integer, Hoelder> allparameters = new HashMap<Integer, Hoelder>(0);
		allparameters.putAll(input.getSigma().getParameters());
		allparameters.putAll(input.getRho().getParameters());
		
		//If needed, the parameter, which represents the backlog, must be separated from the other Hoelder parameters
		if(boundtype == AbstractAnalysis.Boundtype.BACKLOG){
			allparameters.get(allparameters.size()).setPValue(0);
			allparameters.remove(allparameters.size());
		}
		
		IncrementList hoelderlist = new IncrementList(hoeldergranularity);
		for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
			hoelderlist.add(entry.getValue());
		}
		
		for(int i=0; i < hoelderlist.size(); i++){
			hoelderlist.get(i).setPValue(2);
		}
		
		//Initializes further values
		double max_theta = input.getThetastar();
		double sigmapart;
		double theta = thetagranularity;
		
		boolean break_condition = false;
		
		switch(boundtype){
			case BACKLOG:
				
				//Computes initial value
				double backlogvalue;
				try{
					sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
					backlogvalue = -Math.log(violation_probability)/theta + sigmapart;
				}
				catch(ServerOverloadException e){
					backlogvalue = Double.MAX_VALUE;
				}
				
				while(theta < max_theta){
					try{
						sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
						backlogvalue = Math.min(backlogvalue, -Math.log(violation_probability)/theta + sigmapart);
						theta = theta+thetagranularity;
					}
					catch(ServerOverloadException e){
						theta = theta+thetagranularity;
					}
				}
				
				//Resets
				theta = thetagranularity;
				break_condition = false;
				
				//Tests Hoelder coefficients in one direction
				
				while(!break_condition){
					break_condition = !hoelderlist.PDecrement();
					max_theta = input.getThetastar();			
					
					while(theta < max_theta){
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							backlogvalue = Math.min(backlogvalue, -Math.log(violation_probability)/theta + sigmapart);
							theta = theta+thetagranularity;
						}
						catch(ServerOverloadException e){
							theta = theta+thetagranularity;
						}
						catch(ThetaOutOfBoundException e){
							theta = theta+thetagranularity;
						}
					}
					
					theta = thetagranularity;
					
				}
				
				//Resets
				break_condition = false;
				
				//Tests Hoelder coefficients in other direction
				
				while(!break_condition){
					break_condition = !hoelderlist.QDecrement();
					max_theta = input.getThetastar();
					
					while(theta < max_theta){
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							backlogvalue = Math.min(backlogvalue, -Math.log(violation_probability)/theta + sigmapart);
							theta = theta+thetagranularity;
						}
						catch(ServerOverloadException e){
							theta = theta+thetagranularity;
						}
						catch(ThetaOutOfBoundException e){
							theta = theta+thetagranularity;
						}
					}
					
					theta = thetagranularity;
					
				}
				
				result = backlogvalue;
				
				break;
				
			case DELAY:
				
				//Computes initial value
				double delayvalue;
				double rhopart;
				try{
					sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
					rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
					delayvalue = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
				}
				catch(ServerOverloadException e){
					delayvalue = Double.MAX_VALUE;
				}
				
				while(theta < max_theta){
					double newvalue;
					try{
						sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
						rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
						newvalue = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
						delayvalue = Math.min(delayvalue, newvalue);
						theta = theta+thetagranularity;
					}
					catch(ServerOverloadException e){
						theta = theta+thetagranularity;
					}
				}
				
				//Resets
				theta = thetagranularity;
				break_condition = false;
				
				//Tests Hoelder coefficients in one direction
				
				while(!break_condition){
					break_condition = !hoelderlist.PDecrement();
					max_theta = input.getThetastar();
					while(theta < max_theta){
						double newvalue;
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							newvalue = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
							delayvalue = Math.min(delayvalue, newvalue);
							theta = theta+thetagranularity;
						}
						catch(ServerOverloadException e){
							theta = theta+thetagranularity;
						}
						catch(ThetaOutOfBoundException e){
							theta = theta+thetagranularity;
						}
					}
					
					theta = thetagranularity;
					
				}
				
				//Resets
				break_condition = false;
				
				//Tests Hoelder coefficients in other direction
				
				while(!break_condition){
					break_condition = !hoelderlist.QDecrement();
					max_theta = input.getThetastar();
					while(theta < max_theta){
						double newvalue;
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							newvalue = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
							delayvalue = Math.min(delayvalue, newvalue);
							theta = theta+thetagranularity;
						}
						catch(ServerOverloadException e){
							theta = theta+thetagranularity;
						}
						catch(ThetaOutOfBoundException e){
							theta = theta+thetagranularity;
						}
					}
					
					theta = thetagranularity;
					
				}
				
				result = delayvalue;
				
				break;
			case OUTPUT:
				//In case of an output-bound no result is needed
				result = Double.NaN;
				break;
			default:
				result = 0;
				break;
		}
		return result;
	}
	
	@Override
	public double Bound(Arrival input, Boundtype boundtype, double bound, double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		
		double result;
		
		//Initializes the IncrementList of Hoelder-Parameters...
		double thetastar = thetagranularity;
		HashMap<Integer, Hoelder> allparameters = new HashMap<Integer, Hoelder>(0);
		allparameters.putAll(input.getSigma().getParameters());
		allparameters.putAll(input.getRho().getParameters());
		
		//If needed, the parameter, which represents the backlog, must be separated from the other Hoelder parameters
		if(boundtype == AbstractAnalysis.Boundtype.BACKLOG){
			allparameters.get(allparameters.size()).setPValue(bound);
			allparameters.remove(allparameters.size());
		}
		
		IncrementList hoelderlist = new IncrementList(hoeldergranularity);
		for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
			hoelderlist.add(entry.getValue());
		}
		
		for(int i=0; i < hoelderlist.size(); i++){
			hoelderlist.get(i).setPValue(2);
		}
		
		//Initializes further values
		double max_theta = input.getThetastar();
		
		System.out.println("max-theta: "+max_theta);
		
		boolean break_condition = false;
		
		switch(boundtype){
			case BACKLOG:
				
				//Computes initial value
				double backlogvalue;
				try{
					backlogvalue = input.evaluate(thetastar, 0, 0);
				}
				catch(ServerOverloadException e){
					backlogvalue = Double.MAX_VALUE;
				}
				
				while(thetastar < max_theta){
					try{
						double newvalue = input.evaluate(thetastar, 0, 0);
						
						if(newvalue < backlogvalue) System.out.println(" theta: "+thetastar+" max-theta: "+ max_theta +" bound: "+newvalue);
						
						backlogvalue = Math.min(backlogvalue, newvalue);
						thetastar = thetastar+thetagranularity;
					}
					catch(ServerOverloadException e){
						thetastar = thetastar+thetagranularity;
					}
				}
				
				//Resets
				thetastar = thetagranularity;
				break_condition = false;
				
				//Tests Hoelder coefficients in one direction
				
				while(!break_condition){
					break_condition = !hoelderlist.PDecrement();
					max_theta = input.getThetastar();			
					
					while(thetastar < max_theta){
						try{
							double newvalue = input.evaluate(thetastar, 0, 0);

							
							if(newvalue < backlogvalue) System.out.println("Hoelder: "+hoelderlist.toString()+" theta: "+thetastar+" max-theta: "+ max_theta +" bound: "+newvalue);
							
							
							backlogvalue = Math.min(backlogvalue, newvalue);
							thetastar = thetastar+thetagranularity;
						}
						catch(ServerOverloadException e){
							thetastar = thetastar+thetagranularity;
						}
					}
					
					thetastar = thetagranularity;
					
				}
				
				//Resets
				break_condition = false;
				
				//Tests Hoelder coefficients in other direction
				
				while(!break_condition){
					break_condition = !hoelderlist.QDecrement();
					max_theta = input.getThetastar();
					
					while(thetastar < max_theta){
						try{
							double newvalue = input.evaluate(thetastar, 0, 0);
							
							
							if(newvalue < backlogvalue) System.out.println("Hoelder: "+hoelderlist.toString()+" theta: "+thetastar+" max-theta: "+ max_theta +" bound: "+newvalue);
							
							
							backlogvalue = Math.min(backlogvalue, newvalue);
							thetastar = thetastar+thetagranularity;
						}
						catch(ServerOverloadException e){
							thetastar = thetastar+thetagranularity;
						}
					}
					
					thetastar = thetagranularity;
					
				}
				
				result = backlogvalue;
				
				break;
				
			case DELAY:
				
				//Computes initial value
				int delay = (int) Math.round(Math.ceil(bound));
				double delayvalue;
				try{
					delayvalue = input.evaluate(thetastar, delay, 0);
				}
				catch(ServerOverloadException e){
					delayvalue = Double.MAX_VALUE;
				}
				
				while(thetastar < max_theta){
					try{
						double newvalue = input.evaluate(thetastar, delay, 0);
						delayvalue = Math.min(delayvalue, newvalue);
						thetastar = thetastar+thetagranularity;
					}
					catch(ServerOverloadException e){
						thetastar = thetastar+thetagranularity;
					}
				}
				
				//Resets
				thetastar = thetagranularity;
				break_condition = false;
				
				//Tests Hoelder coefficients in one direction
				
				while(!break_condition){
					break_condition = !hoelderlist.PDecrement();
					max_theta = input.getThetastar();
					while(thetastar < max_theta){
						try{
							double newvalue = input.evaluate(thetastar, delay, 0);
							delayvalue = Math.min(delayvalue, newvalue);
							thetastar = thetastar+thetagranularity;
						}
						catch(ServerOverloadException e){
							thetastar = thetastar+thetagranularity;
						}
					}
					
					thetastar = thetagranularity;
					
				}
				
				//Resets
				break_condition = false;
				
				//Tests Hoelder coefficients in other direction
				
				while(!break_condition){
					break_condition = !hoelderlist.QDecrement();
					max_theta = input.getThetastar();
					while(thetastar < max_theta){
						try{
							double newvalue = input.evaluate(thetastar, delay, 0);
							delayvalue = Math.min(delayvalue, newvalue);
							thetastar = thetastar+thetagranularity;
						}
						catch(ServerOverloadException e){
							thetastar = thetastar+thetagranularity;
						}
					}
					
					thetastar = thetagranularity;
					
				}
				
				result = delayvalue;
				
				break;
			case OUTPUT:
				//In case of an output-bound no result is needed
				result = Double.NaN;
				break;
			default:
				result = 0;
				break;
		}
		return result;
	}


	
	/**
	 * A helper class, which gives a method to rotate through all
	 * possible Hoelder configurations. The granularity should be 
	 * equal to the step-size of testing Hoelder coefficients. The
	 * two methods <code>PDecrement</code> and <code>QDecrement</code>
	 * add one step-size to the first Hoelder coefficient in the 
	 * array. If this would lead to a value smaller or equal to 1
	 * instead the next Hoelder coefficient is decreased and the 
	 * current coefficient is reset to 2, and so on.
	 * If a decreasing of the last Hoelder coefficient would lead
	 * to value smaller or equal to 1 instead all Hoelder 
	 * coefficients are reset to 2. Further the methods return 
	 * false to inform the calling procedure, that all coefficients
	 * have been gone through.
	 * @author Michael Beck
	 *
	 */
	private class IncrementList extends ArrayList<Hoelder>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1024843637504465612L;
		
		private double granularity;
		
		public IncrementList(double granularity){
			super();
			this.granularity = granularity;
		}
		
		public boolean PDecrement(){
			boolean stop_it = false;
			for(int i = 0; i<this.size(); i++){
				if(!stop_it && this.get(i).getPValue() - granularity > 1){
					this.get(i).setPValue(this.get(i).getPValue()-granularity);
					stop_it = true;
				}
				else if(!stop_it){
					this.get(i).setPValue(2);
				}
			}
			//If all Hoelder-coefficients are reset stop_it is false!
			//In all other cases stop_it is true and a new Hoelder configuration was established.
			return stop_it;
		}
		
		public boolean QDecrement(){
			boolean stop_it = false;
			for(int i = 0; i<this.size(); i++){
				if(!stop_it && this.get(i).getQValue() - granularity > 1){
					this.get(i).setQValue(this.get(i).getQValue()-granularity);
					stop_it = true;
				}
				else if(!stop_it){
					this.get(i).setQValue(2);
				}
			}
			//If all Hoelder-coefficients are reset stop_it is false!
			//In all other cases stop_it is true and a new Hoelder configuration was established.
			return stop_it;
		}
		
		@Override
		public String toString(){
			String output = "[ " + this.get(0).toString();
			if(this.size() > 1){
				for(int i = 1; i<this.size();i++){
					output = output + ", " + this.get(i).toString();
				}
			}
			output = output + "]";
			return output;
			
		}
		
	}

}
