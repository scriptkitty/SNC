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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import unikl.disco.mgf.Arrival;
import unikl.disco.mgf.SNC;
import unikl.disco.mgf.network.AbstractAnalysis;
import unikl.disco.mgf.network.Flow;
import unikl.disco.mgf.network.Vertex;
import unikl.disco.mgf.network.AnalysisType;
import unikl.disco.mgf.optimization.OptimizationType;

/**
 * This is a first GUI, allowing the user easy manipulations of the network
 * and performing the calculation of symbolic and numerical bounds. It is
 * capable of saving and loading networks (menubar), adding and removing
 * flows as well as nodes and calculating bounds. Further it implements
 * two tables, one showing all nodes in the network, the other showing all
 * flows in the network. The Systems output is redirected to the lower right
 * handed field. The upper right handed field is empty so far, it is intended
 * to implement a graphical view of the network here.
 * @author Michael Beck
 *
 */
public class GUI implements Runnable {
	
	//Members
	private static SNC caller;
	private static AbstractTableModel flowModel;
	private static AbstractTableModel nodeModel;
	
	//Constructor
	public GUI(SNC caller){
		GUI.caller = caller;
	}
	
	@Override
	public void run(){
		
		//Constructs the GUI
		final JFrame MainFrame = new JFrame("disco - Stochastic Network Calculator");
		
		MainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
	
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//*************
		//Adds the menu
		//*************
		JMenuBar menubar = new JMenuBar();
		JMenu fileBar = new JMenu("File");
		menubar.add(fileBar);
		
		//Adds open item
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser open = new JFileChooser();
				int opened = open.showOpenDialog(null);
				if(opened == JFileChooser.APPROVE_OPTION){
					loadNetwork(open.getSelectedFile());
				}
			}
		});
		
		//Adds save item
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser save = new JFileChooser();
				int saved = save.showSaveDialog(null);
				if(saved == JFileChooser.APPROVE_OPTION){
					saveNetwork(save.getSelectedFile());
				}
			}
		});
		
		//Adds exit item
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.dispose();
			}
			
		});
		
		fileBar.add(openItem); fileBar.add(saveItem); fileBar.add(exitItem);
		
		MainFrame.setJMenuBar(menubar);
		
		
		//********************
		//Adds the main Panels
		//********************
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		MainFrame.add(splitPane);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

		splitPane.setTopComponent(leftPanel);
		splitPane.setBottomComponent(rightPanel);
		
		//******************************************
		//Adds the controller to the left upper side
		//******************************************
		JPanel controller = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		controller.setLayout(layout);
		
		//Adds the buttons to the controller panel
		JButton addFlowButton = new JButton("Add Flow");
		addFlowButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FlowEditor dialog = new FlowEditor("Add Flow", caller.getVertices(), caller.getCurrentNetwork());
				int output = dialog.showFlowEditor();
				if(output == FlowEditor.APPROVE_OPTION){
					if(dialog.getEditedFlow() != null) addFlow(dialog.getEditedFlow());
				}
			}
			
		});
		GridBagConstraints addFlowGC = new GridBagConstraints();
		addFlowGC.gridx = 0;
		addFlowGC.gridy = 0;
		addFlowGC.gridwidth = 1;
		addFlowGC.gridheight = 1;
		addFlowGC.anchor = GridBagConstraints.WEST;
		controller.add(addFlowButton, addFlowGC);
		
		JButton removeFlowButton = new JButton("Remove Flow");
		removeFlowButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				HashMap<Integer, Flow> flows = caller.getFlows();
				FlowChooser dialog = new FlowChooser("Remove Flow", flows);
				int output = dialog.showFlowChooser();
				if(output == FlowChooser.APPROVE_OPTION){
					removeFlow(dialog.getSelectedFlow());
				}
			}
			
		});
		GridBagConstraints removeFlowGC = new GridBagConstraints();
		removeFlowGC.gridx = 1;
		removeFlowGC.gridy = 0;
		removeFlowGC.gridwidth = 1;
		removeFlowGC.gridheight = 1;
		removeFlowGC.anchor = GridBagConstraints.WEST;
		controller.add(removeFlowButton, removeFlowGC);
		
		/*
		JButton editFlowButton = new JButton("Edit Flow");
		//TODO: Edit the Flow class, such that the editor can read the flow.
		GridBagConstraints editFlowGC = new GridBagConstraints();
		editFlowGC.gridx = 2;
		editFlowGC.gridy = 0;
		editFlowGC.gridwidth = 1;
		editFlowGC.gridheight = 1;
		editFlowGC.anchor = GridBagConstraints.WEST;
		controller.add(editFlowButton, editFlowGC);*/
		
		JButton addVertexButton = new JButton("Add Vertex");
		addVertexButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				VertexEditor dialog = new VertexEditor("Add Vertex", caller.getCurrentNetwork());
				int output = dialog.showVertexEditor();
				if(output == VertexEditor.APPROVE_OPTION){
					addVertex(dialog.getEditedVertex());
				}
			}
		});
		GridBagConstraints addVertexGC = new GridBagConstraints();
		addVertexGC.gridx = 0;
		addVertexGC.gridy = 1;
		addVertexGC.gridwidth = 1;
		addVertexGC.gridheight = 1;
		addVertexGC.anchor = GridBagConstraints.WEST;
		controller.add(addVertexButton, addVertexGC);
		
		JButton removeVertexButton = new JButton("Remove Vertex");
		removeVertexButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				VertexChooser dialog = new VertexChooser("Remove Vertex", caller.getVertices());
				int output = dialog.showVertexChooser();
				if(output == FlowChooser.APPROVE_OPTION){
					removeVertex(dialog.getSelectedVertex());
				}
			}
			
		});
		
		GridBagConstraints removeVertexGC = new GridBagConstraints();
		removeVertexGC.gridx = 1;
		removeVertexGC.gridy = 1;
		removeVertexGC.gridwidth = 1;
		removeVertexGC.gridheight = 1;
		removeVertexGC.anchor = GridBagConstraints.WEST;
		controller.add(removeVertexButton, removeVertexGC);
		
		/*
		JButton editVertexButton = new JButton("Edit Vertex");
		GridBagConstraints editVertexGC = new GridBagConstraints();
		//TODO: Edit vertex class, such that information can be passed to VertexEditor
		editVertexGC.gridx = 2;
		editVertexGC.gridy = 1;
		editVertexGC.gridwidth = 1;
		editVertexGC.gridheight = 1;
		editVertexGC.anchor = GridBagConstraints.WEST;
		controller.add(editVertexButton, editVertexGC);*/
		
		JButton analyzeButton = new JButton("Analyze Network");
		
		analyzeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				AnalyzeDialog dialog = new AnalyzeDialog("Analyze Network", caller.getFlows(), caller.getVertices());
				int output = dialog.showAnalyzeDialog();
				if(output == BoundDialog.APPROVE_OPTION){
					analyzeNetwork(dialog.getSelectedFlow(), dialog.getSelectedVertex(), dialog.getSelectedSecondVertex(), dialog.getAnalyzer(), dialog.getBoundtype());
				}
			}
			
		});
		GridBagConstraints analyzeGC = new GridBagConstraints();
		analyzeGC.gridx = 0;
		analyzeGC.gridy = 2;
		analyzeGC.gridwidth = 1;
		analyzeGC.gridheight = 1;
		analyzeGC.anchor = GridBagConstraints.WEST;
		controller.add(analyzeButton, analyzeGC);
		
		JButton calculateButton = new JButton("Calculate Bound");

		calculateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BoundDialog dialog = new BoundDialog("Calculate Bound", caller.getFlows(), caller.getVertices());
				int output = dialog.showBoundDialog();
				if(output == BoundDialog.APPROVE_OPTION){
					calculateBound(dialog.getSelectedFlow(), dialog.getSelectedVertex(), dialog.getSelectedSecondVertex(), dialog.getThetaGranularity(), 
							dialog.getHoelderGranularity(), dialog.getAnalyzer(), dialog.getOptimizer(), dialog.getBoundtype(), 
							dialog.getValue());
				}
			}
			
		});
		GridBagConstraints calculateGC = new GridBagConstraints();
		calculateGC.gridx = 1;
		calculateGC.gridy = 2;
		calculateGC.gridwidth = 1;
		calculateGC.gridheight = 1;
		calculateGC.anchor = GridBagConstraints.WEST;
		controller.add(calculateButton, calculateGC);
		
		
		JButton inverseButton = new JButton("Calculate Inverse Bound");
		inverseButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				InverseBoundDialog dialog = new InverseBoundDialog("Calculate Inverse Bound", caller.getFlows(), caller.getVertices());
				int output = dialog.showInverseBoundDialog();
				if(output == BoundDialog.APPROVE_OPTION){
					calculateInverseBound(dialog.getSelectedFlow(), dialog.getSelectedVertex(), dialog.getSelectedSecondVertex(), dialog.getThetaGranularity(), 
							dialog.getHoelderGranularity(), dialog.getBoundGranularity(), dialog.getAnalyzer(), 
							dialog.getOptimizer(), dialog.getBoundtype(), dialog.getProbability());
				}
			}
			
		});
		
		GridBagConstraints inverseGC = new GridBagConstraints();
		inverseGC.gridx = 2;
		inverseGC.gridy = 2;
		inverseGC.gridwidth = 1;
		inverseGC.gridheight = 1;
		inverseGC.anchor = GridBagConstraints.WEST;
		controller.add(inverseButton, inverseGC);
		
		leftPanel.add(controller);
		
		//***************************************************
		//Adds a flow and vertex table to the left lower side
		//***************************************************
		JPanel tables = new JPanel();
		leftPanel.add(tables);
		tables.setLayout(new BoxLayout(tables, BoxLayout.Y_AXIS));
		
		//Creates the tables

		flowModel = new AbstractTableModel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				return 5;
			}

			@Override
			public int getRowCount() {
				return caller.getFlows().keySet().size();
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				Integer[] keys = caller.getFlows().keySet().toArray(new Integer[0]);
				int id = keys[arg0];
				String entry;
				switch(arg1){
					case 0:
						entry = caller.getFlows().get(id).getAlias();
						break;
					case 1:
						entry = ""+caller.getFlows().get(id).getFlow_ID();
						break;
					case 2: 
						try{
							entry = caller.getFlows().get(id).getInitialArrival().toString();
						}
						catch(IndexOutOfBoundsException e){
							entry = "";
						}
						break;
					case 3:
						entry = caller.getFlows().get(id).getVerticeIDs().toString();
						break;
					case 4:
						entry = caller.getFlows().get(id).getPriorities().toString();
						break;
					default:
						entry = "";
				}
				return entry;
			}
			
		};
		JTable flowTable = new JTable(flowModel);
		flowTable.getColumn("A").setHeaderValue("Flow");
		flowTable.getColumn("B").setHeaderValue("ID");
		flowTable.getColumn("C").setHeaderValue("Init.Arr.");
		flowTable.getColumn("D").setHeaderValue("Route");
		flowTable.getColumn("E").setHeaderValue("Priorities");
		
		nodeModel = new AbstractTableModel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -8043680193835853275L;

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public int getRowCount() {
				return caller.getVertices().keySet().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Integer[] keys = caller.getVertices().keySet().toArray(new Integer[0]);
				int id = keys[rowIndex];
				String entry;
				switch(columnIndex){
					case 0:
						entry = caller.getVertices().get(id).getAlias();
						break;
					case 1:
						entry = ""+caller.getVertices().get(id).getVertexID();
						break;
					case 2:
						entry = caller.getVertices().get(id).getAllFlowIDs().toString();
						break;
					default:
						entry = "";
						break;
				}
				return entry;
			}
			
		};
		JTable nodeTable = new JTable(nodeModel);
		nodeTable.getColumn("A").setHeaderValue("Node");
		nodeTable.getColumn("B").setHeaderValue("ID");
		nodeTable.getColumn("C").setHeaderValue("Flows");

		
		//Adds scroll panes to contain the tables
		JScrollPane flowScrollPane = new JScrollPane(flowTable);
		
		flowTable.setFillsViewportHeight(true);
		flowTable.setAutoCreateRowSorter(true);
		
		tables.add(flowScrollPane);
		
		JScrollPane nodeScrollPane = new JScrollPane(nodeTable);
		nodeTable.setFillsViewportHeight(true);	
		nodeTable.setAutoCreateRowSorter(true);
		
		tables.add(nodeScrollPane);
		
		//***********************************************************
		//Creates place for a graphical representation of the network
		//***********************************************************
		//TODO: Graphical representation
		JPanel graph = new JPanel();
		rightPanel.setTopComponent(graph);
		graph.setMinimumSize(new Dimension(300, 300));
		
		//**************************
		//Creates the console output
		//**************************
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		
		JScrollPane console = new JScrollPane(textArea);
		rightPanel.setBottomComponent(console);
		
		//Redirects System Outputs to the console
		redirectSystemStreams(textArea);
		
		//***************
		//Finishing touch
		//***************
		MainFrame.pack();
		
		MainFrame.setVisible(true);
	}
		
	/**
	 * Helper method to redirect the output stream to the console.
	 */
	private static void redirectSystemStreams(final JTextArea textarea){
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException{
				updateTextArea(String.valueOf((char) b), textarea);
			}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len), textarea);
			}
			
			@Override
			public void write(byte[] b) throws IOException{
				write(b, 0, b.length);
			}
		};
		
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
	
	/**
	 * Helper method to redirect the output stream to the console.
	 */
	private static void updateTextArea(final String text, final JTextArea textarea){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run(){
				textarea.append(text);
			}
		});
	}
	
	//Methods
	
	//Loading and saving of Networks
	private static void loadNetwork(File file){
		caller.loadNetwork(file);
		flowModel.fireTableDataChanged();
		nodeModel.fireTableDataChanged();
		updateGraph();
		
		System.out.println(file.getName()+" loaded");
	}
	
	private static void saveNetwork(File file){
		caller.saveNetwork(file, caller.getCurrentNetwork());
		System.out.println(file.getName()+" saved");
	}
	
	//Updates the GUI
	private static void updateGraph(){
		//TODO: Implement graph
	}
	
	//Alters the network
	private static void removeFlow(Flow flow){
		
		//Alters the network via the caller
		boolean success = caller.removeFlow(flow, caller.getCurrentNetwork());
		
		//Console output
		if(success) System.out.println(flow.getAlias()+ " with ID "+flow.getFlow_ID()+ " removed");
		else System.out.println("Flow can not be removed. Possible reasons: The flow has already been removed.");
		
		//Updates GUI
		flowModel.fireTableDataChanged();
		nodeModel.fireTableDataChanged();
		updateGraph();
	}
	
	private static void removeVertex(Vertex vertex){
		
		//Alters the network via the caller
		boolean success = caller.removeVertex(vertex, caller.getCurrentNetwork());
		
		//Console output
		if(success) System.out.println(vertex.getAlias()+ " with ID "+vertex.getVertexID()+" removed");
		else System.out.println("Vertex can not be removed. Possible reasons: The vertex has already been removed.");
		
		//Updates GUI
		flowModel.fireTableDataChanged();
		nodeModel.fireTableDataChanged();
		updateGraph();
	}

	private static void addFlow(Flow flow){

		//Alters the network via the caller
		int newID = caller.addFlow(flow, caller.getCurrentNetwork());
		
		//Console output
		if(newID>0){
			System.out.println(flow.getAlias()+ " with ID "+newID+ " added");
			System.out.println("Route: "+flow.getVerticeIDs());
			System.out.println("Priorities: "+flow.getPriorities());
		}
		else System.out.println("Flow can't be added.");
		
		//Updates GUI
		flowModel.fireTableDataChanged();
		nodeModel.fireTableDataChanged();
		updateGraph();
	}
	
	private static void addVertex(Vertex vertex){
		
		//Alters the network via the caller
		int newID = caller.addVertex(vertex, caller.getCurrentNetwork());
		
		//Console output
		if(newID>0){
			System.out.println(vertex.getAlias()+ " with ID "+newID+" added");
		}
		else System.out.println("Vertex can't be added.");
		
		//Updates GUI
		flowModel.fireTableDataChanged();
		nodeModel.fireTableDataChanged();
		updateGraph();
	}
	
	//Calculating bounds
	private void calculateBound(Flow selectedFlow, Vertex selectedVertex, Vertex selectedSecondVertex,
			double thetaGranularity, double hoelderGranularity,
			AnalysisType analyzer, OptimizationType optimizer, AbstractAnalysis.Boundtype boundtype, double value) {
		System.out.println("Bound is being calculated...");
                System.out.println("Boundtype:" + boundtype.toString());
                double probability = -1;
                if(boundtype == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                    probability = caller.calculateE2EBound(selectedFlow, selectedVertex, selectedSecondVertex, thetaGranularity, hoelderGranularity, analyzer, optimizer, value, caller.getCurrentNetwork());
                } else {
                    probability = caller.calculateBound(selectedFlow, selectedVertex, thetaGranularity, hoelderGranularity, 
				analyzer, optimizer, boundtype, value, caller.getCurrentNetwork());
                }
		System.out.println("The probability for the asked bound being broken is smaller than: "+probability);
	}
	
	private void calculateInverseBound(Flow selectedFlow, Vertex selectedVertex, Vertex selectedSecondVertex, double thetaGranularity, 
			double hoelderGranularity, double boundGranularity, AnalysisType analyzer, OptimizationType optimizer, 
			AbstractAnalysis.Boundtype boundtype, double probability){
		System.out.println("Inverse Bound is being calculated...");
                double value = 0;
                if(boundtype == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                    value = caller.calculateInverseE2EBound(selectedFlow, selectedVertex, selectedSecondVertex, thetaGranularity, hoelderGranularity, boundGranularity, analyzer, optimizer, probability, caller.getCurrentNetwork());
                } else {
                    value = caller.calculateInverseBound(selectedFlow, selectedVertex, thetaGranularity, hoelderGranularity, 
				boundGranularity, analyzer, optimizer, boundtype, probability, caller.getCurrentNetwork());
                }
		System.out.println("The best calculated bound for the asked probability is: "+value);
	}
	
	private void analyzeNetwork(Flow selectedFlow, Vertex selectedVertex, Vertex selectedSecondVertex, AnalysisType analyzer, AbstractAnalysis.Boundtype boundtype){
		System.out.println("Network is being analyzed...");
                Arrival bound = null;
                if(boundtype == AbstractAnalysis.Boundtype.END_TO_END_DELAY) {
                    System.out.println("End-To-End Delay Analysis: No closed form available at the moment.");
                } else {
                    bound = caller.analyzeNetwork(selectedFlow, selectedVertex, analyzer, boundtype, caller.getCurrentNetwork());
                }
		System.out.println("The bound in arrival-representation equals: "+bound.toString());
	}

}
