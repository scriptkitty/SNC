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
package unikl.disco.calculator.commands;

import unikl.disco.calculator.SNC;
import unikl.disco.misc.NotImplementedException;

/**
 * Removes a flow ({@link Flow}) from a network.
 * @author Sebastian Henningsen
 */
public class RemoveFlowCommand implements Command {
    int networkID;
    SNC snc;
    int flowID;
    
    /**
     * Constructs a new Command to remove a flow from a network.
     * @param flowID The ID of the flow
     * @param networkID The ID of the network the flow belongs to
     * @param snc The overall controller
     */
    public RemoveFlowCommand(int flowID, int networkID, SNC snc) {
        this.networkID = networkID;
        this.snc = snc;
	this.flowID = flowID;
    }
    
    @Override
    public void execute() {
	snc.getCurrentNetwork().removeFlow(snc.getCurrentNetwork().getFlow(flowID));
    }

    @Override
    public void undo() {
	throw new NotImplementedException("Undo Operation for RemoveFlowCommand is not implemented yet.");
    }
    
}
