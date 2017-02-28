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
package unikl.disco.misc;

import java.util.Stack;
import unikl.disco.calculator.commands.Command;

/**
 * This class keeps track of all operations performed (e.g. addNode, deleteFlow, ...) and provides
 * functionality to Undo/Redo past operations.
 * @author Sebastian Henningsen
 */
public class UndoRedoStack {
    private final Stack undoStack;
    private final Stack redoStack;
    
    public UndoRedoStack() {
        undoStack = new Stack();
        redoStack = new Stack();
    }
    
    public void undo() {
        if(!undoStack.empty()) {
            System.out.println("Undoing");
            Command c = (Command)undoStack.pop();
            redoStack.add(c);
            c.undo();
        }
    }
    
    public void redo() {
        if(!redoStack.empty()) {
            System.out.println("Redoing");
            Command c = (Command)redoStack.pop();
            undoStack.add(c);
            c.execute();
        }
    }
    
    public void insertIntoStack(Command c) {
        undoStack.add(c);
        redoStack.clear();
    }
}
