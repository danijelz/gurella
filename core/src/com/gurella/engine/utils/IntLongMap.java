package com.gurella.engine.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * An unordered map where the keys and values are ints. This implementation is a cuckoo hash map using 3 hashes, random
 * walking, and a small stash for problematic keys. No allocation is done except when growing the table size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit
 * slower, depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have
 * to rehash to the next higher POT size.
 * 
 * @author Nathan Sweet
 */
public class IntLongMap implements Iterable<IntLongMap.Entry>, Container {
	private static final int PRIME1 = 0xb4b82e39;
	private static final int PRIME2 = 0xced1c241;
	private static final int EMPTY = 0;

	public int size;

	int[] keyTable;
	long[] valueTable;
	int capacity, stashSize;
	long zeroValue;
	boolean hasZeroValue;

	private float loadFactor;
	private int hashShift, mask, threshold;
	private int stashCapacity;
	private int pushIterations;

	private Entries entries1, entries2;
	private Values values1, values2;
	private Keys keys1, keys2;

	/**
	 * Creates a new map with an initial capacity of 32 and a load factor of 0.8. This map will hold 25 items before
	 * growing the backing table.
	 */
	public IntLongMap() {
		this(32, 0.8f);
	}

	/**
	 * Creates a new map with a load factor of 0.8. This map will hold initialCapacity * 0.8 items before growing the
	 * backing table.
	 */
	public IntLongMap(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity *
	 * loadFactor items before growing the backing table.
	 */
	public IntLongMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		if (initialCapacity > 1 << 30) {
			throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		}
		capacity = MathUtils.nextPowerOfTwo(initialCapacity);

		if (loadFactor <= 0) {
			throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
		}
		this.loadFactor = loadFactor;

		threshold = (int) (capacity * loadFactor);
		mask = capacity - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(capacity);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(capacity)) * 2);
		pushIterations = Math.max(Math.min(capacity, 8), (int) Math.sqrt(capacity) / 8);

		keyTable = new int[capacity + stashCapacity];
		valueTable = new long[keyTable.length];
	}

	/** Creates a new map identical to the specified map. */
	public IntLongMap(IntLongMap map) {
		this(map.capacity, map.loadFactor);
		stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		size = map.size;
		zeroValue = map.zeroValue;
		hasZeroValue = map.hasZeroValue;
	}

	public void put(int key, long value) {
		if (key == 0) {
			zeroValue = value;
			if (!hasZeroValue) {
				hasZeroValue = true;
				size++;
			}
			return;
		}

		int[] keyTable = this.keyTable;

		// Check for existing keys.
		int index1 = key & mask;
		int key1 = keyTable[index1];
		if (key == key1) {
			valueTable[index1] = value;
			return;
		}

		int index2 = hash2(key);
		int key2 = keyTable[index2];
		if (key == key2) {
			valueTable[index2] = value;
			return;
		}

		int index3 = hash3(key);
		int key3 = keyTable[index3];
		if (key == key3) {
			valueTable[index3] = value;
			return;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				valueTable[i] = value;
				return;
			}
		}

		// Check for empty buckets.
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	public void putAll(IntLongMap map) {
		for (Entry entry : map.entries()) {
			put(entry.key, entry.value);
		}
	}

	/** Skips checks for existing keys. */
	private void putResize(int key, long value) {
		if (key == 0) {
			zeroValue = value;
			hasZeroValue = true;
			return;
		}

		// Check for empty buckets.
		int index1 = key & mask;
		int key1 = keyTable[index1];
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		int index2 = hash2(key);
		int key2 = keyTable[index2];
		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		int index3 = hash3(key);
		int key3 = keyTable[index3];
		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) {
				resize(capacity << 1);
			}
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	private void push(int insertKey, long insertValue, int index1, int key1, int index2, int key2, int index3,
			int key3) {
		int tempInsertKey = insertKey;
		long tempInsertValue = insertValue;
		int tempIndex1 = index1;
		int tempKey1 = key1;
		int tempIndex2 = index2;
		int tempKey2 = key2;
		int tempIndex3 = index3;
		int tempKey3 = key3;
		int[] keyTable = this.keyTable;
		long[] valueTable = this.valueTable;
		int mask = this.mask;

		// Push keys until an empty bucket is found.
		int evictedKey;
		long evictedValue;
		int i = 0, pushIterations = this.pushIterations;
		do {
			// Replace the key and value for one of the hashes.
			switch (MathUtils.random(2)) {
			case 0:
				evictedKey = tempKey1;
				evictedValue = valueTable[tempIndex1];
				keyTable[tempIndex1] = tempInsertKey;
				valueTable[tempIndex1] = tempInsertValue;
				break;
			case 1:
				evictedKey = tempKey2;
				evictedValue = valueTable[tempIndex2];
				keyTable[tempIndex2] = tempInsertKey;
				valueTable[tempIndex2] = tempInsertValue;
				break;
			default:
				evictedKey = tempKey3;
				evictedValue = valueTable[tempIndex3];
				keyTable[tempIndex3] = tempInsertKey;
				valueTable[tempIndex3] = tempInsertValue;
				break;
			}

			// If the evicted key hashes to an empty bucket, put it there and stop.
			tempIndex1 = evictedKey & mask;
			tempKey1 = keyTable[tempIndex1];
			if (tempKey1 == EMPTY) {
				keyTable[tempIndex1] = evictedKey;
				valueTable[tempIndex1] = evictedValue;
				if (size++ >= threshold) {
					resize(capacity << 1);
				}
				return;
			}

			tempIndex2 = hash2(evictedKey);
			tempKey2 = keyTable[tempIndex2];
			if (tempKey2 == EMPTY) {
				keyTable[tempIndex2] = evictedKey;
				valueTable[tempIndex2] = evictedValue;
				if (size++ >= threshold) {
					resize(capacity << 1);
				}
				return;
			}

			tempIndex3 = hash3(evictedKey);
			tempKey3 = keyTable[tempIndex3];
			if (tempKey3 == EMPTY) {
				keyTable[tempIndex3] = evictedKey;
				valueTable[tempIndex3] = evictedValue;
				if (size++ >= threshold) {
					resize(capacity << 1);
				}
				return;
			}

			if (++i == pushIterations) {
				break;
			}

			tempInsertKey = evictedKey;
			tempInsertValue = evictedValue;
		} while (true);

		putStash(evictedKey, evictedValue);
	}

	private void putStash(int key, long value) {
		if (stashSize == stashCapacity) {
			// Too many pushes occurred and the stash is full, increase the table size.
			resize(capacity << 1);
			put(key, value);
			return;
		}
		// Store key in the stash.
		int index = capacity + stashSize;
		keyTable[index] = key;
		valueTable[index] = value;
		stashSize++;
		size++;
	}

	/**
	 * @param defaultValue
	 *            Returned if the key was not associated with a value.
	 */
	public long get(int key, long defaultValue) {
		if (key == 0) {
			if (!hasZeroValue) {
				return defaultValue;
			}
			return zeroValue;
		}
		int index = key & mask;
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) {
					return getStash(key, defaultValue);
				}
			}
		}
		return valueTable[index];
	}

	private long getStash(int key, long defaultValue) {
		int[] keyTable = this.keyTable;
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				return valueTable[i];
			}
		}
		return defaultValue;
	}

	/**
	 * Returns the key's current value and increments the stored value. If the key is not in the map, defaultValue +
	 * increment is put into the map.
	 */
	public long getAndIncrement(int key, long defaultValue, long increment) {
		if (key == 0) {
			if (hasZeroValue) {
				long value = zeroValue;
				zeroValue += increment;
				return value;
			} else {
				hasZeroValue = true;
				zeroValue = defaultValue + increment;
				++size;
				return defaultValue;
			}
		}
		int index = key & mask;
		if (key != keyTable[index]) {
			index = hash2(key);
			if (key != keyTable[index]) {
				index = hash3(key);
				if (key != keyTable[index]) {
					return getAndIncrementStash(key, defaultValue, increment);
				}
			}
		}
		long value = valueTable[index];
		valueTable[index] = value + increment;
		return value;
	}

	private long getAndIncrementStash(int key, long defaultValue, long increment) {
		int[] keyTable = this.keyTable;
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key == keyTable[i]) {
				long value = valueTable[i];
				valueTable[i] = value + increment;
				return value;
			}
		put(key, defaultValue + increment);
		return defaultValue;
	}

	public long remove(int key, long defaultValue) {
		if (key == 0) {
			if (!hasZeroValue) {
				return defaultValue;
			}
			hasZeroValue = false;
			size--;
			return zeroValue;
		}

		int index = key & mask;
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			long oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash2(key);
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			long oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash3(key);
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			long oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		return removeStash(key, defaultValue);
	}

	long removeStash(int key, long defaultValue) {
		int[] keyTable = this.keyTable;
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				long oldValue = valueTable[i];
				removeStashIndex(i);
				size--;
				return oldValue;
			}
		}
		return defaultValue;
	}

	void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) {
			keyTable[index] = keyTable[lastIndex];
			valueTable[index] = valueTable[lastIndex];
		}
	}

	/**
	 * Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less,
	 * nothing is done. If the map contains more items than the specified capacity, the next highest power of two
	 * capacity is used instead.
	 */
	public void shrink(int maximumCapacity) {
		int tempMaximumCapacity = maximumCapacity;
		if (tempMaximumCapacity < 0) {
			throw new IllegalArgumentException("maximumCapacity must be >= 0: " + tempMaximumCapacity);
		}
		if (size > tempMaximumCapacity) {
			tempMaximumCapacity = size;
		}
		if (capacity <= tempMaximumCapacity) {
			return;
		}
		tempMaximumCapacity = MathUtils.nextPowerOfTwo(tempMaximumCapacity);
		resize(tempMaximumCapacity);
	}

	/** Clears the map and reduces the size of the backing arrays to be the specified capacity if they are larger. */
	public void clear(int maximumCapacity) {
		if (capacity <= maximumCapacity) {
			clear();
			return;
		}
		hasZeroValue = false;
		size = 0;
		resize(maximumCapacity);
	}

	@Override
	public void clear() {
		if (size == 0) {
			return;
		}
		int[] keyTable = this.keyTable;
		for (int i = capacity + stashSize; i-- > 0;) {
			keyTable[i] = EMPTY;
		}
		size = 0;
		stashSize = 0;
		hasZeroValue = false;
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value,
	 * which may be an expensive operation.
	 */
	public boolean containsValue(int value) {
		if (hasZeroValue && zeroValue == value) {
			return true;
		}
		long[] valueTable = this.valueTable;
		for (int i = capacity + stashSize; i-- > 0;) {
			if (valueTable[i] == value) {
				return true;
			}
		}
		return false;
	}

	public boolean containsKey(int key) {
		if (key == 0) {
			return hasZeroValue;
		}
		int index = key & mask;
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) {
					return containsKeyStash(key);
				}
			}
		}
		return true;
	}

	private boolean containsKeyStash(int key) {
		int[] keyTable = this.keyTable;
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and
	 * compares every value, which may be an expensive operation.
	 */
	public int findKey(int value, int notFound) {
		if (hasZeroValue && zeroValue == value) {
			return 0;
		}
		long[] valueTable = this.valueTable;
		for (int i = capacity + stashSize; i-- > 0;) {
			if (valueTable[i] == value) {
				return keyTable[i];
			}
		}
		return notFound;
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before
	 * adding many items to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= threshold) {
			resize(MathUtils.nextPowerOfTwo((int) (sizeNeeded / loadFactor)));
		}
	}

	private void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		int[] oldKeyTable = keyTable;
		long[] oldValueTable = valueTable;

		keyTable = new int[newSize + stashCapacity];
		valueTable = new long[newSize + stashCapacity];

		int oldSize = size;
		size = hasZeroValue ? 1 : 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				int key = oldKeyTable[i];
				if (key != EMPTY) {
					putResize(key, oldValueTable[i]);
				}
			}
		}
	}

	private int hash2(int h) {
		int temp = h * PRIME1;
		return (temp ^ temp >>> hashShift) & mask;
	}

	private int hash3(int h) {
		int temp = h * PRIME2;
		return (temp ^ temp >>> hashShift) & mask;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "{}";
		}
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		int[] keyTable = this.keyTable;
		long[] valueTable = this.valueTable;
		int i = keyTable.length;
		if (hasZeroValue) {
			buffer.append("0=");
			buffer.append(zeroValue);
		} else {
			while (i-- > 0) {
				int key = keyTable[i];
				if (key == EMPTY) {
					continue;
				}
				buffer.append(key);
				buffer.append('=');
				buffer.append(valueTable[i]);
				break;
			}
		}
		while (i-- > 0) {
			int key = keyTable[i];
			if (key == EMPTY) {
				continue;
			}
			buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public Iterator<Entry> iterator() {
		return entries();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is
	 * returned each time this method is called. Use the {@link Entries} constructor for nested or multithreaded
	 * iteration.
	 */
	public Entries entries() {
		if (entries1 == null) {
			entries1 = new Entries(this);
			entries2 = new Entries(this);
		}
		if (!entries1.valid) {
			entries1.reset();
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.reset();
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	/**
	 * Returns an iterator for the values in the map. Remove is supported. Note that the same iterator instance is
	 * returned each time this method is called. Use the {@link Entries} constructor for nested or multithreaded
	 * iteration.
	 */
	public Values values() {
		if (values1 == null) {
			values1 = new Values(this);
			values2 = new Values(this);
		}
		if (!values1.valid) {
			values1.reset();
			values1.valid = true;
			values2.valid = false;
			return values1;
		}
		values2.reset();
		values2.valid = true;
		values1.valid = false;
		return values2;
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is
	 * returned each time this method is called. Use the {@link Entries} constructor for nested or multithreaded
	 * iteration.
	 */
	public Keys keys() {
		if (keys1 == null) {
			keys1 = new Keys(this);
			keys2 = new Keys(this);
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	static public class Entry {
		public int key;
		public long value;

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}

	static private class MapIterator {
		static final int INDEX_ILLEGAL = -2;
		static final int INDEX_ZERO = -1;

		public boolean hasNext;

		final IntLongMap map;
		int nextIndex, currentIndex;
		boolean valid = true;

		public MapIterator(IntLongMap map) {
			this.map = map;
			reset();
		}

		public void reset() {
			currentIndex = INDEX_ILLEGAL;
			nextIndex = INDEX_ZERO;
			if (map.hasZeroValue) {
				hasNext = true;
			} else {
				findNextIndex();
			}
		}

		void findNextIndex() {
			hasNext = false;
			int[] keyTable = map.keyTable;
			for (int n = map.capacity + map.stashSize; ++nextIndex < n;) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if (currentIndex == INDEX_ZERO && map.hasZeroValue) {
				map.hasZeroValue = false;
			} else if (currentIndex < 0) {
				throw new IllegalStateException("next must be called before remove.");
			} else if (currentIndex >= map.capacity) {
				map.removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				map.keyTable[currentIndex] = EMPTY;
			}
			currentIndex = INDEX_ILLEGAL;
			map.size--;
		}
	}

	static public class Entries extends MapIterator implements Iterable<Entry>, Iterator<Entry> {
		private Entry entry = new Entry();

		public Entries(IntLongMap map) {
			super(map);
		}

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public Entry next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			int[] keyTable = map.keyTable;
			if (nextIndex == INDEX_ZERO) {
				entry.key = 0;
				entry.value = map.zeroValue;
			} else {
				entry.key = keyTable[nextIndex];
				entry.value = map.valueTable[nextIndex];
			}
			currentIndex = nextIndex;
			findNextIndex();
			return entry;
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			return hasNext;
		}

		@Override
		public Iterator<Entry> iterator() {
			return this;
		}

		@Override
		public void remove() {
			super.remove();
		}
	}

	static public class Values extends MapIterator {
		public Values(IntLongMap map) {
			super(map);
		}

		public boolean hasNext() {
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			return hasNext;
		}

		public long next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			long value;
			if (nextIndex == INDEX_ZERO) {
				value = map.zeroValue;
			} else {
				value = map.valueTable[nextIndex];
			}
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		/** Returns a new array containing the remaining values. */
		public LongArray toArray() {
			LongArray array = new LongArray(true, map.size);
			while (hasNext) {
				array.add(next());
			}
			return array;
		}
	}

	static public class Keys extends MapIterator {
		public Keys(IntLongMap map) {
			super(map);
		}

		public boolean hasNext() {
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			return hasNext;
		}

		public int next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}
			int key = nextIndex == INDEX_ZERO ? 0 : map.keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		/** Returns a new array containing the remaining keys. */
		public LongArray toArray() {
			LongArray array = new LongArray(true, map.size);
			while (hasNext) {
				array.add(next());
			}
			return array;
		}
	}
}
