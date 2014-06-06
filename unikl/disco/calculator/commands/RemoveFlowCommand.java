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
package unikl.disco.calculator.commands;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import unikl.disco.calculator.SNC;

/**
 * This class represents the action to add a vertex with given properties in the target network.
 * @author Sebastian Henningsen
 */
public class RemoveFlowCommand implements Command {
    int networkID;
    SNC snc;
    int flowID;
    
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
	throw new NotImplementedException();
    }
    
}
