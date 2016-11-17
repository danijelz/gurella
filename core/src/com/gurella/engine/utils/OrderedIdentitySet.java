package com.gurella.engine.utils;

import java.util.Comparator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * @author Nathan Sweet
 */
// TODO Poolable, array pool
public class OrderedIdentitySet<T> extends IdentitySet<T> {
	final ArrayExt<T> items;
	private OrderedIdentitySetIterator<T> iterator1, iterator2;

	public OrderedIdentitySet() {
		items = new ArrayExt<T>();
	}

	public OrderedIdentitySet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		items = new ArrayExt<T>(capacity);
	}

	public OrderedIdentitySet(int initialCapacity) {
		super(initialCapacity);
		items = new ArrayExt<T>(capacity);
	}

	public OrderedIdentitySet(OrderedIdentitySet<T> set) {
		super(set);
		items = new ArrayExt<T>(capacity);
		items.addAll(set.items);
	}

	@Override
	public boolean add(T key) {
		if (super.add(key)) {
			items.add(key);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(T key) {
		if (super.remove(key)) {
			items.removeValue(key, true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void clear(int maximumCapacity) {
		super.clear(maximumCapacity);
		items.clear();
	}

	@Override
	public void clear() {
		super.clear();
		items.clear();
	}

	public ImmutableArray<T> orderedItems() {
		return items.immutable();
	}

	public void appendTo(Array<? super T> out) {
		out.addAll(items);
	}

	public void sort(Comparator<? super T> comparator) {
		items.sort(comparator);
	}

	public void sort() {
		items.sort();
	}

	@Override
	public T first() {
		return items.first();
	}

	public T get(int index) {
		return items.get(index);
	}

	public T peek() {
		return items.peek();
	}

	public int indexOf(T value) {
		return items.indexOf(value, true);
	}

	public void reverse() {
		items.reverse();
	}

	public T random() {
		return items.random();
	}

	public T pop() {
		T value = peek();
		remove(value);
		return value;
	}

	public T removeIndex(int index) {
		T value = items.get(index);
		remove(value);
		return value;
	}

	public void setIndex(int newIndex, T value) {
		items.removeValue(value, true);
		items.insert(newIndex, value);
	}

	@Override
	public OrderedIdentitySetIterator<T> iterator() {
		if (iterator1 == null) {
			iterator1 = new OrderedIdentitySetIterator<T>(this);
			iterator2 = new OrderedIdentitySetIterator<T>(this);
		}
		if (!iterator1.valid) {
			iterator1.reset();
			iterator1.valid = true;
			iterator2.valid = false;
			return iterator1;
		}
		iterator2.reset();
		iterator2.valid = true;
		iterator1.valid = false;
		return iterator2;
	}

	public void toArray(T[] arr) {
		T[] temp = items.items;
		System.arraycopy(temp, 0, arr, 0, Math.min(arr.length, temp.length));
	}

	@Override
	public void reset() {
		super.reset();
		items.reset();
	}

	@Override
	public int hashCode() {
		return 31 + items.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != OrderedIdentitySet.class) {
			return false;
		}
		OrderedIdentitySet<?> other = (OrderedIdentitySet<?>) obj;
		return items.equals(other.items);
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "{}";
		}
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		Array<T> keys = this.items;
		for (int i = 0, n = keys.size; i < n; i++) {
			T key = keys.get(i);
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(key);
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class OrderedIdentitySetIterator<T> extends IdentitySetIterator<T> {
		private Array<T> items;

		public OrderedIdentitySetIterator(OrderedIdentitySet<T> set) {
			super(set);
			items = set.items;
		}

		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = set.size > 0;
		}

		@Override
		public T next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			T key = items.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < set.size;
			return key;
		}

		@Override
		public void remove() {
			if (nextIndex < 0) {
				throw new IllegalStateException("next must be called before remove.");
			}
			nextIndex--;
			set.remove(items.get(nextIndex));
		}
	}
}
