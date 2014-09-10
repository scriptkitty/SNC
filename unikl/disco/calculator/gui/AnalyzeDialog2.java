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

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.AbstractAnalysis;
import unikl.disco.calculator.network.AnalysisType;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.calculator.optimization.BoundType;

/**
 *
 * @author Sebastian Henningsen
 */
public class AnalyzeDialog2 {

    private JPanel panel;
    private JLabel vertexOfInterest;
    private JLabel flowOfInterest;
    private JLabel analysisType;
    private JLabel boundType;
    private JComboBox<Displayable> vertexSelector;
    private JComboBox<Displayable> flowSelector;
    private JComboBox<AnalysisType> analysisSelector;
    private JComboBox boundSelector;
    private GridLayout layout;

    public AnalyzeDialog2() {
        panel = new JPanel();

        vertexOfInterest = new JLabel("Vertex Of Interest: ");
        flowOfInterest = new JLabel("Flow Of Interest: ");
        analysisType = new JLabel("Analysis Type: ");
        boundType = new JLabel("Boundtype : ");

        vertexSelector = new JComboBox(MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getVertices()));
        flowSelector = new JComboBox(MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getFlows()));

        analysisSelector = new JComboBox(AnalysisType.values());
        boundSelector = new JComboBox(AbstractAnalysis.Boundtype.values());

        layout = new GridLayout(0, 2);
        panel.setLayout(layout);

        panel.add(vertexOfInterest);
        panel.add(vertexSelector);
        panel.add(flowOfInterest);
        panel.add(flowSelector);
        panel.add(analysisType);
        panel.add(analysisSelector);
        panel.add(boundType);
        panel.add(boundSelector);
    }

    public void display() {
        int result = JOptionPane.showConfirmDialog(null, panel, "Analyze Dialog",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Network nw = SNC.getInstance().getCurrentNetwork();
            int flowID = ((Displayable) flowSelector.getSelectedItem()).getID();
            Flow flow = nw.getFlow(flowID);
            int vertexID = ((Displayable) vertexSelector.getSelectedItem()).getID();
            Vertex vertex = nw.getVertex(vertexID);

            SNC.getInstance().analyzeNetwork(flow, vertex, (AnalysisType) analysisSelector.getSelectedItem(),
                    (AbstractAnalysis.Boundtype) boundSelector.getSelectedItem(), nw);
            // Just for debugging
            System.out.println(vertexSelector.getSelectedItem()
                    + " " + flowSelector.getSelectedItem()
                    + " " + analysisSelector.getSelectedItem()
                    + " " + boundSelector.getSelectedItem());
        }
    }
}
