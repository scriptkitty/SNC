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

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import unikl.disco.mgf.*;
import unikl.disco.mgf.network.*;

/**
 * Dialog for editing a flow.
 * @author Michael Beck
 *
 */
public class FlowEditor extends JDialog {

	//Members
	
	private static final long serialVersionUID = 4146247537102257784L;
	
	static final int CANCEL_OPTION = 0;
	static final int APPROVE_OPTION = 1;
	static final int ERROR_OPTION = 2;
	private int output = 0;
	private Flow flow;
        private final Network nw;
	
	private final JPanel topCardContainer = new JPanel();
	private final JPanel bottomCardContainer = new JPanel();
	
	//Constructors
	
	public FlowEditor(String title, final HashMap<Integer, Vertex> vertices, Flow flow, Network nw){
		this(title, vertices, nw);
		this.flow = flow;
	}
	
	public FlowEditor(String title, final HashMap<Integer, Vertex> vertices, final Network nw){
		
                this.nw = nw;
		//Constructs the dialog
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		//********************
		//Adds alias-textfield
		//********************
		JLabel aliasLabel = new JLabel("Alias of the flow:");
		final JTextField aliasField = new JTextField(10);
		
		JPanel aliasPanel = new JPanel();
		aliasPanel.setLayout(new FlowLayout());
		aliasPanel.add(aliasLabel); aliasPanel.add(aliasField);
		
		mainPanel.add(aliasPanel);
		
		//******************
		//Adds the top panel
		//******************
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Initial Arrival Type: "));
		
		//List of arrival types:
		final String CONSTANT = "Constant rate arrival";
		final String EXPONENTIAL = "Exponentially distributed increments";
		final String POISSON = "Compound Poisson Arrival";
		
		String[] arrivalBox = {CONSTANT, EXPONENTIAL, POISSON};
		final JComboBox<String> arrival = new JComboBox<String>(arrivalBox);
		topPanel.add(arrival);
		
		//Adds the listener for the initial arrival combo box
		ItemListener initListener = new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				CardLayout card = (CardLayout) topCardContainer.getLayout();
				card.show(topCardContainer, (String) e.getItem());
			}			
		};
		arrival.addItemListener(initListener);
		
		//******************************
		//Adds the cards for the arrival
		//******************************
		topCardContainer.setLayout(new CardLayout());
		
		JPanel card1 = new JPanel();
		card1.add(new JLabel("Rate: c="));
		final JTextField constantRate = new JTextField(10);
		card1.add(constantRate);
		
		JPanel card2 = new JPanel();
		card2.setLayout(new FlowLayout());
		card2.add(new JLabel("Rate: lambda="));
		final JTextField exponentialRate = new JTextField(10);
		card2.add(exponentialRate);
		
		JPanel card3 = new JPanel();
		card3.setLayout(new FlowLayout());
		card3.add(new JLabel("Intensity: mu="));
		final JTextField poissonIntensity = new JTextField(10);
		card3.add(poissonIntensity);
		
		topCardContainer.add(card1, CONSTANT);
		topCardContainer.add(card2, EXPONENTIAL);
		topCardContainer.add(card3, POISSON);
		
		mainPanel.add(topPanel);
		mainPanel.add(topCardContainer);
		((CardLayout) topCardContainer.getLayout()).show(topCardContainer, CONSTANT);

		//******************************
		//Adds the cards for the routing
		//******************************
		bottomCardContainer.setLayout(new CardLayout());
		Integer[] nodes = vertices.keySet().toArray(new Integer[0]);
		final CardStack cardStack = new CardStack(nodes);
		bottomCardContainer.add(cardStack);
		
		mainPanel.add(bottomCardContainer);
		
		//*********************
		//Adds the exit buttons
		//*********************
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				output = APPROVE_OPTION;
				boolean correct = true;
				ArrayList<Integer> route = cardStack.getRoute(new ArrayList<Integer>(0));
				
				//Gets the priorities from the CardStack
				ArrayList<Integer> priorities = new ArrayList<Integer>(0);
				try{
					priorities = cardStack.getPriorities(priorities);
				}
				catch(NumberFormatException exc){
					System.out.println("Priorities must be integers.");
					correct = false;
				}
				
				//Constant arrival case
				if(arrival.getSelectedItem() == CONSTANT){
					//uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
					rateSigma rho;
					try{
						rho = new rateSigma(-0.1);
						rho.setRate(Double.valueOf(constantRate.getText()));
					}
					catch(BadInitializationException exc){	
						rho = null;
						correct = false;
					}
					catch(NumberFormatException exc){
						System.out.println("The constant arrival rate must be a number.");
						rho = null;
						correct = false;
					}
					
					Arrival arrival = new Arrival(new ZeroFunction(), rho, nw);
					ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
					arrivals.add(arrival);
					
					if(correct) {
						flow = new Flow(-1, route, arrivals, priorities, aliasField.getText(), nw);
						flow.getInitialArrival().getArrivaldependencies().clear();
					}
					else flow = null;
				}
				
				if(arrival.getSelectedItem() == EXPONENTIAL){
					//uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
					ExponentialSigma rho = null;
					try{
						rho = new ExponentialSigma(Double.valueOf(exponentialRate.getText()));
					}
					catch(NumberFormatException exc){
						System.out.println("The rate lambda must be a number.");
						correct = false;
					} catch (BadInitializationException exc) {
						System.out.println(exc.getMessage());
						correct = false;
					}
					
					Arrival arrival = new Arrival(new ZeroFunction(), rho, nw);
					ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
					arrivals.add(arrival);
					
					if(correct) {
						flow = new Flow(-1, route, arrivals, priorities, aliasField.getText(), nw);
						flow.getInitialArrival().getArrivaldependencies().clear();
					}
					
					else flow = null;
				}
				if(arrival.getSelectedItem() == POISSON){
					//uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
					ZeroFunction sigma = null;
					try{
						sigma = new ZeroFunction();
					}
					catch(NumberFormatException exc) {
						System.out.println("The intensity mu must be a number");
						correct = false;
					}
					
					//defines the rho-part
					PoissonRho rho = null;
					
					if(correct){
						FlowEditor dialog = new FlowEditor("Define Increment", vertices, nw);
						int output = dialog.showFlowEditor();
						if(output == FlowEditor.APPROVE_OPTION){
							if(dialog.getEditedFlow() != null) rho = new PoissonRho(dialog.getEditedFlow().getInitialArrival().getRho(), 
									Double.valueOf(poissonIntensity.getText()));
							else correct = false;
						}
					}
					
					Arrival arrival = new Arrival(sigma, rho, nw);
					ArrayList<Arrival> arrivals = new ArrayList<Arrival>(0);
					arrivals.add(arrival);
					
					
					if(correct){
							flow = new Flow(-1, route, arrivals, priorities, aliasField.getText(), nw);
							flow.getInitialArrival().getArrivaldependencies().clear();
					}
					
					else flow = null;
				}
				//Add further arrivals types...
				dispose();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				output = CANCEL_OPTION;
				dispose();
			}
			
		});
		buttonPanel.add(ok); buttonPanel.add(cancel);
		mainPanel.add(buttonPanel);
		
		add(mainPanel);
		
		//***************
		//Finishing touch
		//***************
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);		
	}
	
	//Mehtods
	
	public int showFlowEditor(){
		setVisible(true);
		return output;
	}
	
	//Getter and Setter
	
	public Flow getEditedFlow(){
		return flow;
	}
	
	//Helper Classes
	/**
	 * Allows the user to set a route through the network. Visiting the same node
	 * multiple times is allowed (though not encouraged). The interface consists 
	 * of cards, which are linked to a combo-box. Is no node selected in the 
	 * combo-box no next card will be displayed. If a node is selected a new card
	 * - including its own combo-box - is displayed. This child-card is a copy of
	 * its parent card, with the difference that the combo-box is initialized with
	 * no node selected. The cards know about their child-card, as well as their 
	 * parent-card.
	 * @author Michael Beck
	 *
	 */
	private class CardStack extends JPanel{
		
		//Members
		
		private static final long serialVersionUID = -2317710139280863490L;
		
		public static final String NONE = "none";
		private JComboBox<Integer> nodeBox;
		private JTextField priorityField;
		
		private CardStack parent;
		private CardStack child;
		
		private JPanel cardPanel = new JPanel();
		private JPanel emptyCard = new JPanel();
		
		//Constructors
		public CardStack(Integer[] nodes){
			
			//Preparations
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel input = new JPanel();
			input.setLayout(new FlowLayout());
			input.add(new JLabel("Node: "));
			
			//Adds ComboBox
			nodeBox = new JComboBox<Integer>(nodes);
			nodeBox.setSelectedIndex(-1);
			final CardStackListener listener = new CardStackListener(this, nodes);
			nodeBox.addItemListener(listener);
			input.add(nodeBox);
			
			//Adds field for priority
			priorityField = new JTextField("", 5);
			input.add(new JLabel("Priority: "));
			input.add(priorityField);
			
			//Adds Delete-Button
			JButton delete = new JButton("Delete Node");
			delete.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					nodeBox.setSelectedIndex(-1);
					removeChild();
					activateParent();
					listener.setChildActive(false);
					activate();
				}
			});
			
			input.add(delete);
			
			add(input);
			
			cardPanel.setLayout(new CardLayout());
			cardPanel.add(emptyCard, NONE);
			((CardLayout)cardPanel.getLayout()).show(cardPanel, NONE);
			add(cardPanel);	
		}
		
		public CardStack(Integer[] nodes, CardStack parent){
			this(nodes);
			this.parent = parent;
		}
		
		//Methods
		
		public void addChild(CardStack child, int value){
			this.child = child;
			cardPanel.add(child, Integer.toString(value));
			((CardLayout)cardPanel.getLayout()).show(cardPanel, Integer.toString(value));
		}
		
		public void removeChild(){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, NONE);
			cardPanel.removeAll();
			cardPanel.add(emptyCard, NONE);
			this.child = null;
		}
		
		public void deactivateParent(){
			if(parent != null) parent.deactivate();
		}
		
		public void activateParent(){
			if(parent != null) parent.activate();
		}
		
		public void deactivate(){
			nodeBox.setEnabled(false);
		}
		public void activate(){
			nodeBox.setEnabled(true);
		}
		
		public ArrayList<Integer> getRoute(ArrayList<Integer> list){
			if((Integer)nodeBox.getSelectedItem() != null) list.add((Integer)nodeBox.getSelectedItem());
			if(child != null) {
				list = child.getRoute(list);
				return list;
			}
			else return list;
		}
		
		public ArrayList<Integer> getPriorities(ArrayList<Integer> priorities){
			if((Integer)nodeBox.getSelectedItem() != null)priorities.add(Integer.parseInt(priorityField.getText()));
			if(child != null) {
				priorities = child.getPriorities(priorities);
				return priorities;
			}
			else return priorities;
		}
		
	}
	
	/**
	 * Helper class for {@link CardStack}
	 * @author Michael Beck
	 *
	 */
	private class CardStackListener implements ItemListener{

		//Members
		
		private CardStack caller;
		private boolean childActive;
		private Integer[] nodes;
		
		//Constructor
		
		public CardStackListener(CardStack caller, Integer[] nodes){
			this.caller = caller;
			this.nodes = nodes;
			childActive = false;
		}
		
		//Methods
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			//Generate child card if a node is chosen and no child card had already been created
			if(e.getItem() != CardStack.NONE && !childActive){
				CardStack child = new CardStack(nodes, caller);
				caller.addChild(child,(Integer) e.getItem());
				childActive = true;
				caller.deactivateParent();
				pack();
			}
			
			//Delete the child card, if a previous selected node is deleted
			else if(e.getItem() == CardStack.NONE){
				caller.removeChild();
				childActive = false;
				caller.activateParent();
				pack();
			}
		}
		
		public void setChildActive(boolean ca){
			childActive = ca;
		}
	}
}
