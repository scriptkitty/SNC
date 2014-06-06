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
package unikl.disco.calculator.network;

import java.util.ArrayList;
import java.util.List;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.BadInitializationException;

/**
 *
 * @author Sebastian Henningsen
 */
public class SimpleEndToEndConvolutor {
    
    Flow flowOfInterest;
    
    public SimpleEndToEndConvolutor(Flow flowOfInterest) {
        this.flowOfInterest = flowOfInterest;
    }
    
    // Yields a tree of recursions
    public List<ConvolutionState> computeAllConvolutions(String operations, int vertex1ID, int vertex2ID, Network nw) {
        // For every node on path: 
        // Convolute with right neighbour and apply recursion
        // And subtract flows (if any) and apply recursion
        // Base Case: One Node -> Subtract all flows and return Arrival bound
        List<ConvolutionState> results = new ArrayList<>();
        Arrival bound = null;
        
        if(nw.getVertices().size() >= 2) {
            // Get route of FoI
            List<Integer> route = flowOfInterest.getVerticeIDs();
            for(int i = 0, j = 1;j < route.size() - 1;i++, j++) {
                // Convolution
                Network convNetwork = nw.copy();
                int newID = convNetwork.convolute(route.get(i), route.get(j));
                vertex1ID = vertex1ID == i || vertex1ID == j ? newID : vertex1ID;
                vertex2ID = vertex2ID == i || vertex1ID == j ? newID : vertex2ID;

                results.addAll(computeAllConvolutions(operations + "convolute " + convNetwork.getVertex(i).getAlias() 
                        + " (" + i + "), " + convNetwork.getVertex(j).getAlias() + " (" + j + ")", vertex1ID, vertex2ID, convNetwork));
                
                // Subtract
                Network subtNetwork = nw.copy();
                Vertex current = subtNetwork.getVertex(i);
                if(current.getPrioritizedFlow() != flowOfInterest.getFlowID()) {
                    try {
                        current.serve();
                    } catch (ArrivalNotAvailableException ex) {
                        ex.printStackTrace();
                    }
                    results.addAll(computeAllConvolutions(operations + "subtract " + subtNetwork.getFlow(i).getAlias() 
                        + " (" + i + ")", vertex1ID, vertex2ID, subtNetwork));
                }
            }
        } else {
            // Only one node in network
            results = new ArrayList<>();
            
            Analyzer analyzer = AnalysisFactory.getAnalyzer(AnalysisType.SIMPLE_ANA, nw, nw.getVertices(), nw.getFlows(), flowOfInterest.getFlowID(), vertex1ID, AbstractAnalysis.Boundtype.DELAY);
            try {
                bound = analyzer.analyze();
            } catch (    ArrivalNotAvailableException | DeadlockException | BadInitializationException ex) {
                ex.printStackTrace();
            }
        }
        results.add(new ConvolutionState(operations, bound));
        return results;

      }
        
    // Starts new threads and finds the best result
    public void findBestConvolution() {
        
    }
}
