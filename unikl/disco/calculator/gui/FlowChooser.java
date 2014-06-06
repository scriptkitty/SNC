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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import unikl.disco.calculator.network.Flow;

/**
 * A dialog for choosing flows.
 * @author Michael Beck
 *
 */
public class FlowChooser extends JDialog {

	//Members
	
	private static final long serialVersionUID = -7469913174921379065L;
	
	static final int CANCEL_OPTION = 0;
	static final int APPROVE_OPTION = 1;
	static final int ERROR_OPTION = 2;
	private int output = 0;
	private Flow flow;
	
	//Constructor
	
	public FlowChooser(String title, final Map<Integer, Flow> flows){
		
		//Constructs the dialog
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		//******************
		//Adds the top panel
		//******************
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Choose the flow-ID to be removed:"));
		
		Integer[] array = flows.keySet().toArray(new Integer[0]);
		final JComboBox<Integer> flowBox = new JComboBox<Integer>(array);
		flowBox.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int id = (Integer) arg0.getItem();
				flow = flows.get(id);
			}
			
		});
		flowBox.setSelectedIndex(-1);
		topPanel.add(flowBox);
		mainPanel.add(topPanel);
		
		//*********************
		//Adds the exit buttons
		//*********************
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(flowBox.getSelectedIndex() == -1) output = CANCEL_OPTION;
				else output = APPROVE_OPTION;
				
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
		bottomPanel.add(ok); bottomPanel.add(cancel);
		mainPanel.add(bottomPanel);

		add(mainPanel);
		
		//***************
		//Finishing touch
		//***************
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);		
	}
	
	//Methods 
	
	public int showFlowChooser(){
		setVisible(true);
		return output;
	}
	
	//Getter and Setter
	
	public Flow getSelectedFlow(){
		return flow;
	}
}
