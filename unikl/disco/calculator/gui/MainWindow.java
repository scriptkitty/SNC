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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * The main window of the GUI. This is where all the initialization and composition of
 * different components takes place.
 * @author Sebastian Henningsen
 */
public class MainWindow {

    private JFrame mainFrame;
    private MenuBar menuBar;
    private VertexTablePanel vertexTablePanel;
    private FlowTablePanel flowTablePanel;
    private ConsoleOutputPanel consolePanel;
    private GridBagLayout layoutManager;
    private JSplitPane horizontalSplitPane;
    private JSplitPane verticalSplitPane;
    private JSplitPane rightHorizontalSplitPane;
    private ControlPanel controllerPanel;
    private NetworkVisualizationPanel visPanel;

    /**
     * Creates all necessary components and adds them to the main JFrame.
     * Since this method contains Swing components, it should only be called inside
     * the event queue. For example (from {@link SNC}):
     * <code>Runnable runnable = new Runnable() {
            @Override
            public void run() {
                main.createGUI();
            }
        };
        EventQueue.invokeLater(runnable);
        </code>
     */
    public void createGUI() {
        mainFrame = new JFrame("Disco Stochastic Network Calculator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        menuBar = new MenuBar(this);
        mainFrame.setJMenuBar(menuBar.getMenuBar());

        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        mainFrame.add(horizontalSplitPane);
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        horizontalSplitPane.setBottomComponent(verticalSplitPane);

        vertexTablePanel = new VertexTablePanel();
        flowTablePanel = new FlowTablePanel();
        JPanel tables = new JPanel();
        tables.add(vertexTablePanel.getPanel());
        tables.add(flowTablePanel.getPanel());
        horizontalSplitPane.setTopComponent(tables);

        rightHorizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        verticalSplitPane.setBottomComponent(rightHorizontalSplitPane);

        consolePanel = new ConsoleOutputPanel();
        consolePanel.redirectOut();
        consolePanel.redirectErr(Color.RED);
        rightHorizontalSplitPane.setBottomComponent(consolePanel.getPanel());

        visPanel = new NetworkVisualizationPanel(new Dimension(300, 300));
        rightHorizontalSplitPane.setTopComponent(visPanel.getPanel());

        controllerPanel = new ControlPanel();
        verticalSplitPane.setTopComponent(controllerPanel.getPanel());

        // Layout Part: Set the Layout Manager and arrange the components
        layoutManager = new GridBagLayout();
        //mainFrame.setLayout(layoutManager);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /**
     * Closes the main window.
     */
    public void close() {
        mainFrame.dispose();
    }

    /**
     * Converts a map of {@link Displayable} elements to an array. This is used
     * to convert from the output of SNC to {@link JComboBox}.
     * @param <T>
     * @param map
     * @return
     */
    public static <T extends Displayable> Displayable[] convertDisplayables(Map<Integer, T> map) {
        Displayable[] selectables = new Displayable[map.size()];
        int counter = 0;
        for (Map.Entry<Integer, T> entry : map.entrySet()) {
            T val = entry.getValue();
            selectables[counter] = val;
            counter++;
        }
        return selectables;
    }
}
