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
package unikl.disco.calculator.gui;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.commands.Command;
import unikl.disco.calculator.commands.ConvoluteVerticesCommand;
import unikl.disco.calculator.network.Network;

/**
 * A dialog to get input from the user in order to convolute two vertices.
 *
 * @author Sebastian Henningsen
 */
public class ConvoluteVerticesDialog {

    private final JPanel panel;
    private final JLabel vertex1;
    private final JLabel vertex2;
    private final JLabel flow;
    private final JComboBox<Displayable> vertex1Chooser;
    private final JComboBox<Displayable> vertex2Chooser;
    private final JComboBox<Displayable> flowChooser;

    private final GridLayout layout;

    /**
     * Constructs the dialog and initializes all necessary fields.
     */
    public ConvoluteVerticesDialog() {
        panel = new JPanel();
        Network nw = SNC.getInstance().getCurrentNetwork();

        vertex1 = new JLabel("Vertex 1: ");
        vertex2 = new JLabel("Vertex 2: ");
        flow = new JLabel("Flow of interest: ");

        vertex1Chooser = new JComboBox(MainWindow.convertDisplayables(nw.getVertices()));
        vertex2Chooser = new JComboBox(MainWindow.convertDisplayables(nw.getVertices()));
        flowChooser = new JComboBox(MainWindow.convertDisplayables(nw.getFlows()));

        panel.add(vertex1);
        panel.add(vertex1Chooser);
        panel.add(vertex2);
        panel.add(vertex2Chooser);
        panel.add(flow);
        panel.add(flowChooser);

        layout = new GridLayout(0, 1);
        panel.setLayout(layout);
    }

    /**
     * Displays the dialog
     */
    public void display() {
        int result = JOptionPane.showConfirmDialog(null, panel, "Convolute vertices",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            SNC snc = SNC.getInstance();
            int vertex1ID = ((Displayable) vertex1Chooser.getSelectedItem()).getID();
            int vertex2ID = ((Displayable) vertex2Chooser.getSelectedItem()).getID();
            int flowID = ((Displayable) flowChooser.getSelectedItem()).getID();
            // v1, v2, flow, nw, snc
            Command cmd = new ConvoluteVerticesCommand(vertex1ID, vertex2ID, flowID, -1, SNC.getInstance());
            snc.invokeCommand(cmd);
            // Just for debugging
            //System.out.println(aliasField.getText());
        }
    }
}
