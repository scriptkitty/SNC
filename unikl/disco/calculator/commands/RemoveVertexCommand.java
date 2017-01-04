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
 * Remove a vertex ({@link Vertex}) from a network.
 * @author Sebastian Henningsen
 */
public class RemoveVertexCommand implements Command {
    int networkID;
    SNC snc;
    int vertexID;
    
    /**
     * Constructs a new command to remove a vertex from a network.
     * @param vertexID The vertex ID
     * @param networkID The network ID the vertex belongs to
     * @param snc The overall controller
     */
    public RemoveVertexCommand(int vertexID, int networkID, SNC snc) {
        this.networkID = networkID;
        this.snc = snc;
	this.vertexID = vertexID;
    }
    
    @Override
    public void execute() {
	snc.getCurrentNetwork().removeVertex(snc.getCurrentNetwork().getVertex(vertexID));
    }

    @Override
    public void undo() {
	throw new NotImplementedException();
    }
    
}
