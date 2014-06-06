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

import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.symbolic_math.Service;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.Vertex;

/**
 * This class represents the action to add a vertex with given properties in the target network.
 * @author Sebastian Henningsen
 */
public class AddVertexCommand implements Command {
    private String alias;
    double rate;
    int networkID;
    SNC snc;
    boolean success;
    int vertexID;
    
    public AddVertexCommand(String alias, double rate, int networkID, SNC snc) {
        this.alias = alias != null ? alias : "";
        this.rate = rate;
        this.networkID = networkID;
        this.snc = snc;
        this.success = false;
        this.vertexID = -1;
    }
    
    @Override
    public void execute() {
	if(rate > 0) {
	    success = false;
	    // TODO: Introduce a more general framework for asynchronous exception handling
	    throw new IllegalArgumentException("Rate has to be negative.");
	}
	Network nw = snc.getCurrentNetwork();
	vertexID = nw.addVertex(new Service(new ConstantFunction(0), 
				new ConstantFunction(rate), snc.getCurrentNetwork()), alias).getVertexID();
	// Why is this?
	snc.getCurrentNetwork().getVertex(vertexID).getService().getServicedependencies().clear();

	success = true;
    }

    @Override
    public void undo() {
        if(success) {
            snc.getCurrentNetwork().removeVertex(vertexID);
        }
    }
    
}
