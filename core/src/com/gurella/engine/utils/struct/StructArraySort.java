package com.gurella.engine.utils.struct;

import java.util.Comparator;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Sort;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.struct.StructProperty.GridPoint3StructProperty;

/**
 * A stable, adaptive, iterative mergesort that requires far fewer than n lg(n) comparisons when running on partially
 * sorted arrays, while offering performance comparable to a traditional mergesort when run on random arrays. Like all
 * proper mergesorts, this sort is stable and runs O(n log n) time (worst case). In the worst case, this sort requires
 * temporary storage space for n/2 object references; in the best case, it requires only a small constant amount of
 * space.
 * 
 * This implementation was adapted from Tim Peters's list sort for Python, which is described in detail here:
 * 
 * http://svn.python.org/projects/python/trunk/Objects/listsort.txt
 * 
 * Tim's C code may be found here:
 * 
 * http://svn.python.org/projects/python/trunk/Objects/listobject.c
 * 
 * The underlying techniques are described in this paper (and may have even earlier origins):
 * 
 * "Optimistic Sorting and Information Theoretic Complexity" Peter McIlroy SODA (Fourth Annual ACM-SIAM Symposium on
 * Discrete Algorithms), pp 467-474, Austin, Texas, 25-27 January 1993.
 * 
 * While the API to this class consists solely of static methods, it is (privately) instantiable; a TimSort instance
 * holds the state of an ongoing sort, assuming the input array is large enough to warrant the full-blown TimSort. Small
 * arrays are sorted in place, using a binary insertion sort.
 */
public class StructArraySort<T extends Struct> {
	private static final int MIN_MERGE = 32;
	private static final int MIN_GALLOP = 7;
	private static final int INITIAL_TMP_STORAGE_LENGTH = 256;

	private StructArray<T> a;
	private Comparator<T> c;
	private int minGallop = MIN_GALLOP;

	private StructArray<T> tmpa;
	private T tmp;
	private T tmp1;
	private int tmpCount;
	private int stackSize = 0;

	private final int[] runBase;
	private final int[] runLen;

	public StructArraySort(Class<T> type) {
		this(StructType.get(type));
	}

	public StructArraySort(StructType<T> structType) {
		tmpa = new StructArray<T>(structType, INITIAL_TMP_STORAGE_LENGTH);
		tmp = structType.newInstance(new FloatArrayBuffer(structType.size), 0);
		tmp1 = structType.newInstance(new FloatArrayBuffer(structType.size), 0);
		runBase = new int[40];
		runLen = new int[40];
	}

	public void sort(StructArray<T> a, Comparator<T> c) {
		sort(a, c, 0, a.length());
	}

	public void sort(StructArray<T> a, Comparator<T> c, int lo, int hi) {
		int l = lo;
		stackSize = 0;
		rangeCheck(a.length(), l, hi);

		int nRemaining = hi - l;
		if (nRemaining < 2) {
			return;
		}

		tmpa.clear();
		this.a = a;
		this.c = c;
		tmpCount = 0;

		if (nRemaining < MIN_MERGE) {
			int initRunLen = countRunAndMakeAscending(l, hi);
			binarySort(l, hi, l + initRunLen);
			return;
		}

		int minRun = minRunLength(nRemaining);
		do {
			int runLen = countRunAndMakeAscending(l, hi);

			if (runLen < minRun) {
				int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(l, l + force, l + runLen);
				runLen = force;
			}

			pushRun(l, runLen);
			mergeCollapse();

			l += runLen;
			nRemaining -= runLen;
		} while (nRemaining != 0);

		mergeForceCollapse();

		this.a = null;
		this.c = null;
	}

	@SuppressWarnings("fallthrough")
	private void binarySort(int lo, int hi, int s) {
		int start = s;
		if (start == lo) {
			start++;
		}

		for (; start < hi; start++) {
			int pivot = start;
			tmpa.set(0, a.get(pivot, tmp1));

			int left = lo;
			int right = start;

			while (left < right) {
				int mid = (left + right) >>> 1;
				if (c.compare(a.get(pivot, tmp), a.get(mid, tmp1)) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}

			int n = start - left; // The number of elements to move
			switch (n) {
			case 2:
				a.set(left + 1, left + 2);
			case 1:
				a.set(left, left + 1);
				break;
			default:
				a.set(a, left, left + 1, n);
			}

			a.set(left, tmpa.get(0, tmp1));
		}
	}

	private int countRunAndMakeAscending(int lo, int hi) {
		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		if (c.compare(a.get(runHi++, tmp), a.get(lo, tmp1)) < 0) { // Descending
			while (runHi < hi && c.compare(a.get(runHi, tmp), a.get(runHi - 1, tmp1)) < 0) {
				runHi++;
			}
			reverseRange(lo, runHi);
		} else { // Ascending
			while (runHi < hi && c.compare(a.get(runHi, tmp), a.get(runHi - 1, tmp1)) >= 0) {
				runHi++;
			}
		}

		return runHi - lo;
	}

	private void reverseRange(int lo, int hi) {
		int l = lo;
		int h = hi;

		h--;
		while (l < h) {
			a.swap(h--, l++);
		}
	}

	private static int minRunLength(int n) {
		int temp = n;
		int r = 0;
		while (temp >= MIN_MERGE) {
			r |= (temp & 1);
			temp >>= 1;
		}
		return temp + r;
	}

	private void pushRun(int runBase, int runLen) {
		this.runBase[stackSize] = runBase;
		this.runLen[stackSize] = runLen;
		stackSize++;
	}

	private void mergeCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if ((n >= 1 && runLen[n - 1] <= runLen[n] + runLen[n + 1])
					|| (n >= 2 && runLen[n - 2] <= runLen[n] + runLen[n - 1])) {
				if (runLen[n - 1] < runLen[n + 1]) {
					n--;
				}
			} else if (runLen[n] > runLen[n + 1]) {
				break;
			}
			mergeAt(n);
		}
	}

	private void mergeForceCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLen[n - 1] < runLen[n + 1]) {
				n--;
			}
			mergeAt(n);
		}
	}

	private void mergeAt(int i) {
		int base1 = runBase[i];
		int len1 = runLen[i];
		int base2 = runBase[i + 1];
		int len2 = runLen[i + 1];

		runLen[i] = len1 + len2;
		if (i == stackSize - 3) {
			runBase[i + 1] = runBase[i + 2];
			runLen[i + 1] = runLen[i + 2];
		}
		stackSize--;

		int k = gallopRight(a, base2, a, base1, len1, 0);
		base1 += k;
		len1 -= k;
		if (len1 == 0) {
			return;
		}

		len2 = gallopLeft(a, base1 + len1 - 1, a, base2, len2, len2 - 1);
		if (len2 == 0) {
			return;
		}

		if (len1 <= len2) {
			mergeLo(base1, len1, base2, len2);
		} else {
			mergeHi(base1, len1, base2, len2);
		}
	}

	private int gallopLeft(StructArray<T> k, int key, StructArray<T> a, int base, int len, int hint) {
		int lastOfs = 0;
		int ofs = 1;
		if (c.compare(k.get(key, tmp), a.get(base + hint, tmp1)) > 0) {
			int maxOfs = len - hint;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint + ofs, tmp1)) > 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {
					ofs = maxOfs;
				}
			}

			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			lastOfs += hint;
			ofs += hint;
		} else {
			final int maxOfs = hint + 1;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint - ofs, tmp1)) <= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {
					ofs = maxOfs;
				}
			}

			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		}

		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(k.get(key, tmp), a.get(base + m, tmp1)) > 0) {
				lastOfs = m + 1; // a[base + m] < key
			} else {
				ofs = m; // key <= a[base + m]
			}
		}

		return ofs;
	}

	private int gallopRight(StructArray<T> k, int key, StructArray<T> a, int base, int len, int hint) {
		int ofs = 1;
		int lastOfs = 0;
		if (c.compare(k.get(key, tmp), a.get(base + hint, tmp1)) < 0) {
			int maxOfs = hint + 1;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint - ofs, tmp1)) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {
					// int overflow
					ofs = maxOfs;
				}
			}

			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		} else {
			int maxOfs = len - hint;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint + ofs, tmp1)) >= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {
					ofs = maxOfs;
				}
			}

			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			lastOfs += hint;
			ofs += hint;
		}

		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(k.get(key, tmp), a.get(base + m, tmp1)) < 0) {
				ofs = m; // key < a[b + m]
			} else {
				lastOfs = m + 1; // a[b + m] <= key
			}
		}

		return ofs;
	}

	private void mergeLo(int base1, int len1, int base2, int len2) {
		int tempLen1 = len1;
		int tempLen2 = len2;

		ensureCapacity(tempLen1);
		tmpa.set(a, base1, 0, tempLen1);

		int cursor1 = 0; // Indexes into tmp array
		int cursor2 = base2; // Indexes int a
		int dest = base1; // Indexes int a

		a.set(cursor2++, dest++);
		if (--tempLen2 == 0) {
			a.set(tmpa, cursor1, dest, tempLen1);
			return;
		}

		if (tempLen1 == 1) {
			a.set(a, cursor2, dest, tempLen2);
			a.set(dest + tempLen2, tmpa.get(cursor1, tmp1));
			return;
		}

		int minGallop = this.minGallop;
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			do {
				if (c.compare(a.get(cursor2, tmp), tmpa.get(cursor1, tmp1)) < 0) {
					a.set(dest++, a.get(cursor2++, tmp1));
					count2++;
					count1 = 0;
					if (--tempLen2 == 0) {
						break outer;
					}
				} else {
					a.set(dest++, tmpa.get(cursor1++, tmp1));
					count1++;
					count2 = 0;
					if (--tempLen1 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			do {
				count1 = gallopRight(a, cursor2, tmpa, cursor1, tempLen1, 0);
				if (count1 != 0) {
					a.set(tmpa, cursor1, dest, count1);
					dest += count1;
					cursor1 += count1;
					tempLen1 -= count1;
					if (tempLen1 <= 1) // len1 == 1 || len1 == 0
						break outer;
				}

				a.set(cursor2++, dest++);
				if (--tempLen2 == 0) {
					break outer;
				}

				count2 = gallopLeft(tmpa, cursor1, a, cursor2, tempLen2, 0);
				if (count2 != 0) {
					a.set(a, cursor2, dest, count2);
					dest += count2;
					cursor2 += count2;
					tempLen2 -= count2;
					if (tempLen2 == 0) {
						break outer;
					}
				}
				a.set(dest++, tmpa.get(cursor1++, tmp1));
				if (--tempLen1 == 1) {
					break outer;
				}
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0)
				minGallop = 0;
			minGallop += 2; // Penalize for leaving gallop mode
		} // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // Write back to field

		if (tempLen1 == 1) {
			a.set(a, cursor2, dest, tempLen2);
			a.set(dest + tempLen2, tmpa.get(cursor1, tmp1));
		} else if (tempLen1 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.set(tmpa, cursor1, dest, tempLen1);
		}
	}

	private void mergeHi(int base1, int len1, int base2, int len2) {
		int tempLen1 = len1;
		int tempLen2 = len2;

		ensureCapacity(tempLen2);
		tmpa.set(a, base2, 0, tempLen2);

		int cursor1 = base1 + tempLen1 - 1; // Indexes into a
		int cursor2 = tempLen2 - 1; // Indexes into tmp array
		int dest = base2 + tempLen2 - 1; // Indexes into a

		a.set(cursor1--, dest--);
		if (--tempLen1 == 0) {
			a.set(tmpa, 0, dest - (tempLen2 - 1), tempLen2);
			return;
		}

		if (tempLen2 == 1) {
			dest -= tempLen1;
			cursor1 -= tempLen1;
			a.set(a, cursor1 + 1, dest + 1, tempLen1);
			a.set(dest, tmpa.get(cursor2, tmp1));
			return;
		}

		int minGallop = this.minGallop;
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run appears to win consistently.
			 */
			do {
				if (c.compare(tmpa.get(cursor2, tmp), a.get(cursor1, tmp1)) < 0) {
					a.set(dest--, a.get(cursor1--, tmp1));
					count1++;
					count2 = 0;
					if (--tempLen1 == 0) {
						break outer;
					}
				} else {
					a.set(dest--, tmpa.get(cursor2--, tmp1));
					count2++;
					count1 = 0;
					if (--tempLen2 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			do {
				count1 = tempLen1 - gallopRight(tmpa, cursor2, a, base1, tempLen1, tempLen1 - 1);
				if (count1 != 0) {
					dest -= count1;
					cursor1 -= count1;
					tempLen1 -= count1;
					a.set(a, cursor1 + 1, dest + 1, count1);
					if (tempLen1 == 0) {
						break outer;
					}
				}

				a.set(dest--, tmpa.get(cursor2--, tmp1));
				if (--tempLen2 == 1) {
					break outer;
				}

				count2 = tempLen2 - gallopLeft(a, cursor1, tmpa, 0, tempLen2, tempLen2 - 1);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					tempLen2 -= count2;
					a.set(tmpa, cursor2 + 1, dest + 1, count2);
					if (tempLen2 <= 1) {
						break outer;
					}
				}

				a.set(cursor1--, dest--);
				if (--tempLen1 == 0) {
					break outer;
				}

				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);

			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // Penalize for leaving gallop mode
		} // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // Write back to field

		if (tempLen2 == 1) {
			dest -= tempLen1;
			cursor1 -= tempLen1;
			a.set(a, cursor1 + 1, dest + 1, tempLen1);
			a.set(dest, tmpa.get(cursor2, tmp1));
		} else if (tempLen2 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.set(tmpa, 0, dest - (tempLen2 - 1), tempLen2);
		}
	}

	private void ensureCapacity(int minCapacity) {
		tmpCount = Math.max(tmpCount, minCapacity);
		if (tmpa.getCapacity() < minCapacity) {
			// Compute smallest power of 2 > minCapacity
			int newSize = minCapacity;
			newSize |= newSize >> 1;
			newSize |= newSize >> 2;
			newSize |= newSize >> 4;
			newSize |= newSize >> 8;
			newSize |= newSize >> 16;
			newSize++;

			if (newSize < 0) {
				newSize = minCapacity;
			} else {
				newSize = Math.min(newSize, a.length() >>> 1);
			}

			tmpa.ensureCapacity(newSize);
		}
	}

	private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		if (toIndex > arrayLen) {
			throw new ArrayIndexOutOfBoundsException(toIndex);
		}
	}

	public static class TestClass {
		GridPoint3 point = new GridPoint3(randomInt(), randomInt(), randomInt());

		private static int randomInt() {
			return Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue();
		}
	}

	public static class TestStruct extends Struct {
		public static final GridPoint3StructProperty point = new GridPoint3StructProperty();

		public GridPoint3 getPoint() {
			return point.get(this);
		}

		public void setPoint(GridPoint3 value) {
			point.set(this, value);
		}
	}

	public static void main(String[] args) {
		int testSize = 100;
		TestClass[] tc = new TestClass[testSize];
		StructArray<TestStruct> sa = new StructArray<TestStruct>(TestStruct.class, testSize);
		for (int i = 0; i < testSize; i++) {
			TestClass testClass = new TestClass();
			tc[i] = testClass;
			TestStruct testStruct = sa.add();
			testStruct.setPoint(testClass.point);
		}

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				throw new IllegalStateException("Diff 1");
			}
		}

		Sort sort = new Sort();
		sort.sort(tc, new TestClassComparator());

		StructArray<TestStruct> sa2 = new StructArray<TestStruct>(TestStruct.class, testSize);
		sa2.addAll(sa);

		StructArraySort<TestStruct> structSort = new StructArraySort<TestStruct>(TestStruct.class);
		structSort.sort(sa, new TestStructComparator());

		for (int i = 0; i < 10; i++) {
			System.out.println(sa.get(i).getPoint());
			System.out.println(sa2.get(i).getPoint());
		}

		for (int i = 0; i < testSize; i++) {
			System.out.println(tc[i].point);
			System.out.println(sa.get(i).getPoint());
			System.out.println();

			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				System.out.println("i = " + i);
				throw new IllegalStateException("Diff 2");
			}
		}

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				throw new IllegalStateException("Diff 2");
			}
		}
	}

	public static class TestClassComparator implements Comparator<TestClass> {
		@Override
		public int compare(TestClass o1, TestClass o2) {
			return Values.compare(o1.point.x, o2.point.x);
		}
	}

	public static class TestStructComparator implements Comparator<TestStruct> {
		@Override
		public int compare(TestStruct o1, TestStruct o2) {
			return Values.compare(o1.getPoint().x, o2.getPoint().x);
		}
	}
}
