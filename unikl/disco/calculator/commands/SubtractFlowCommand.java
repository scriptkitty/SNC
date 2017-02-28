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
import unikl.disco.calculator.network.ArrivalNotAvailableException;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.misc.NetworkActionException;

/**
 * Computes the left-over-service at the given vertex
 * @author Sebastian Henningsen
 */
public class SubtractFlowCommand implements Command {
    
    private final int vertexID;
    private final int networkID;
    private final SNC snc;
    /**
     * Creates a new SubtractFlowCommand 
     * @param vertexID The ID of the vertex at which the left-over-service should be computed
     * @param networkID The ID of the network the vertex belongs to
     * @param snc The overall controller
     */
    public SubtractFlowCommand(int vertexID, int networkID, SNC snc) {
        this.vertexID = vertexID;
        this.networkID = networkID;
        this.snc = snc;
    }
    
    @Override
    public void execute() {
        try {
            snc.getCurrentNetwork().computeLeftoverService(vertexID);
        } catch (ArrivalNotAvailableException e) {
            throw new NetworkActionException("Error while computing leftover service of vertex " + snc.getCurrentNetwork().getVertex(vertexID).getAlias() + ": " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        // TODO
    }
    
}
