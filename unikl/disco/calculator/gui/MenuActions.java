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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import unikl.disco.calculator.SNC;

/**
 *
 * @author Sebastian Henningsen
 */
public class MenuActions {

    static class LoadNetworkAction extends AbstractAction {

        public LoadNetworkAction(String name) {
            super(name);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            JFileChooser chooser = new JFileChooser();
            int opened = chooser.showOpenDialog(null);
            if (opened == JFileChooser.APPROVE_OPTION) {
                SNC.getInstance().loadNetwork(chooser.getSelectedFile());
                System.out.println("Load Network");
            }
        }

    }

    static class SaveNetworkAction extends AbstractAction {

        public SaveNetworkAction(String name) {
            super(name);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            JFileChooser chooser = new JFileChooser();
            int saved = chooser.showSaveDialog(null);
            if (saved == JFileChooser.APPROVE_OPTION) {
                SNC.getInstance().saveNetwork(chooser.getSelectedFile());
                System.out.println("Save Network");
            }
        }

    }

    static class ExitAction extends AbstractAction {

        MainWindow gui;

        public ExitAction(String name, MainWindow gui) {
            super(name);
            this.gui = gui;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            gui.close();
        }

    }

    static class UndoAction extends AbstractAction {

        public UndoAction(String name) {
            super(name);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            SNC.getInstance().undo();
            System.out.println("Undo");
        }

    }

    static class RedoAction extends AbstractAction {

        public RedoAction(String name) {
            super(name);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            SNC.getInstance().redo();
            System.out.println("Redo");
        }

    }
}
