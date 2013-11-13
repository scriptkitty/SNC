/*
 *  (c) 2005,2006 Frank A. Zdarsky, disco | Distributed Computer Systems Lab
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

package unikl.disco.misc;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A tiny collection of convenience methods useful in dealing with sets but not
 * provided directly by Sun's set classes.
 * 
 * @author Frank A. Zdarsky
 */
public class SetUtils {
	/**
	 * Returns the set difference between the set <code>s1</code> and the set <code>s2</code>.
	 * @param s1
	 * @param s2
	 * @return the difference set
	 */
    public static <T> Set<T> getDifference(Set<T> s1, Set<T> s2) {
    	Set<T> result = new HashSet<T>(s1);
    	result.removeAll(s2);
    	return result;
    }
    
    /**
     * Returns the intersection of set <code>s1</code> and set <code>s2</code>.
     * @param s1 a set
     * @param s2 another set
     * @return the intersection set
     */
    public static <T> Set<T> getIntersection(Set<T> s1, Set<T> s2) {
		if(s1==null || s2==null)
			return new HashSet<T>();

    	Set<T> result = new HashSet<T>(s1);
    	result.retainAll(s2);
    	return result;
    }
    
    /**
     * Returns the intersection of all sets contained in the list <code>sets</code>.
     * @param sets a list of sets
     * @return the intersection of all sets
     */
    public static <T> Set<T> getIntersection(List<Set<T>> sets) {
    	Set<T> result = new HashSet<T>();    	
    	Iterator<Set<T>> iter = sets.iterator();
    	if (iter.hasNext()) {
    		result.addAll(iter.next());
        	for (; iter.hasNext(); ) {
        		result.retainAll(iter.next());
        	}    		
    	}
    	return result;
    }

    /**
     * Returns the union of set <code>s1</code> and set <code>s2</code>.
     * @param s1 a set
     * @param s2 another set
     * @return the union set
     */
    public static <T> Set<T> getUnion(Set<T> s1, Set<T> s2) {
    	Set<T> result = new HashSet<T>(s1);
   		result.addAll(s2);
    	return result;
    }
    
    /**
     * Returns the union of all sets contained in the list <code>sets</code>.
     * @param sets a list of sets
     * @return the union of all sets
     */
    public static <T> Set<T> getUnion(List<Set<T>> sets) {
    	Set<T> result = new HashSet<T>();
    	for(Set<T> set : sets) {
    		result.addAll(set);
    	}
    	return result;
    }
    
    /**
     * Creates a map containing each element of the <code>Set set</code> as key
     * and the <coded>Object value</code> as corresponding value.
     * @param set the set whose elements will be the keys of entries in the map
     * @param value the value of each of the map's entries
     * @return a map
     */
//    public static Map createMap(Set set, Object value) {
//    	Map map = new HashMap(set.size());
//    	for (Iterator iter = set.iterator(); iter.hasNext(); ) {
//    		map.put(iter.next(), value);
//    	}
//    	return map;
//    }
}
