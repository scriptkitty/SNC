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
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
    private Graph<GraphItem, GraphItem> graph;
    private Layout<GraphItem, GraphItem> layout;
    VisualizationViewer<GraphItem, GraphItem> bvs;
    private Dimension size;
    private List<GraphItem> vertices;
    private List<GraphItem> flows;

    /**
     * Creates the panel.
     *
     * @param size
     */
    public NetworkVisualizationPanel(Dimension size) {
        vertices = new LinkedList<>();
        flows = new LinkedList<>();
        visualizationPanel = new ScrollPane();
        visualizationPanel.setPreferredSize(size);
        this.size = size;
        graph = new SparseMultigraph();
        layout = new FRLayout<>(graph);
        layout.setSize(size);
        bvs = new VisualizationViewer<>(layout);
        bvs.setPreferredSize(size);
        bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GraphItem>());
        bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphItem>());
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
            layout = new FRLayout<>(graph);
            bvs.setGraphLayout(layout);
            bvs.repaint();
        }

        @Override
        public void vertexAdded(Vertex newVertex) {
            GraphItem gi = new GraphItem(newVertex.getID(), newVertex.getAlias());
            graph.addVertex(gi);
            vertices.add(gi);
            updateLayout();
        }

        @Override
        public void vertexRemoved(Vertex removedVertex) {
            GraphItem gi = new GraphItem(removedVertex.getID(), removedVertex.getAlias());
            vertices.remove(gi);
            graph.removeVertex(gi);
            // TODO: Handle flows. Is this already covered by flowChanged()?
            updateLayout();
        }

        @Override
        public void flowAdded(Flow newFlow) {
            List<Integer> route = newFlow.getVerticeIDs();
            Iterator<Integer> it = route.iterator();
            int oldId = it.next();
            if (route.size() > 1) {
                int newID = 0;
                int i = 0;
                while (it.hasNext()) {
                    newID = it.next();
                    System.out.println(oldId + " " + newID);
                    GraphItem gi = new GraphItem(newFlow.getID() + i, newFlow.getAlias());
                    flows.add(gi);
                    graph.addEdge(gi, getVertexbyID(oldId), getVertexbyID(newID), EdgeType.DIRECTED);
                    oldId = newID;
                    i++;
                }
            } else {
                GraphItem gi = new GraphItem(newFlow.getID(), newFlow.getAlias());
                graph.addEdge(gi, getVertexbyID(oldId), getVertexbyID(oldId), EdgeType.DIRECTED);
            }
            updateLayout();
        }

        @Override
        public void flowRemoved(Flow removedFlow) {
            List<Integer> route = removedFlow.getVerticeIDs();
            for (int i = 0; i < route.size(); i++) {
                GraphItem gi = getFlowbyID(removedFlow.getID()+i);
                graph.removeEdge(gi);
                flows.remove(gi);
            }
            updateLayout();
            
        }

        @Override
        public void flowChanged(Flow changedFlow) {
            // Only the route is relevant for us
            // Remove the old one and add new ones
            for (GraphItem gi : getFlowbyAlias(changedFlow.getAlias())) {
                graph.removeEdge(gi);
                flows.remove(gi);
            }
            flowAdded(changedFlow);
            
        }

        @Override
        public void vertexChanged(Vertex changedVertex) {
            // Ignore for now
        }

        @Override
        public void clear() {
            graph = new SparseMultigraph<>();
            updateLayout();
        }

        private GraphItem getVertexbyID(int id) {
            GraphItem result = null;
            for (GraphItem gi : vertices) {
                if (gi.getID() == id) {
                    result = gi;
                }
            }
            return result;
        }

        private GraphItem getFlowbyID(int id) {
            GraphItem result = null;
            for (GraphItem gi : flows) {
                if (gi.getID() == id) {
                    result = gi;
                }
            }
            return result;
        }
        
        private List<GraphItem> getFlowbyAlias(String alias) {
            List<GraphItem> result = new LinkedList<>();
            for (GraphItem gi : flows) {
                if (gi.getAlias().equals(alias)) {
                    result.add(gi);
                }
            }
            return result;
        }

    }
}

class GraphItem {

    private final int id;
    private final String alias;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.id;
        hash = 23 * hash + Objects.hashCode(this.alias);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphItem other = (GraphItem) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.alias, other.alias)) {
            return false;
        }
        return true;
    }

    public GraphItem(int id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return alias;
    }

    public int getID() {
        return id;
    }

    public String getAlias() {
        return alias;
    }
}
