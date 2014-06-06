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

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;

import unikl.disco.calculator.SNC;
import unikl.disco.calculator.network.AbstractAnalysis;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.calculator.network.AnalysisType;

/**
 * Creates a dialog, asking the user for the parameters
 * needed to calculate a symbolic sigma-rho-bound.
 * @author Michael Beck
 *
 */
public class AnalyzeDialog extends JDialog {

	//Members
	private static final long serialVersionUID = 7955016125663851149L;
	
	//Constructor
	public AnalyzeDialog(String title, final SNC snc) {
            // Use ids, if there is no alias?
            List<ComboBoxItem> flowBox = new ArrayList<>();
            for(Entry<Integer, Flow> entry : snc.getCurrentNetwork().getFlows().entrySet()) {
                flowBox.add(new ComboBoxItem(entry.getKey(), entry.getValue().getAlias()));
            }
            
            List<ComboBoxItem> vertexBox = new ArrayList<>();
            for(Entry<Integer, Vertex> entry : snc.getCurrentNetwork().getVertices().entrySet()) {
                vertexBox.add(new ComboBoxItem(entry.getKey(), entry.getValue().getAlias()));
            }
            
            //Constructs the dialog
            this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            this.setTitle(title);

            setLayout(new GridLayout(0,2));

            //**********************************************
            //Creates the left side panel
            //Includes the type of bound and flow of interest
            //**********************************************
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new GridLayout(0,2));

            JLabel FOILabel = new JLabel("Flow of interest:");
            leftPanel.add(FOILabel);
            
            final JComboBox<ComboBoxItem> FOIBox = new JComboBox<>(flowBox.toArray(new ComboBoxItem[0]));
            leftPanel.add(FOIBox);

            add(leftPanel);

            //*****************************************************
            //Creates the right side panel
            //Includes the analysis type and the vertex of interest
            //*****************************************************
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new GridLayout(0,2));

            JLabel analyLabel = new JLabel("Analysis Type:");
            rightPanel.add(analyLabel);

            final JComboBox<Object> analyBox = new JComboBox<Object>(AnalysisType.values());
            rightPanel.add(analyBox);

            JLabel VOILabel = new JLabel("Vertex of interest:");
            rightPanel.add(VOILabel);

            final JComboBox<ComboBoxItem> VOIBox = new JComboBox<>(vertexBox.toArray(new ComboBoxItem[0]));

            rightPanel.add(VOIBox);

            leftPanel.add(new JLabel("Select the type of bound: "));
            final JComboBox<Object> typeBox = new JComboBox<Object>(AbstractAnalysis.Boundtype.values());
            typeBox.addActionListener(new ActionListener() {
                // TODO: Change analysis types for good
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if(typeBox.getSelectedItem() == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                        typeBox.setSelectedIndex(-1);
                        System.out.println("Not implemented yet");
                    }
                }
            });
            leftPanel.add(typeBox);

            add(rightPanel);
		
		//*************************
		//Creates the lower panel 
		//Includes the exit buttons
		//*************************
		JPanel lowerPanel = new JPanel();
		
		JButton okButton = new JButton("OK");
		lowerPanel.add(okButton);
		okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComboBoxItem selectedFlow = (ComboBoxItem)FOIBox.getSelectedItem();
				ComboBoxItem selectedVertex = (ComboBoxItem)VOIBox.getSelectedItem();
                                AnalysisType anaType = (AnalysisType)analyBox.getSelectedItem();
                                AbstractAnalysis.Boundtype boundType = (AbstractAnalysis.Boundtype)typeBox.getSelectedItem();

				snc.analyzeNetwork(snc.getCurrentNetwork().getFlow(selectedFlow.getId()), snc.getCurrentNetwork().getVertex(selectedVertex.getId()), anaType, boundType, snc.getCurrentNetwork());
				dispose();
			}
			
		});
		
		JButton cancelButton = new JButton("Cancel");
		lowerPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		
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
