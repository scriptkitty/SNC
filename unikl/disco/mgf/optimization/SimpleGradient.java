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
import java.util.Map;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.Hoelder;
import unikl.disco.mgf.ParameterMismatchException;
import unikl.disco.mgf.ServerOverloadException;
import unikl.disco.mgf.ThetaOutOfBoundException;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.Network;
import unikl.disco.mgf.network.AbstractAnalysis.Boundtype;

/**
 * This is a simple Gradient-search optimization. The search
 * starts with all Hoelder parameters being equal to 2 and a
 * theta equal to the thetagranularity. It then checks all 
 * bounds, which can be obtained by deviating from this point 
 * by "one step" - meaning either a single Hoelder parameter 
 * is changed by the hoeldergranularity or theta is changed 
 * by the thetagranularity.
 * We move then to the position, which delivered the best of
 * these values and repeat the process. If no neighbour can
 * deliver a better result than the current bound, we will 
 * not move and give the current bound as result instead.
 * @author Michael Beck
 *
 */
public class SimpleGradient extends AbstractOptimizer {
	
	enum Change{
		THETA_DEC, THETA_INC, HOELDER_P, HOELDER_Q, NOTHING
	};

	public SimpleGradient(Arrival input, Boundtype boundtype) {
		super(input, boundtype);
		
	}

	@Override
	public double Bound(Arrival input, Boundtype boundtype, double bound, double thetagranularity, double hoeldergranularity)
			throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double ReverseBound(Arrival input, Boundtype boundtype, double violation_probability, double thetagranularity, double hoeldergranularity) throws ThetaOutOfBoundException, ParameterMismatchException, ServerOverloadException {

		double result;
		
		//Initializes the list of Hoelder-Parameters...
		HashMap<Integer, Hoelder> allparameters = new HashMap<Integer, Hoelder>(0);
		allparameters.putAll(input.getSigma().getParameters());
		allparameters.putAll(input.getRho().getParameters());
			
		//If needed, the parameter, which represents the backlog, must be separated from the other Hoelder parameters
		if(boundtype == AbstractAnalysis.Boundtype.BACKLOG){
			allparameters.get(Network.getHOELDER_ID()-1).setPValue(0);
			allparameters.remove(Network.getHOELDER_ID()-1);
		}
		System.out.println("allparameters:"+ allparameters.toString());
		for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
			entry.getValue().setPValue(2);
		}
		
		//Initializes theta
		double max_theta = input.getThetastar();
		double sigmapart;
		double rhopart;
		double theta = thetagranularity;
		int changed_hoelder = Integer.MAX_VALUE;
		boolean improved = true;
		
		Change change = SimpleGradient.Change.NOTHING;
		
		switch(boundtype){
			case BACKLOG:
				
				//Computes initial value
				double backlogvalue;
				double new_backlog;
				try{
					sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
					backlogvalue = -Math.log(violation_probability)/theta + sigmapart;
				}
				catch(ServerOverloadException e){
					backlogvalue = Double.POSITIVE_INFINITY;
				}
				while(improved){
					improved = false;
					change = SimpleGradient.Change.NOTHING;
					//Check if decreasing theta leads to a better result
					if(theta > thetagranularity){
						
						theta = theta - thetagranularity;
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							new_backlog = -Math.log(violation_probability)/theta + sigmapart;
						}
						catch(ServerOverloadException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						if(backlogvalue > new_backlog){
							backlogvalue = new_backlog;
							change = SimpleGradient.Change.THETA_DEC;
						}
						
						theta = theta + thetagranularity;
					}
					
					//Check if increasing theta leads to a better result
					if(theta < max_theta - thetagranularity){
						theta = theta + thetagranularity;
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							new_backlog = -Math.log(violation_probability)/theta + sigmapart;
						}
						catch(ServerOverloadException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						if(backlogvalue > new_backlog){
							backlogvalue = new_backlog;
							change = SimpleGradient.Change.THETA_INC;
						}
						
						theta = theta - thetagranularity;
					}
					
					//Check each neighbors resulting from decreasing the P-Value of Hoelder parameters
					for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
						double old_p_value = entry.getValue().getPValue();
						if(entry.getValue().getPValue() < 2){
							entry.getValue().setPValue(-hoeldergranularity + entry.getValue().getPValue());
						}
						else{
							entry.getValue().setQValue(hoeldergranularity + entry.getValue().getQValue());
						}
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							new_backlog = -Math.log(violation_probability)/theta + sigmapart;
						}
						catch(ServerOverloadException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						if(backlogvalue > new_backlog){
							backlogvalue = new_backlog; 
							changed_hoelder = entry.getKey();
							change = SimpleGradient.Change.HOELDER_P;
						}
						
						entry.getValue().setPValue(old_p_value);
					}
					
					//Check each neighbor by decreasing the Q-Value of Hoelder parameters
					for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
						double old_q_value = entry.getValue().getQValue();
						if(entry.getValue().getPValue() < 2){
							entry.getValue().setPValue(hoeldergranularity + entry.getValue().getPValue());
						}
						else{
							entry.getValue().setQValue(-hoeldergranularity + entry.getValue().getQValue());
						}
						entry.getValue().setQValue(-hoeldergranularity + entry.getValue().getQValue());
						try{
							sigmapart = 1/theta*Math.log(input.evaluate(theta, 0, 0));
							new_backlog = -Math.log(violation_probability)/theta + sigmapart;
						}
						catch(ServerOverloadException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_backlog = Double.POSITIVE_INFINITY;
						}
						if(backlogvalue > new_backlog){
							backlogvalue = new_backlog; 
							changed_hoelder = entry.getKey();
							change = SimpleGradient.Change.HOELDER_Q;
						}
						
						entry.getValue().setQValue(old_q_value);
					}
					
					switch(change){
					case THETA_INC:
						theta = theta + thetagranularity;
						improved = true;
						break;
					case THETA_DEC:
						theta = theta - thetagranularity;
						improved = true;
						break;
					case HOELDER_P:
						allparameters.get(changed_hoelder).setPValue(allparameters.get(changed_hoelder).getPValue() - hoeldergranularity);
						improved = true;
						break;
					case HOELDER_Q:
						allparameters.get(changed_hoelder).setQValue(allparameters.get(changed_hoelder).getQValue() - hoeldergranularity);
						improved = true;
						break;
					case NOTHING:
						improved = false;
						break;
					default:
						improved = false;
						break;
					}
					System.out.println("Theta: "+theta+" Hoelder: "+allparameters.toString()+" Bound: "+backlogvalue);
				}
				
				result = backlogvalue;
				
				break;
				
			case DELAY:
				
				//Computes initial value
				double delayvalue;
				double new_delay;
				try{
					sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
					rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
					delayvalue = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
				}
				catch(ServerOverloadException e){
					delayvalue = Double.POSITIVE_INFINITY;
				}
					
				while(improved){
					improved = false;
					change = SimpleGradient.Change.NOTHING;
					//Check if decreasing theta leads to a better result
					if(theta > thetagranularity){
						theta = theta - thetagranularity;
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							new_delay = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
						}
						catch(ServerOverloadException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						if(delayvalue > new_delay){
							delayvalue = new_delay;
							change = SimpleGradient.Change.THETA_DEC;
						}
						theta = theta + thetagranularity;
					}
					
					//Check if increasing theta leads to a better result
					if(theta < max_theta - thetagranularity){
						theta = theta + thetagranularity;
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							new_delay = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
						}
						catch(ServerOverloadException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						if(delayvalue > new_delay){
							delayvalue = new_delay;
							change = SimpleGradient.Change.THETA_INC;
						}
						theta = theta - thetagranularity;
					}
					
					//Check each neighbor by decreasing the P-Value of Hoelder parameters
					for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
						double old_p_value = entry.getValue().getPValue();
						if(entry.getValue().getPValue() < 2){
							entry.getValue().setPValue(-hoeldergranularity + entry.getValue().getPValue());
						}
						else{
							entry.getValue().setQValue(hoeldergranularity + entry.getValue().getQValue());
						}
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							new_delay = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
						}
						catch(ServerOverloadException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						if(delayvalue > new_delay){
							delayvalue = new_delay;
							changed_hoelder = entry.getKey();
							change = SimpleGradient.Change.HOELDER_P;
						}
						entry.getValue().setPValue(old_p_value);
					}
					
					//Check each neighbor by decreasing the Q-Value of Hoelder parameters
					for(Map.Entry<Integer, Hoelder> entry : allparameters.entrySet()){
						double old_q_value = entry.getValue().getQValue();
						if(entry.getValue().getPValue() < 2){
							entry.getValue().setPValue(hoeldergranularity + entry.getValue().getPValue());
						}
						else{
							entry.getValue().setQValue(-hoeldergranularity + entry.getValue().getQValue());
						}
						try{
							sigmapart = input.getSigma().getValue(theta, input.getSigma().getParameters());
							rhopart = input.getRho().getValue(theta, input.getRho().getParameters());
							new_delay = -1/rhopart*(-Math.log(violation_probability)/theta + sigmapart);
						}
						catch(ServerOverloadException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						catch(ThetaOutOfBoundException e){
							new_delay = Double.POSITIVE_INFINITY;
						}
						if(delayvalue > new_delay){
							delayvalue = new_delay;
							changed_hoelder = entry.getKey();
							change = SimpleGradient.Change.HOELDER_Q;
						}
						entry.getValue().setQValue(old_q_value);
					}
					
					switch(change){
					case THETA_INC:
						theta = theta + thetagranularity;
						improved = true;
						break;
					case THETA_DEC:
						theta = theta - thetagranularity;
						improved = true;
						break;
					case HOELDER_P:
						allparameters.get(changed_hoelder).setPValue(allparameters.get(changed_hoelder).getPValue() + hoeldergranularity);
						improved = true;
						break;
					case HOELDER_Q:
						allparameters.get(changed_hoelder).setQValue(allparameters.get(changed_hoelder).getQValue() + hoeldergranularity);
						improved = true;
						break;
					case NOTHING:
						improved = false;
						break;
					default:
						improved = false;
						break;
					}
					System.out.println("Theta: "+theta+" Hoelder: "+allparameters.toString()+" Bound: "+delayvalue);
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

}
