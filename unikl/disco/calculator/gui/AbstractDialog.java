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
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import unikl.disco.calculator.SNC;

/**
 *
 * @author Sebastian Henningsen
 */
public abstract class AbstractDialog extends JDialog {
    final SNC snc;
    String title;
    JPanel leftPanel, rightPanel, lowerPanel;
    
    
    AbstractDialog(String title, final SNC snc) {
        super();
        this.title = title;
        this.snc = snc;
        //Constructs the dialog
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setTitle(title);
        
        setLayout(new GridLayout(0,2));

    }
    
    JComboBox<ComboBoxItem> createComboBox(Map<Integer, ? extends Displayable> objectMap) {
        // TODO: Use IDs, if there is no alias?
        List<ComboBoxItem> flowList = new ArrayList<>();
        for(Map.Entry<Integer, ? extends Displayable> entry : objectMap.entrySet()) {
            flowList.add(new ComboBoxItem(entry.getKey(), entry.getValue().getAlias()));
        }
        return new JComboBox<>(flowList.toArray(new ComboBoxItem[0]));
    }
    
    JButton createExitButton() {
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });
        return exitButton;
    }
    
}
