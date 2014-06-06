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
package unikl.disco.misc.commands;

import java.util.List;
import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.SNC;
import unikl.disco.mgf.network.ArrivalNotAvailableException;

/**
 * This class represents the action to add a vertex with given properties in the target network.
 * @author Sebastian Henningsen
 */
public class AddFlowCommand implements Command {
    private String alias;
    int networkID;
    SNC snc;
    boolean success;
    int flowID;
    Arrival arrival;
    List<Integer> route;
    List<Integer> priorities;
    
    public AddFlowCommand(String alias, Arrival arrival, List<Integer> route, List<Integer> priorities, int networkID, SNC snc) {
        this.alias = alias != null ? alias : "";
        this.networkID = networkID;
        this.snc = snc;
        this.success = false;
	this.arrival = arrival;
	this.route = route;
	this.priorities = priorities;
    }
    
    @Override
    public void execute() {
	try {
	    flowID = snc.getCurrentNetwork().addFlow(arrival, route, priorities, alias);
	    // TODO: Why is this?
	    snc.getCurrentNetwork().getFlow(flowID).getInitialArrival().getArrivaldependencies().clear();

	} catch (ArrivalNotAvailableException ex) {
	    ex.printStackTrace();
	    success = false;
	}

    }

    @Override
    public void undo() {
        if(success) {
            snc.getCurrentNetwork().removeFlow(snc.getCurrentNetwork().getFlow(flowID));
        }
    }
    
}
