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
package unikl.disco.calculator.gui;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedMultigraph;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.NetworkListener;
import unikl.disco.calculator.network.Vertex;

/**
 *
 * @author Sebastian Henningsen
 */
public class NetworkVisualizationPanel {
    private JPanel visualizationPanel;
    private ListenableGraph adapterGraph;
    private Graph networkGraph;
    private JGraph graphVis;
    
    public NetworkVisualizationPanel(Dimension size) {
        visualizationPanel = new JPanel();
        visualizationPanel.setPreferredSize(size);
        networkGraph = new DirectedMultigraph(DefaultEdge.class);
        adapterGraph = new DefaultListenableGraph(networkGraph);
        graphVis = new JGraph(new JGraphModelAdapter(adapterGraph));
        
        visualizationPanel.add(graphVis);
        
        SNC.getInstance().registerNetworkListener(new NetworkChangeListener());
        
    }
    
    public JPanel getPanel() {
        return visualizationPanel;
    }
    
    private class NetworkChangeListener implements NetworkListener {

        @Override
        public void vertexAdded(Vertex newVertex) {
            adapterGraph.addVertex(newVertex);
        }

        @Override
        public void vertexRemoved(Vertex removedVertex) {
            adapterGraph.removeVertex(removedVertex);
        }

        @Override
        public void flowAdded(Flow newFlow) {
            // Missing: First and Last arrow
            List<Integer> route = newFlow.getVerticeIDs();
            Network nw = SNC.getInstance().getNetwork(0);
            for(int i = 0; i < route.size() -1; i++) {
                adapterGraph.addEdge(nw.getVertex(route.get(i)), nw.getVertex(route.get(i + 1)));
            }
        }

        @Override
        public void flowRemoved(Flow removedFlow) {
        }
        
    }
}
