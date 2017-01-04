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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.commands.Command;
import unikl.disco.calculator.commands.ConvoluteVerticesCommand;
import unikl.disco.calculator.commands.RemoveFlowCommand;
import unikl.disco.calculator.commands.RemoveVertexCommand;
import unikl.disco.calculator.commands.SubtractFlowCommand;
import unikl.disco.calculator.network.Network;

/**
 * This static factory serves as collection of all actions corresponding to the {@link ControlPanel}.
 * @author Sebastian Henningsen
 */
public class ControlPanelActions {

    static class AddNodeAction extends AbstractAction {

        public AddNodeAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            AddVertexDialog dialog = new AddVertexDialog();
            dialog.display();
        }
    }

    static class RemoveNodeAction extends AbstractAction {

        public RemoveNodeAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            Displayable[] selectables = MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getVertices());
            if (selectables.length != 0) {
                Displayable d = (Displayable) JOptionPane.showInputDialog(
                        null,
                        "Please Choose a vertex: ",
                        "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        selectables,
                        selectables[0]);
                if (d != null) {
                    Command cmd = new RemoveVertexCommand(d.getID(), -1, SNC.getInstance());
                    SNC.getInstance().invokeCommand(cmd);
                    System.out.println(d.getID() + " " + d.getAlias());
                } else {
                    System.out.println("No vertex selected");
                }
            } else {
                JOptionPane.showMessageDialog(null, "There are no vertices in the network!");
            }
        }
    }

    static class RemoveFlowAction extends AbstractAction {

        public RemoveFlowAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Show a dialog where the user can select a flow that will be removed
            // Box from a Map of IDs and Flows to an Array of Displayables which is needed for
            // the JComboBox
            Displayable[] selectables = MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getFlows());
            if (selectables.length != 0) {
                Displayable d = (Displayable) JOptionPane.showInputDialog(
                        null,
                        "Please choose a flow: ",
                        "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        selectables,
                        selectables[0]);
                if (d != null) {
                    Command cmd = new RemoveFlowCommand(d.getID(), -1, SNC.getInstance());
                    SNC.getInstance().invokeCommand(cmd);
                    System.out.println(d.getID() + " " + d.getAlias());
                } else {
                    System.out.println("No flow selected.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "There are no flows in the network!");
            }
        }
    }

    static class AddFlowAction extends AbstractAction {

        public AddFlowAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            Network nw = SNC.getInstance().getCurrentNetwork();
            FlowEditor dialog = new FlowEditor("Add Flow", nw.getVertices(), nw, SNC.getInstance());
            dialog.showFlowEditor();
            //AddFlowDialog dialog = new AddFlowDialog();
            //dialog.display();
            
        }
    }

    static class AnalyzeNetworkAction extends AbstractAction {

        public AnalyzeNetworkAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            AnalyzeDialog dialog = new AnalyzeDialog();
            dialog.display();
        }
    }

    static class OptimizationAction extends AbstractAction {

        public OptimizationAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            OptimizationDialog dialog = new OptimizationDialog();
            dialog.display();
        }
    }
    
    static class SubtractAction extends AbstractAction {

        public SubtractAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Choose vertex and compute left over service
            // Box from a Map of IDs and Vertices to an Array of Displayables which is needed for
            // the JComboBox
            Displayable[] selectables = MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getVertices());
            if (selectables.length != 0) {
                Displayable d = (Displayable) JOptionPane.showInputDialog(
                        null,
                        "Please choose a vertex: ",
                        "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        selectables,
                        selectables[0]);
                if (d != null) {
                    Command cmd = new SubtractFlowCommand(d.getID(), -1, SNC.getInstance());
                    SNC.getInstance().invokeCommand(cmd);
                } else {
                    System.out.println("No vertex selected.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "There are no flows in the network!");
            }
        }
    }
    
    static class ConvoluteAction extends AbstractAction {

        public ConvoluteAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Choose vertices and flow of interest
            ConvoluteVerticesDialog dialog = new ConvoluteVerticesDialog();
            dialog.display();
        }
    }
}
