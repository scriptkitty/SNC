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

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.NetworkListener;
import unikl.disco.calculator.network.Vertex;

/**
 * A panel which uses a graph library to display a network.
 * @author Sebastian Henningsen
 */
public class NetworkVisualizationPanel {

    private final JPanel visualizationPanel;
    private final mxGraph graph;

    /**
     * Creates the panel.
     * @param size
     */
    public NetworkVisualizationPanel(Dimension size) {
        visualizationPanel = new JPanel();
        visualizationPanel.setPreferredSize(size);

        graph = new mxGraph();
        graph.setAllowDanglingEdges(true);
        graph.setCellsEditable(false);
        graph.setCellsDeletable(false);
        graph.setCellsDisconnectable(false);
        graph.setEdgeLabelsMovable(false);
        
        /*Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
                    30);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
                    80, 30);
            graph.insertEdge(parent, null, "Edge", v1, v2);
            Object edge = graph.createEdge(parent, null, "Test", null, null, null);
            graph.addEdge(edge, null, null, v1, null);
        } finally {
            graph.getModel().endUpdate();
        }*/
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        visualizationPanel.add(graphComponent);

        SNC.getInstance().registerNetworkListener(new NetworkChangeListener());

    }

    /**
     * Returns the JPanel on which everything is displayed.
     * @return
     */
    public JPanel getPanel() {
        return visualizationPanel;
    }

    private class NetworkChangeListener implements NetworkListener {

        @Override
        public void vertexAdded(Vertex newVertex) {
        }

        @Override
        public void vertexRemoved(Vertex removedVertex) {
        }

        @Override
        public void flowAdded(Flow newFlow) {
        }

        @Override
        public void flowRemoved(Flow removedFlow) {
        }

        @Override
        public void flowChanged(Flow changedFlow) {
        }

        @Override
        public void vertexChanged(Vertex changedVertex) {
        }

    }
}
