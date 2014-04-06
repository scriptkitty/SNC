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

import unikl.disco.mgf.BadInitializationException;
import unikl.disco.mgf.SNC;
import unikl.disco.mgf.Service;
import unikl.disco.mgf.ZeroFunction;
import unikl.disco.mgf.network.Vertex;
import unikl.disco.mgf.rateSigma;

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
        try {
            // TODO: Make sure that rate is negative
            vertexID = snc.addVertex(
                    new Vertex(-1, 
                            new Service(new ZeroFunction(), 
                                    new rateSigma(rate), snc.getCurrentNetwork()), 
                            alias, snc.getCurrentNetwork()), 
                    snc.getCurrentNetwork());
            success = true;
        } catch (BadInitializationException ex) {
            System.out.println("Creation of vertex failed.");
        }
    }

    @Override
    public void undo() {
        if(success) {
            snc.removeVertex(vertexID, networkID);
        }
    }
    
}
