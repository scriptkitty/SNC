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

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author Sebastian Henningsen
 */
public class MenuBar {
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    
    private final JMenuItem loadFileItem;
    private final JMenuItem saveFileItem;
    private final JMenuItem exitItem;
    
    private final JMenuItem undoMenuItem;
    private final JMenuItem redoMenuItem;
    
    MenuBar(MainWindow mainWindow) {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        loadFileItem = new JMenuItem();
        loadFileItem.setAction(new MenuActions.LoadNetworkAction("Load Network"));
        
        saveFileItem = new JMenuItem();
        saveFileItem.setAction(new MenuActions.SaveNetworkAction("Save Network"));
        
        exitItem = new JMenuItem();
        exitItem.setAction(new MenuActions.ExitAction("Exit", mainWindow));
        
        fileMenu.add(loadFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        undoMenuItem = new JMenuItem();
        undoMenuItem.setAction(new MenuActions.UndoAction("Undo"));
        
        redoMenuItem = new JMenuItem();
        redoMenuItem.setAction(new MenuActions.RedoAction("Redo"));

        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        menuBar.add(editMenu);
        
        
        
    }
    
    public JMenuBar getMenuBar() {
        return menuBar;
    }
}
