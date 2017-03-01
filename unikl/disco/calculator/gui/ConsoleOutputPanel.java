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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Relays the output to stdout and stderr and displays it on a {@link JTextArea}.
 * @author Sebastian Henningsen
 */
public class ConsoleOutputPanel {
    private final JScrollPane scrollPane;
    private final JTextArea textArea;
    
    /**
     * Constructs a new Panel.
     */
    public ConsoleOutputPanel() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        scrollPane = new JScrollPane(textArea);
        
    }
    
    /**
     * Redirects the standard output onto the text area.
     */
    public void redirectOut() {
        ConsoleOutputStream outputStream = new ConsoleOutputStream();
        System.setOut( new PrintStream(outputStream, true) );
    }
    
    /**
     * Redirects the error output onto the text area which is printed in the given color.
     * @param textColor Color in which error message should be printed.
     */
    public void redirectErr(Color textColor) {
        ConsoleOutputStream outputStream = new ConsoleOutputStream(textColor);
	System.setErr( new PrintStream(outputStream, true) );
    }
    
    /**
     * Returns the {@link JPanel} which holds the text area.
     * @return
     */
    public JScrollPane getPanel() {
        return scrollPane;
    }
    
    class ConsoleOutputStream extends OutputStream {
        private SimpleAttributeSet attributes;
        
        public ConsoleOutputStream(Color textColor) {
            attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, textColor);
        }

        public ConsoleOutputStream() {}

        @Override
        public void write(int i) throws IOException {
            updateTextArea(String.valueOf((char)i));
        }
        
        private void updateTextArea(final String text) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // redirects data to the text area
                    textArea.append(text);
                    // scrolls the text area to the end of data
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            });
        }
            
    }
}
