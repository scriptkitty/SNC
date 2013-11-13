/* Provide a generic Pair class.
 *
 * Yes, I know there's something similar in JUNG, but as long as JUNG is stuck
 * in Java 1.4, I prefer a version that doesn't hurt the linter.
 *
 * @author Nicos Gollan <gollan@informatik.uni-kl.de>
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
