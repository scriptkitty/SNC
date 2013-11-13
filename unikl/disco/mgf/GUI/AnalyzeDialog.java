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

package unikl.disco.mgf.GUI;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.*;

import unikl.disco.mgf.SNC;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.Flow;
import unikl.disco.mgf.network.Vertex;

/**
 * Creates a dialog, asking the user for the parameters
 * needed to calculate a symbolic sigma-rho-bound.
 * @author Michael Beck
 *
 */
public class AnalyzeDialog extends JDialog {

	//Members
	private static final long serialVersionUID = 7955016125663851149L;
	
	static final int CANCEL_OPTION = 0;
	static final int APPROVE_OPTION = 1;
	static final int ERROR_OPTION = 2;
	
	private int output;
	private Flow flow; 
	private Vertex vertex;
	private SNC.AnalysisType anaType;
	private AbstractAnalysis.Boundtype boundtype;
	
	//Constructor
	public AnalyzeDialog(String title, final HashMap<Integer, Flow> flows, 
			final HashMap<Integer, Vertex> vertices){
		
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
		
		leftPanel.add(new JLabel("Select the type of bound: "));
		final JComboBox<Object> typeBox = new JComboBox<Object>(AbstractAnalysis.Boundtype.values());
		leftPanel.add(typeBox);
		
		JLabel FOILabel = new JLabel("Flow of interest:");
		leftPanel.add(FOILabel);
		
		final HashMap<Integer, String> flowAliases = new HashMap<Integer, String>();
		for(Entry<Integer, Flow> entry : flows.entrySet()){
			if(entry.getValue().getAlias() != null) flowAliases.put(entry.getValue().getFlow_ID(), entry.getValue().getAlias());
			else flowAliases.put(entry.getValue().getFlow_ID(), "ID "+entry.getValue().getFlow_ID());
		}
		final JComboBox<String> FOIBox = new JComboBox<String>(flowAliases.values().toArray(new String[0]));
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
		
		final JComboBox<Object> analyBox = new JComboBox<Object>(SNC.AnalysisType.values());
		rightPanel.add(analyBox);
		
		JLabel VOILabel = new JLabel("Vertex of interest:");
		rightPanel.add(VOILabel);
		
		final HashMap<Integer, String> vertexAliases = new HashMap<Integer, String>();
		for(Entry<Integer, Vertex> entry : vertices.entrySet()){
			if(entry.getValue().getAlias() != null) vertexAliases.put(entry.getValue().getVertexID(), entry.getValue().getAlias());
			else vertexAliases.put(entry.getValue().getVertexID(), "ID "+entry.getValue().getVertexID());
		}
		final JComboBox<String> VOIBox = new JComboBox<String>(vertexAliases.values().toArray(new String[0]));
		rightPanel.add(VOIBox);
		
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
				output = APPROVE_OPTION;
				
				String flowName = (String)FOIBox.getSelectedItem();
				int flowID = -1;
				for(Entry<Integer, String> entry : flowAliases.entrySet()){
					if(entry.getValue() == flowName) flowID = entry.getKey();
				}
				flow = flows.get(flowID);
				
				String vertexName = (String)VOIBox.getSelectedItem();
				int vertexID = -1;
				for(Entry<Integer, String> entry : vertexAliases.entrySet()){
					if(entry.getValue() == vertexName) vertexID = entry.getKey();
				}
				vertex = vertices.get(vertexID);
				
				anaType = (SNC.AnalysisType)analyBox.getSelectedItem();
				
				boundtype = (AbstractAnalysis.Boundtype)typeBox.getSelectedItem();
				
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
	}
	
	//Methods
	
	public int showAnalyzeDialog(){
		setVisible(true);
		return output;
	}
	
	//Getter and Setter
	
	public Flow getSelectedFlow(){
		return flow;
	}
	
	public Vertex getSelectedVertex(){
		return vertex;
	}
	
	public SNC.AnalysisType getAnalyzer(){
		return anaType;
	}
	
	public AbstractAnalysis.Boundtype getBoundtype(){
		return boundtype;
	}
	
}
