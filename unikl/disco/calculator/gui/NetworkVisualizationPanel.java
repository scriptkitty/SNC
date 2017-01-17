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

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.util.Iterator;
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
 *
 * @author Sebastian Henningsen
 */
public class NetworkVisualizationPanel {

    private final ScrollPane visualizationPanel;
    private Graph<Integer, String> graph;
    private Layout<Integer, String> layout;
    VisualizationViewer<Integer, String> bvs;
    private Dimension size;

    /**
     * Creates the panel.
     *
     * @param size
     */
    public NetworkVisualizationPanel(Dimension size) {
        visualizationPanel = new ScrollPane();
        visualizationPanel.setPreferredSize(size);
        this.size = size;
        graph = new SparseMultigraph();
        graph.addVertex(10);
        graph.addEdge("Derp", 10, 10);
        layout = new CircleLayout(graph);
        layout.setSize(size);
        bvs = new VisualizationViewer<>(layout);
        bvs.setPreferredSize(size);
        bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
        bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
        bvs.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        visualizationPanel.add(bvs);
        SNC.getInstance().registerNetworkListener(new NetworkChangeListener());

    }

    /**
     * Returns the JPanel on which everything is displayed.
     *
     * @return
     */
    public ScrollPane getPanel() {
        return visualizationPanel;
    }

    private class NetworkChangeListener implements NetworkListener {

        private void updateLayout() {
            layout = new CircleLayout<>(graph);
            bvs.setGraphLayout(layout);
            bvs.repaint();
        }

        @Override
        public void vertexAdded(Vertex newVertex) {
            graph.addVertex(newVertex.getID());
            updateLayout();
        }

        @Override
        public void vertexRemoved(Vertex removedVertex) {
            graph.removeVertex(removedVertex.getID());
            updateLayout();
        }

        @Override
        public void flowAdded(Flow newFlow) {
            List<Integer> route = newFlow.getVerticeIDs();
            Iterator<Integer> it = route.iterator();
            int oldId = it.next();
            int newID = 0;
            int i = 0;
            while(it.hasNext()) {
                newID = it.next();
                System.out.println(oldId + " " + newID);
                graph.addEdge(newFlow.getAlias() + i, oldId, newID);
                oldId = newID;
                i++;
            }
            updateLayout();
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

        @Override
        public void clear() {
            graph = new SparseMultigraph<>();
            updateLayout();
        }

    }
}
