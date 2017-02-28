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

/**
 *
 * @author ngollan
 */
public class Pair<T1,T2> {
	private T1 first;
	private T2 second;

	public Pair(T1 f, T2 s) {
		first = f;
		second = s;
	}

	public T1 getFirst() {
		return first;
	}

	public T2 getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o){
		if(o == null || o.getClass() != this.getClass()) {
			return false;
		}

		Pair p = new Pair<Object,Object>(((Pair)o).getFirst(), ((Pair)o).getSecond());
		return (this.first != null? this.first.equals(p.first) : p.first == null)
				&& (this.second != null? this.second.equals(p.second) : p.second == null);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.first != null ? this.first.hashCode() : 0);
		hash = 83 * hash + (this.second != null ? this.second.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "(" + String.valueOf(first) + ", " + String.valueOf(second) + ")";
	}
}
