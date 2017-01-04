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

/**
 * This interface is used to de-couple the GUI from the model.
 * In most cases, the front-end only uses a fraction of the information provided by the model.
 * Therefore, classes from the model which are handled by the GUI (i.e. {@link Vertex} and {@link Flow})
 * implement this interface, which enables the GUI to reuse to use more abstract code.
 * @author Sebastian Henningsen
 */
public interface Displayable {

    /**
     * Returns the alias of the element.
     * @return
     */
    public String getAlias();

    /**
     * Returns the ID of the element.
     * @return
     */
    public int getID();
}
