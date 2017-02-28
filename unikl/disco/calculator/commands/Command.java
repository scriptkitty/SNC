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
package unikl.disco.calculator.commands;

/**
 * The command abstraction interface which is used to communicate with the controller
 * and to implement a Undo/Redo mechanism. See also {@link UndoRedoStack}
 * @author Sebastian Henningsen
 */
public interface Command {

    /**
     * The main function of the command, also used for redoing the command.
     */
    public void execute();

    /**
     * Used to undo the actions that were performed during execution of the command.
     */
    public void undo();
}
