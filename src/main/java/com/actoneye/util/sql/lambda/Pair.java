package com.actoneye.util.sql.lambda;

/**
 * Two Entities of any type
 */

public class Pair<T, K> {

	private T v1;
	private K v2;

	public Pair(T car, K cdr) {
		this.v1 = car;
		this.v2 = cdr;
	}

	/**
	 * Creates a new Pair object with given Elements and returns
	 */
	public static <T, K> Pair<T, K> cons(T car, K cdr) {
		return new Pair<T, K>(car, cdr);
	}

	public T getV1() {
		return v1;
	}

	public K getV2() {
		return v2;
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(v1).append('=').append(v2).append(')').toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Pair))
			return false;

		Pair<T, K> pair = (Pair<T, K>) o;

		if (v1 != null ? !v1.equals(pair.v1) : pair.v1 != null)
			return false;
		if (v2 != null ? !v2.equals(pair.v2) : pair.v2 != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		return result;
	}

}
