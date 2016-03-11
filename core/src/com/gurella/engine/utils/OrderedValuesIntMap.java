package com.gurella.engine.utils;

import com.badlogic.gdx.utils.IntMap;

//TODO Poolable, array pool
public class OrderedValuesIntMap<V> extends IntMap<V> {
	private final ArrayExt<V> values;

	public OrderedValuesIntMap() {
		super();
		values = new ArrayExt<V>();
	}

	public OrderedValuesIntMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		values = new ArrayExt<V>(initialCapacity);
	}

	public OrderedValuesIntMap(int initialCapacity) {
		super(initialCapacity);
		values = new ArrayExt<V>(initialCapacity);
	}

	public OrderedValuesIntMap(IntMap<? extends V> map) {
		super(map);
		values = new ArrayExt<V>(map.size);
	}

	@Override
	public V put(int key, V value) {
		if (!containsKey(key)) {
			values.add(value);
		}
		return super.put(key, value);
	}

	@Override
	public V remove(int key) {
		if (containsKey(key)) {
			V removed = super.remove(key);
			values.removeValue(removed, true);
			return removed;
		} else {
			return null;
		}
	}

	@Override
	public void clear() {
		super.clear();
		values.clear();
	}

	@Override
	public void clear(int maximumCapacity) {
		super.clear(maximumCapacity);
		values.clear();
	}

	public ImmutableArray<V> orderedValues() {
		return values.immutable();
	}

	@Override
	public int hashCode() {
		return 31 + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof OrderedValuesIntMap)) {
			return false;
		}
		OrderedValuesIntMap<?> other = (OrderedValuesIntMap<?>) obj;
		return values.equals(other.values);
	}

}
