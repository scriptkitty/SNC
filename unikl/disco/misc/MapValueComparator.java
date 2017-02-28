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

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator that does not compare two objects directly but uses them
 * as keys into a given map and comparing the resulting pair of values.
 * 
 * @author Frank A. Zdarsky
 * 
 * @internal
 *FIXME This is a royal pain to make it check out with Java 5+'s idea of
 * generics
 */
public class MapValueComparator<T1,T2> implements Comparator<T1> {
	private Map<T1,T2> map;
	
	/**
	 * Creates an instance of <code>MapValueComparator</code>.
	 * The values in the provided map <code>map</code> must implement the
	 * interface <code>Comparable</code>.
	 * @param map
	 */
	public MapValueComparator(Map<T1,T2> map) {
		this.map = map;
	}
	
	/**
	 * Compares two objects indirectly by using them as keys into a map
	 * and comparing the resulting pair of values.
	 * @param o1
	 * @param o2
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second 
	 */
	@Override
	@SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
        return ((Comparable) map.get(o1)).compareTo(map.get(o2));
    }		
}
