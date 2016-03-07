package com.gurella.engine.utils;

import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StringBuilder;

/** 
 * @author Nathan Sweet 
 * */
public class IdentityOrderedSet<T> extends IdentitySet<T> {
	final ArrayExt<T> items;
	private IdentityOrderedSetIterator<T> iterator1, iterator2;

	public IdentityOrderedSet() {
		items = new ArrayExt<T>();
	}

	public IdentityOrderedSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		items = new ArrayExt<T>(capacity);
	}

	public IdentityOrderedSet(int initialCapacity) {
		super(initialCapacity);
		items = new ArrayExt<T>(capacity);
	}

	public IdentityOrderedSet(IdentityOrderedSet<T> set) {
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

	@Override
	public IdentityOrderedSetIterator<T> iterator() {
		if (iterator1 == null) {
			iterator1 = new IdentityOrderedSetIterator<T>(this);
			iterator2 = new IdentityOrderedSetIterator<T>(this);
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

	@Override
	public String toString() {
		if (size == 0)
			return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		Array<T> keys = this.items;
		for (int i = 0, n = keys.size; i < n; i++) {
			T key = keys.get(i);
			if (i > 0)
				buffer.append(", ");
			buffer.append(key);
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class IdentityOrderedSetIterator<T> extends IdentitySetIterator<T> {
		private Array<T> items;

		public IdentityOrderedSetIterator(IdentityOrderedSet<T> set) {
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
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			T key = items.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < set.size;
			return key;
		}

		@Override
		public void remove() {
			if (nextIndex < 0)
				throw new IllegalStateException("next must be called before remove.");
			nextIndex--;
			set.remove(items.get(nextIndex));
		}
	}
}
