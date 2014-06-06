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

import unikl.disco.calculator.SNC;

/**
 * This class represents the action to convolute two adjacent vertices in a given target network
 * @author Sebastian Henningsen
 */
public class ConvoluteVerticesCommand implements Command {
     
    int vertex1ID;
    int vertex2ID;
    int networkID;
    SNC snc;
    
    public ConvoluteVerticesCommand(int vertex1ID, int vertex2ID, int networkID, SNC snc) {
        this.vertex1ID = vertex1ID;
        this.vertex2ID = vertex2ID;
        this.networkID = networkID;
    }
    
    @Override
    public void execute() {
        snc.getCurrentNetwork().convolute(vertex1ID, vertex2ID);
    }

    @Override
    public void undo() {
        // TODO
        System.out.println("Undo Action for convolution not implemented yet.");
    }
    
}
