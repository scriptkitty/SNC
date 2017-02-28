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
