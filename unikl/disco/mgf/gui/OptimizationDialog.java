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
package unikl.disco.mgf.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import unikl.disco.mgf.SNC;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.AnalysisType;
import unikl.disco.mgf.optimization.BoundType;
import unikl.disco.mgf.optimization.OptimizationType;

/**
 *
 * @author Sebastian Henningsen
 */
public class OptimizationDialog extends AbstractDialog {
    OptimizationDialog(String title, final SNC snc) {
        super(title, snc);
		
        //***************************************
        //Creates the granularity panel
        //Includes spinners for theta and hoelder
        //***************************************
        JPanel granPanel = new JPanel();
        granPanel.setSize(400, 400);
        add(granPanel);
		
        granPanel.setLayout(new GridLayout(0,2));

        //Creates the spinners for choosing the granularities
        JLabel hoelderLabel = new JLabel("Hölder-Granularity");
        granPanel.add(hoelderLabel);

        SpinnerNumberModel hoeldermodel = new SpinnerNumberModel(0.01, 0, 1, 0.001);
        final JSpinner hoelder = new JSpinner(hoeldermodel);
        granPanel.add(hoelder);

        JLabel thetaLabel = new JLabel("Theta-Granularity");
        granPanel.add(thetaLabel);

        SpinnerNumberModel thetamodel = new SpinnerNumberModel(0.01, 0.0, null, 0.001);
        final JSpinner theta = new JSpinner(thetamodel);
        granPanel.add(theta);
		
        //Creates a dummy label
        add(new JLabel());
		
        //***************************************************
        //Creates the left side panel 
        //Includes the optimization type and flow of interest
        //***************************************************
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(0,2));
		
        JLabel optiLabel = new JLabel("Optimization Type:");
        leftPanel.add(optiLabel);

        final JComboBox<OptimizationType> optiBox = new JComboBox<>(OptimizationType.values());
        leftPanel.add(optiBox);

        JLabel FOILabel = new JLabel("Flow of interest:");
        leftPanel.add(FOILabel);
		
        final JComboBox<ComboBoxItem> FOIBox = createComboBox(snc.getFlows());
	leftPanel.add(FOIBox);
		
        add(leftPanel);
		
        //*****************************************************
        //Creates the right side pandel
        //Inlcudes the analysis type and the vertex of interest
        //*****************************************************
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(0,2));

        JLabel analyLabel = new JLabel("Analysis Type:");
        rightPanel.add(analyLabel);

        final JComboBox<Object> analyBox = new JComboBox<Object>(AnalysisType.values());
        rightPanel.add(analyBox);

        JLabel VOILabel = new JLabel("Vertex of interest:");
        rightPanel.add(VOILabel);

        JLabel VOILabel2 = new JLabel("Second vertex of interest:");
        rightPanel.add(VOILabel2);

        final JComboBox<ComboBoxItem> VOIBox = createComboBox(snc.getVertices());
        final JComboBox<ComboBoxItem> VOIBox2 = createComboBox(snc.getVertices());
        VOIBox2.setEnabled(false);
        rightPanel.add(VOIBox);
        rightPanel.add(VOIBox2);

        add(rightPanel);
		
        //*********************************************
        //Creates the next left side panel 
        //Includes the boundtype and value of the bound
        //*********************************************
        JPanel boundPanel = new JPanel();
        boundPanel.setLayout(new GridLayout(0,2));

        boundPanel.add(new JLabel("Select the type of bound: "));
        final JComboBox<Object> typeBox = new JComboBox<Object>(BoundType.values());
        typeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if(typeBox.getSelectedItem() == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                    VOIBox2.setEnabled(true);
                } else {
                    VOIBox2.setEnabled(false);
                }
            }
        });
        boundPanel.add(typeBox);

        boundPanel.add(new JLabel("Give the Delay or Backlog bound: "));
        final JTextField valueField = new JTextField(10);
        boundPanel.add(valueField);

        add(boundPanel);

        //adds a dummy label

        add(new JLabel());
		
        //***********************
        //Creates the lower panel 
        //Includes exit buttons
        //***********************
        lowerPanel = new JPanel();

        JButton okButton = new JButton("OK");
        lowerPanel.add(okButton);
        okButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    ComboBoxItem selectedFlow = (ComboBoxItem)FOIBox.getSelectedItem();
                    ComboBoxItem selectedVertex = (ComboBoxItem)VOIBox.getSelectedItem();
                    AnalysisType anaType = (AnalysisType)analyBox.getSelectedItem();
                    BoundType boundType = (BoundType)typeBox.getSelectedItem();
                    OptimizationType optType = (OptimizationType)optiBox.getSelectedItem();
                    double thetaGranularity = (double)theta.getValue();
                    double hoelderGranularity = (double) hoelder.getValue();
                    double value = (double)Double.parseDouble(valueField.getText());
                    //snc.calculateBound(snc.getFlow(selectedFlow.getId()), snc.getVertex(selectedVertex.getId()), thetaGranularity, hoelderGranularity, anaType, optType, boundType, value, snc.getCurrentNetwork());
                    dispose();
                }

        });
		
        JButton cancelButton = createExitButton();
        lowerPanel.add(cancelButton);
        add(lowerPanel);

        //***************
        //Finishing touch
        //***************
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
