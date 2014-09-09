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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.AnalysisType;
import unikl.disco.calculator.optimization.BoundType;
import unikl.disco.calculator.optimization.OptimizationType;

/**
 *
 * @author Sebastian Henningsen
 */
public class OptimizationDialog {

    private JPanel panel;
    private JLabel vertexOfInterest;
    private JLabel flowOfInterest;
    private JLabel analysisType;
    private JLabel boundType;
    private JLabel hoelderGranularity;
    private JLabel thetaGranularity;
    private JLabel optimizationAlgorithm;
    private JLabel boundValue;
    private JComboBox<Displayable> vertexSelector;
    private JComboBox<Displayable> flowSelector;
    private JComboBox<AnalysisType> analysisSelector;
    private JComboBox<BoundType> boundSelector;
    private JComboBox<OptimizationType> optSelector;
    private JSpinner hoelderGran;
    private JSpinner thetaGran;
    private SpinnerModel hoelderModel;
    private SpinnerModel thetaModel;
    private JTextField valueField;
    private GridLayout layout;
    
    public OptimizationDialog() {
        panel = new JPanel();

        vertexOfInterest = new JLabel("Vertex Of Interest: ");
        flowOfInterest = new JLabel("Flow Of Interest: ");
        analysisType = new JLabel("Analysis Type: ");
        boundType = new JLabel("Bound Type: ");
        hoelderGranularity = new JLabel("Hoelder Granularity: ");
        thetaGranularity = new JLabel("Theta Granularity: ");
        optimizationAlgorithm = new JLabel("Optimization Algorithm: ");
        boundValue = new JLabel("Bound Or Probability: ");

        vertexSelector = new JComboBox<>(MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getVertices()));
        flowSelector = new JComboBox<>(MainWindow.convertDisplayables(SNC.getInstance().getCurrentNetwork().getFlows()));
        analysisSelector = new JComboBox<>(AnalysisType.values());
        boundSelector = new JComboBox<>(BoundType.values());
        optSelector = new JComboBox<>(OptimizationType.values());

        hoelderModel = new SpinnerNumberModel(0.01, 0, 1, 0.001);
        thetaModel = new SpinnerNumberModel(0.01, 0.0, null, 0.001);
        hoelderGran = new JSpinner(hoelderModel);
        thetaGran = new JSpinner(thetaModel);
        
        valueField = new JTextField(10);
        
        layout = new GridLayout(0,2);
        panel.setLayout(layout);

        panel.add(vertexOfInterest);
        panel.add(vertexSelector);
        panel.add(flowOfInterest);
        panel.add(flowSelector);
        panel.add(analysisType);
        panel.add(analysisSelector);
        panel.add(boundType);
        panel.add(boundSelector);
        panel.add(optimizationAlgorithm);
        panel.add(optSelector);
        panel.add(hoelderGranularity);
        panel.add(hoelderGran);
        panel.add(thetaGranularity);
        panel.add(thetaGran);
        panel.add(boundValue);
        panel.add(valueField);
        
    }

    public void display() {
        int result = JOptionPane.showConfirmDialog(null, panel, "Analyze Dialog",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (valueField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "You must specify a bound/probability!");
            } else {
            // Delegate from here
            System.out.println(vertexSelector.getSelectedItem()
                    + " " + flowSelector.getSelectedItem()
                    + " " + analysisSelector.getSelectedItem()
                    + " " + boundSelector.getSelectedItem()
                    + " " + optSelector.getSelectedItem()
                    + " " + (double)(hoelderGran.getModel().getValue())
                    + " " + (double)(thetaGran.getModel().getValue())
                    + " " + Double.parseDouble(valueField.getText()));
            }
        }
    }
}
