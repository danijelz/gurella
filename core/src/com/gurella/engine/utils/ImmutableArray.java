package com.gurella.engine.utils;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterable;

/**
 * Wrapper class to treat {@link Array} objects as if they were immutable.
 * However, note that the values could be modified if they are mutable.
 * 
 * @author David Saltares
 */
public class ImmutableArray<T> implements Iterable<T> {
	private static final ImmutableArray<Object> empty = new ImmutableArray<Object>(new Array<Object>());

	private final Array<T> array;
	private ArrayIterable<T> iterable;

	public static <T> ImmutableArray<T> with(Array<T> array) {
		return new ImmutableArray<T>(array);
	}

	public ImmutableArray(Array<T> array) {
		this.array = array;
	}

	public int size() {
		return array.size;
	}

	public T get(int index) {
		return array.get(index);
	}

	public boolean contains(T value, boolean identity) {
		return array.contains(value, identity);
	}

	public int indexOf(T value, boolean identity) {
		return array.indexOf(value, identity);
	}

	public int lastIndexOf(T value, boolean identity) {
		return array.lastIndexOf(value, identity);
	}

	public T peek() {
		return array.peek();
	}

	public T first() {
		return array.first();
	}

	public T random() {
		return array.random();
	}

	public T[] toArray() {
		return array.toArray();
	}

	public <V> V[] toArray(Class<?> type) {
		return array.toArray(type);
	}

	public void appendTo(Array<? super T> out) {
		out.addAll(array);
	}

	public void appendTo(Array<? super T> out, int start, int count) {
		out.addAll(array, start, count);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof ImmutableArray)) {
			return false;
		}
		ImmutableArray<?> other = (ImmutableArray<?>) object;
		return array.equals(other.array);
	}

	@Override
	public int hashCode() {
		return 31 * array.hashCode();
	}

	@Override
	public String toString() {
		return array.toString();
	}

	public String toString(String separator) {
		return array.toString(separator);
	}

	@Override
	public Iterator<T> iterator() {
		if (iterable == null) {
			iterable = new ArrayIterable<T>(array, false);
		}

		return iterable.iterator();
	}

	@SuppressWarnings("unchecked")
	public static <T> ImmutableArray<T> empty() {
		return (ImmutableArray<T>) empty;
	}
}