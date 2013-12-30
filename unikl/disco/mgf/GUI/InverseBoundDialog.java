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
import unikl.disco.mgf.network.AnalysisType;
import unikl.disco.mgf.optimization.OptimizationType;

/**
 * Creates a dialog, asking the user for the parameters
 * needed to calculate a bound.
 * @author Michael Beck
 */
public class InverseBoundDialog extends JDialog {

	//Members
	
	private static final long serialVersionUID = 7955016125663851149L;
	
	static final int CANCEL_OPTION = 0;
	static final int APPROVE_OPTION = 1;
	static final int ERROR_OPTION = 2;
		
	private int output;
	private Flow flow; 
	private Vertex vertex;
        private Vertex vertex2;
	private double thetaGran;
	private double hoelderGran;
	private double boundGran;
	private OptimizationType optType;
	private AnalysisType anaType;
	private AbstractAnalysis.Boundtype boundtype;
	private double probability;
	
	//Constructor
	
	public InverseBoundDialog(String title, final HashMap<Integer, Flow> flows, 
			final HashMap<Integer, Vertex> vertices){
		
		//Constructs the dialog
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		
		setLayout(new GridLayout(0,2));
		
		//************************************************
		//Adds the granularity Panel
		//Includes spinners for choosing theta and hoelder
		//************************************************
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
		
		//***********************************
		//Adds another granularity Panel
		//Includes spinner for choosing bound
		//***********************************
		JPanel granBoundPanel = new JPanel();
		granBoundPanel.setLayout(new GridLayout(0,2));
		add(granBoundPanel);
		
		//Creates the spinner for choosing bound-granularity
		JLabel boundLabel = new JLabel("Bound-Granularity");
		granBoundPanel.add(boundLabel);
		
		SpinnerNumberModel boundmodel = new SpinnerNumberModel(0.01, 0, 1, 0.001);
		final JSpinner bound = new JSpinner(boundmodel);
		granBoundPanel.add(bound);
		
		//***************************************************
		//Creates the left side panel
		//Includes the optimization type and flow of interest
		//***************************************************
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(0,2));
		
		JLabel optiLabel = new JLabel("Optimization Type:");
		leftPanel.add(optiLabel);
		
		final JComboBox<Object> optiBox = new JComboBox<Object>(OptimizationType.values());
		leftPanel.add(optiBox);
		
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
		
		final JComboBox<Object> analyBox = new JComboBox<Object>(AnalysisType.values());
		rightPanel.add(analyBox);
		
		JLabel VOILabel = new JLabel("Vertex of interest:");
		rightPanel.add(VOILabel);
                
                JLabel VOILabel2 = new JLabel("Second vertex of interest:");
                rightPanel.add(VOILabel2);
		
		final HashMap<Integer, String> vertexAliases = new HashMap<Integer, String>();
		for(Entry<Integer, Vertex> entry : vertices.entrySet()){
			if(entry.getValue().getAlias() != null) vertexAliases.put(entry.getValue().getVertexID(), entry.getValue().getAlias());
			else vertexAliases.put(entry.getValue().getVertexID(), "ID "+entry.getValue().getVertexID());
		}
		final JComboBox<String> VOIBox = new JComboBox<String>(vertexAliases.values().toArray(new String[0]));
                final JComboBox<String> VOIBox2 = new JComboBox<String>(vertexAliases.values().toArray(new String[0]));
                VOIBox2.setEnabled(false);
		rightPanel.add(VOIBox);
                rightPanel.add(VOIBox2);
		
		add(rightPanel);
		
		//**********************************************
		//Creates the next left side panel 
		//Includes the boundtype and value for the bound
		//**********************************************
		JPanel boundPanel = new JPanel();
		boundPanel.setLayout(new GridLayout(0,2));
		
		boundPanel.add(new JLabel("Select the type of bound: "));
		final JComboBox<Object> typeBox = new JComboBox<Object>(AbstractAnalysis.Boundtype.values());
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
		
		boundPanel.add(new JLabel("Give the maximal violation probability: "));
		final JTextField probabilityField = new JTextField(10);
		boundPanel.add(probabilityField);
		
		add(boundPanel);
		
		//adds a dummy label
		
		add(new JLabel());
		
		//********************************************
		//Creates the lower panel for the exit buttons
		//********************************************
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
                                String vertex2Name = (String)VOIBox2.getSelectedItem();
				int vertexID = -1;
                                int vertex2ID = -1;
				for(Entry<Integer, String> entry : vertexAliases.entrySet()){
					if(entry.getValue() == vertexName) vertexID = entry.getKey();
                                        if(entry.getValue() == vertex2Name) vertex2ID = entry.getKey();
				}
				vertex = vertices.get(vertexID);
                                vertex2 = vertices.get(vertex2ID);
				
				optType = (OptimizationType)optiBox.getSelectedItem();
				anaType = (AnalysisType)analyBox.getSelectedItem();
				try{
					thetaGran = ((SpinnerNumberModel) theta.getModel()).getNumber().doubleValue();
				}
				catch(Exception exc){
					System.out.println(exc.getMessage());
					output = CANCEL_OPTION;
				}
				try{
					hoelderGran = ((SpinnerNumberModel) hoelder.getModel()).getNumber().doubleValue();
				}
				catch(Exception exc){
					System.out.println(exc.getMessage());
					output = CANCEL_OPTION;
				}
				try{
					boundGran = ((SpinnerNumberModel) bound.getModel()).getNumber().doubleValue();
				}
				catch(Exception exc){
					System.out.println(exc.getMessage());
					output = CANCEL_OPTION;
				}
				
				boundtype = (AbstractAnalysis.Boundtype)typeBox.getSelectedItem();
                                if(vertexID == vertex2ID && boundtype == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                                    System.out.println("Vertices are the same.");
                                    output = CANCEL_OPTION;
                                }
				try{
					probability = Double.parseDouble(probabilityField.getText());
				}
				catch(Exception exc){
					System.out.println(exc.getMessage());
					output = CANCEL_OPTION;
				}			
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
	
	public int showInverseBoundDialog(){
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
        
        public Vertex getSelectedSecondVertex() {
            return vertex2;
        }
	
	public double getThetaGranularity(){
		return thetaGran;
	}
	
	public double getHoelderGranularity(){
		return hoelderGran;
	}
	
	public double getBoundGranularity(){
		return boundGran;
	}
	
	public AnalysisType getAnalyzer(){
		return anaType;
	}
	
	public OptimizationType getOptimizer(){
		return optType;
	}
	
	public AbstractAnalysis.Boundtype getBoundtype(){
		return boundtype;
	}
	
	public double getProbability(){
		return probability;
	}
}
