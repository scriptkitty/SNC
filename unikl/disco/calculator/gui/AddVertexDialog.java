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
import javax.swing.JTextField;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.commands.AddVertexCommand;
import unikl.disco.calculator.commands.Command;
import unikl.disco.calculator.symbolic_math.ServiceFactory;
import unikl.disco.calculator.symbolic_math.ServiceType;
import unikl.disco.misc.NetworkActionException;

/**
 * A dialog to get input from the user in order to add a vertex to a network.
 * WARNING: This class is outdated. Do not use.
 * @author Sebastian Henningsen
 * @author Michael Beck
 */
public class AddVertexDialog {

    private final JPanel panel;
    private final JLabel alias;
    private final JLabel service;
    private final JLabel rate;
    private final JTextField aliasField;
    private final JTextField rateField;
    private final JComboBox<ServiceType> serviceTypes;
    private final GridLayout layout;

    /**
     * Constructs the dialog and initializes all necessary fields.
     */
    public AddVertexDialog() {
        panel = new JPanel();
        
        alias = new JLabel("Alias of the vertex: ");
        service = new JLabel("Service Type: ");
        rate = new JLabel("Rate: ");
        aliasField = new JTextField();
        rateField = new JTextField(10);
       
        serviceTypes = new JComboBox<>(ServiceType.values());
        
        panel.add(alias);
        panel.add(aliasField);
        panel.add(service);
        panel.add(serviceTypes);
        panel.add(rate);
        panel.add(rateField);
        
        layout = new GridLayout(0, 1);
        panel.setLayout(layout);
    }

    /**
     * Displays the dialog
     */
    public void display() {
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Vertex",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            SNC snc = SNC.getInstance();
            double serviceRate = Double.parseDouble(rateField.getText());
            Command cmd = new AddVertexCommand(aliasField.getText(), ServiceFactory.buildConstantRate(serviceRate), -1, snc);
            try {
            snc.invokeCommand(cmd);
            } catch(NetworkActionException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
