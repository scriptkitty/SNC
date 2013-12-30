/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unikl.disco.mgf.network;

import java.util.HashMap;

/**
 *
 * @author sebi
 */
public class AnalysisFactory {
    
    public static AbstractAnalysis getAnalyzer(AnalysisType type, Network nw, HashMap<Integer, Vertex> vertices, HashMap<Integer, Flow> flows, int flow_of_interest, int vertex_of_interest, AbstractAnalysis.Boundtype boundtype) {
        switch(type) {
            case SIMPLE_ANA:
                return new SimpleAnalysis(nw, vertices, flows, flow_of_interest, vertex_of_interest, boundtype);
            default:
                throw new IllegalArgumentException("Analysis Type: " + type.toString() + " not known.");
        }
    }
}
