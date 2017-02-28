/*
 *  (c) 2017 Michael A. Beck, Sebastian Henningsen
 *  		disco | Distributed Computer Systems Lab
 *  		University of Kaiserslautern, Germany
 *  All Rights Reserved.
 *
 * This software is work in progress and is released in the hope that it will
 * be useful to the scientific community. It is provided "as is" without
 * express or implied warranty, including but not limited to the correctness
 * of the code or its suitability for any particular purpose.
 *
 * This software is provided under the MIT License, however, we would 
 * appreciate it if you contacted the respective authors prior to commercial use.
 *
 * If you find our software useful, we would appreciate if you mentioned it
 * in any publication arising from the use of this software or acknowledge
 * our work otherwise. We would also like to hear of any fixes or useful
 */
package unikl.disco.calculator.gui;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The menu bar at the top of the main window. Note that this class only deals with the
 * visualization, the actions are in {@link MenuActions}
 * @author Sebastian Henningsen
 */
public class MenuBar {
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    private final JMenu helpMenu;
    
    private final JMenuItem loadFileItem;
    private final JMenuItem saveFileItem;
    private final JMenuItem exitItem;
    
    private final JMenuItem undoMenuItem;
    private final JMenuItem redoMenuItem;
    
    private final JMenuItem aboutMenuItem;
    
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
        
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        aboutMenuItem = new JMenuItem();
        aboutMenuItem.setAction(new MenuActions.AboutAction("About"));
        
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
    }
    
    /**
     * Returns the menu bar.
     * @return
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }
}
