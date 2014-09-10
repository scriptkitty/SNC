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

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.NetworkListener;
import unikl.disco.calculator.network.Vertex;

/**
 *
 * @author Sebastian Henningsen
 */
public class VertexTablePanel {

    private final JScrollPane scrollPane;
    private final JTable table;
    private final DefaultTableModel tableModel;

    VertexTablePanel() {
        String[] colNames = {"ID", "Name", "Service"}; // Flows entry missing
        tableModel = new DefaultTableModel(colNames, 0);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        SNC.getInstance().registerNetworkListener(new NetworkChangeListener());
    }

    public JScrollPane getPanel() {
        return scrollPane;
    }

    private class NetworkChangeListener implements NetworkListener {

        @Override
        public void vertexAdded(Vertex newVertex) {
            Object[] data = {newVertex.getID(), newVertex.getAlias(), newVertex.getService()};
            tableModel.addRow(data);
        }

        @Override
        public void vertexRemoved(Vertex removedVertex) {
            int id;
            for (int i = 0;i < tableModel.getRowCount(); i++) {
                id = (Integer)tableModel.getValueAt(i, 0);
                if(id == removedVertex.getID()) {
                    tableModel.removeRow(i);
                    break;
                }
            }
        }

        @Override
        public void flowAdded(Flow newFlow) {
            // Not of concern for us
        }

        @Override
        public void flowRemoved(Flow removedFlow) {
            // Not of concern for us
        }

        @Override
        public void flowChanged(Flow changedFlow) {
            // Not of concern for us
        }

        @Override
        public void vertexChanged(Vertex changedVertex) {
            Object[] data = {changedVertex.getID(), changedVertex.getAlias(), changedVertex.getService()};

            int id;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                id = (Integer) tableModel.getValueAt(i, 0);
                if (id == changedVertex.getID()) {
                    tableModel.removeRow(i);
                    tableModel.insertRow(i, data);
                    break;
                }
            }
        }

    }
}
