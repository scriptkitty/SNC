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

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import unikl.disco.calculator.SNC;
import unikl.disco.calculator.commands.AddFlowCommand;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.ArrivalFactory;
import unikl.disco.calculator.symbolic_math.ArrivalType;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.misc.NetworkActionException;


/**
 * Dialog for editing a flow.
 *
 * @author Michael Beck
 * @author Sebastian Henningsen
 *
 */
public class FlowEditor extends JDialog {

	//Members
    private static final long serialVersionUID = 4146247537102257784L;

    static final int CANCEL_OPTION = 0;
    static final int APPROVE_OPTION = 1;
    static final int ERROR_OPTION = 2;
    private int output = 0;
    private Arrival flow_arrival;
   
    //private final Network nw; TODO: <-- Needed?
    private boolean flow_adding = true;

    private final JPanel topCardContainer = new JPanel();
    private final JPanel bottomCardContainer = new JPanel();

	//Constructors

    /**
     *
     * @param title
     * @param vertices
     * @param flow_arrival
     * @param nw
     * @param snc
     */
    public FlowEditor(String title, final Map<Integer, Vertex> vertices, Arrival flow_arrival, Network nw, SNC snc) {
    	this(title, vertices, nw, snc);
    	this.flow_arrival = flow_arrival;
    }
    
    /**
     * A constructor with <code>flow_adding</code>. This is needed to
     * call the increment-editor for compound poisson arrivals without it trying to add a new flow
     * to the network. Calling this with <code>flow_adding</code> set to <code>true</code> will
     * just result in the normal FlowEditor.
     * Setting <code>flow_adding</code> to <code>false</code> will construct the flow and write it 
     * into this dialog's <code>flow</code>-variable, accessible via <code>getEditedFlow</code>. 
     */
    public FlowEditor(String title, final Map<Integer, Vertex> vertices, Network nw, SNC snc, boolean flow_adding) {
    	this(title, vertices, nw, snc);
    	this.flow_adding = flow_adding;
    }

    /**
     *
     * @param title
     * @param vertices
     * @param nw
     * @param snc
     */
    public FlowEditor(String title, final Map<Integer, Vertex> vertices, final Network nw, final SNC snc) {

//	this.nw = nw; TODO: <-- needed?
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
	aliasPanel.add(aliasLabel);
	aliasPanel.add(aliasField);

	mainPanel.add(aliasPanel);

		//******************
	//Adds the top panel
	//******************
	JPanel topPanel = new JPanel();
	topPanel.setLayout(new FlowLayout());
	topPanel.add(new JLabel("Initial Arrival Type: "));

	//List of arrival types is found in the ArrivalType Enum

	final JComboBox<ArrivalType> arrival = new JComboBox<>(ArrivalType.values());
	topPanel.add(arrival);

	//Adds the listener for the initial arrival combo box
	ItemListener initListener = new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent e) {
		CardLayout card = (CardLayout) topCardContainer.getLayout();
		card.show(topCardContainer, e.getItem().toString());
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
	
	JPanel card4 = new JPanel();
	card4.setLayout(new FlowLayout());
	card4.add(new JLabel("Rate: rho="));
	final JTextField EBBrate = new JTextField(10);
	card4.add(EBBrate);
	card4.add(new JLabel("Decay: d="));
	final JTextField EBBdecay = new JTextField(10);
	card4.add(EBBdecay);
	card4.add(new JLabel("Prefactor: M="));
	final JTextField EBBprefactor = new JTextField(10);
	card4.add(EBBprefactor);

	JPanel card5 = new JPanel();
	card5.setLayout(new FlowLayout());
	card5.add(new JLabel("Token Rate: rho="));
	final JTextField STBrate = new JTextField(10);
	card5.add(STBrate);
	card5.add(new JLabel("Bucket Size: b="));
	final JTextField STBbucket = new JTextField(10);
	card5.add(STBbucket);
	card5.add(new JLabel("maxTheta (leave blank if there is none): t="));
	final JTextField STBmaxTheta = new JTextField(10);
	card5.add(STBmaxTheta);

	
	topCardContainer.add(card1, ArrivalType.CONSTANT_RATE.toString());
	topCardContainer.add(card2, ArrivalType.EXPONENTIAL.toString());
	topCardContainer.add(card3, ArrivalType.POISSON.toString());
	topCardContainer.add(card4, ArrivalType.EBB.toString());
	topCardContainer.add(card5, ArrivalType.STATIONARYTB.toString());

	mainPanel.add(topPanel);
	mainPanel.add(topCardContainer);
	((CardLayout) topCardContainer.getLayout()).show(topCardContainer, ArrivalType.CONSTANT_RATE.toString());

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
	ok.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		output = APPROVE_OPTION;

		ArrayList<Integer> route = cardStack.getRoute(new ArrayList<Integer>(0));

		//Gets the priorities from the CardStack
		ArrayList<Integer> priorities = new ArrayList<Integer>(0);
		try {
		    priorities = cardStack.getPriorities(priorities);
		} catch (NumberFormatException exc) {
		    System.out.println("Priorities must be integers.");
		    return;
		}


		if (arrival.getSelectedItem() == ArrivalType.CONSTANT_RATE) {
		    //uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
		    double rate = 0;
                    try {
                        rate = Double.valueOf(constantRate.getText());
                    } catch (NumberFormatException exc) {
                        System.out.println("Rate must be a number!");
                        return;
                    }
                    if (rate < 0) {
                        System.out.println("Constant rate must be positive!");
                        return;
                    }
                    
                    flow_arrival = ArrivalFactory.buildConstantRate(rate);
		}

		if (arrival.getSelectedItem() == ArrivalType.EXPONENTIAL) {
		    //uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
		    //ExponentialSigma rho = null;
                    double rate = 0;
                    try {
                        rate = Double.valueOf(exponentialRate.getText());
                    } catch(NumberFormatException exc) {
                        System.out.println("The rate must be a number!");
                        return;
                    }                        
                    try {
                        flow_arrival = ArrivalFactory.buildExponentialRate(rate);
                    } catch (BadInitializationException ex) {
                        System.out.println("The rate must be positive!");
                        return;
                    }
		}
		if (arrival.getSelectedItem() == ArrivalType.POISSON) {
		    //uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
//		    SymbolicFunction sigma = null;
			double mu = 0;
		    try {
		    	mu = Double.valueOf(poissonIntensity.getText());
		    } catch (NumberFormatException exc) {
		    	System.out.println("The intensity mu must be a number");
		    	return;
		    }

		    //defines the rho-part
	    	FlowEditor dialog = new FlowEditor("Define Increment", vertices, nw, snc, false);
			int output = dialog.showFlowEditor();
			if (output == FlowEditor.APPROVE_OPTION) {
				if (dialog.getEditedArrival() != null) {
					flow_arrival = ArrivalFactory.buildPoissonRate(dialog.getEditedArrival().getRho(), mu);
				} else {
					System.out.println("The increment process was not defined correctly.");
					return;
				}
			}
        }
		
		if (arrival.getSelectedItem() == ArrivalType.EBB) {
		    //uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
                    double rate = 0;
                    try {
                        rate = Double.valueOf(EBBrate.getText());
                    } catch(NumberFormatException exc) {
                        System.out.println("The rate must be a number!");
                        return;
                    }
                    double decay = 0;
                    try {
                    	decay = Double.valueOf(EBBdecay.getText());
                    } catch(NumberFormatException exc) {
                    	System.out.println("The decay must be a number!");
                    	return;
                    }
                    double prefactor = 0;
                    try {
                    	prefactor = Double.valueOf(EBBprefactor.getText());
                    } catch(NumberFormatException exc) {
                    	System.out.println("The prefactor must be a number!");
                    	return;
                    }
                    
                    try {
                    	flow_arrival = ArrivalFactory.buildEBB(rate,decay,prefactor);
                    } catch (BadInitializationException ex) {
                        System.out.println("The decay and prefactor must be positive!");
                        return;
                    }
		}
		
		if (arrival.getSelectedItem() == ArrivalType.STATIONARYTB) {
		    //uses the flow-constructor to submit information to main GUI. Do not use this flow directly!
                    double rate = 0;
                    try {
                        rate = Double.valueOf(STBrate.getText());
                    } catch(NumberFormatException exc) {
                        System.out.println("The rate must be a number!");
                        return;
                    }
                    double bucket = 0;
                    try {
                    	bucket = Double.valueOf(STBbucket.getText());
                    } catch(NumberFormatException exc) {
                    	System.out.println("The prefactor must be a number!");
                    	return;
                    }
                    double maxTheta = Double.POSITIVE_INFINITY;
                    try {
                    	maxTheta = Double.valueOf(STBmaxTheta.getText());
                    } catch(NumberFormatException exc) {
                    	System.out.println("Maximal theta set to infinity."); 
                    }
                    
                    try {
                    	flow_arrival = ArrivalFactory.buildStationaryTB(rate, bucket, maxTheta);
                    } catch (BadInitializationException ex) {
                        System.out.println("The decay and prefactor must be positive!");
                        return;
                    }
		}
		
		if(flow_adding) {
                    try {
                        snc.invokeCommand(new AddFlowCommand(aliasField.getText(), flow_arrival, route, priorities, -1, snc));
                    } catch(NetworkActionException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

		dispose();
	    }
	});

	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		output = CANCEL_OPTION;
		dispose();
	    }

	});
	buttonPanel.add(ok);
	buttonPanel.add(cancel);
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

    /**
     *
     * @return
     */
        public int showFlowEditor() {
	setVisible(true);
	return output;
    }

	//Getter and Setter

    /**
     *
     * @return
     */
        public Arrival getEditedArrival() {
	return flow_arrival;
    }

    //Helper Classes
    /**
     * Allows the user to set a route through the network. Visiting the same
     * node multiple times is allowed (though not encouraged). The interface
     * consists of cards, which are linked to a combo-box. Is no node selected
     * in the combo-box no next card will be displayed. If a node is selected a
     * new card - including its own combo-box - is displayed. This child-card is
     * a copy of its parent card, with the difference that the combo-box is
     * initialized with no node selected. The cards know about their child-card,
     * as well as their parent-card.
     *
     * @author Michael Beck
     *
     */
    private class CardStack extends JPanel {

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
	public CardStack(Integer[] nodes) {

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
	    delete.addActionListener(new ActionListener() {
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
	    ((CardLayout) cardPanel.getLayout()).show(cardPanel, NONE);
	    add(cardPanel);
	}

	public CardStack(Integer[] nodes, CardStack parent) {
	    this(nodes);
	    this.parent = parent;
	}

		//Methods
	public void addChild(CardStack child, int value) {
	    this.child = child;
	    cardPanel.add(child, Integer.toString(value));
	    ((CardLayout) cardPanel.getLayout()).show(cardPanel, Integer.toString(value));
	}

	public void removeChild() {
	    ((CardLayout) cardPanel.getLayout()).show(cardPanel, NONE);
	    cardPanel.removeAll();
	    cardPanel.add(emptyCard, NONE);
	    this.child = null;
	}

	public void deactivateParent() {
	    if (parent != null) {
		parent.deactivate();
	    }
	}

	public void activateParent() {
	    if (parent != null) {
		parent.activate();
	    }
	}

	public void deactivate() {
	    nodeBox.setEnabled(false);
	}

	public void activate() {
	    nodeBox.setEnabled(true);
	}

	public ArrayList<Integer> getRoute(ArrayList<Integer> list) {
	    if ((Integer) nodeBox.getSelectedItem() != null) {
		list.add((Integer) nodeBox.getSelectedItem());
	    }
	    if (child != null) {
		list = child.getRoute(list);
		return list;
	    } else {
		return list;
	    }
	}

	public ArrayList<Integer> getPriorities(ArrayList<Integer> priorities) {
	    if ((Integer) nodeBox.getSelectedItem() != null) {
		priorities.add(Integer.parseInt(priorityField.getText()));
	    }
	    if (child != null) {
		priorities = child.getPriorities(priorities);
		return priorities;
	    } else {
		return priorities;
	    }
	}

    }

    /**
     * Helper class for {@link CardStack}
     *
     * @author Michael Beck
     *
     */
    private class CardStackListener implements ItemListener {

		//Members
	private CardStack caller;
	private boolean childActive;
	private Integer[] nodes;

		//Constructor
	public CardStackListener(CardStack caller, Integer[] nodes) {
	    this.caller = caller;
	    this.nodes = nodes;
	    childActive = false;
	}

		//Methods
	@Override
	public void itemStateChanged(ItemEvent e) {

	    //Generate child card if a node is chosen and no child card had already been created
	    if (e.getItem() != CardStack.NONE && !childActive) {
		CardStack child = new CardStack(nodes, caller);
		caller.addChild(child, (Integer) e.getItem());
		childActive = true;
		caller.deactivateParent();
		pack();
	    } //Delete the child card, if a previous selected node is deleted
	    else if (e.getItem() == CardStack.NONE) {
		caller.removeChild();
		childActive = false;
		caller.activateParent();
		pack();
	    }
	}

	public void setChildActive(boolean ca) {
	    childActive = ca;
	}
    }
}
