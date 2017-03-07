package com.gurella.engine.utils.struct;

import java.util.Comparator;

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
	private static final int minMerge = 32;
	private static final int initialMinGallop = 7;
	private static final int defaultInitialTmpStorageLength = 256;

	private StructArray<T> a;
	private Comparator<T> c;
	private int minGallop = initialMinGallop;

	private StructArray<T> tmpa;
	private T tmp;
	private int stackSize = 0;

	private final int[] runBase;
	private final int[] runLen;

	public StructArraySort(Class<T> type) {
		this(StructType.get(type), defaultInitialTmpStorageLength);
	}

	public StructArraySort(StructType<T> structType) {
		this(structType, defaultInitialTmpStorageLength);
	}

	public StructArraySort(Class<T> type, int initialTmpStorageLength) {
		this(StructType.get(type), initialTmpStorageLength);
	}

	public StructArraySort(StructType<T> structType, int initialTmpStorageLength) {
		tmpa = new StructArray<T>(structType, initialTmpStorageLength);
		tmp = structType.newInstance(null, 0);
		runBase = new int[40];
		runLen = new int[40];
	}

	public void sort(StructArray<T> a, Comparator<T> c) {
		sort(a, c, 0, a.length());
	}

	public void sort(StructArrayView<T> v, Comparator<T> c) {
		sort(a, c, 0 + v.offsetIndex, v.length());
	}

	public void sort(StructArrayView<T> v, Comparator<T> c, int lo, int hi) {
		sort(a, c, lo + v.offsetIndex, hi + v.offsetIndex);
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

		if (nRemaining < minMerge) {
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
			tmpa.clear();
			tmpa.addAll(a, pivot, 1);

			int left = lo;
			int right = start;

			while (left < right) {
				int mid = (left + right) >>> 1;
				if (c.compare(a.get(pivot, tmp), a.get(mid)) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}

			int n = start - left; // The number of elements to move
			switch (n) {
			case 2:
				a.move(left + 1, left + 2);
			case 1:
				a.move(left, left + 1);
				break;
			default:
				a.setAll(left + 1, a, left, n);
			}

			a.setAll(left, tmpa, 0, 1);
		}
	}

	private int countRunAndMakeAscending(int lo, int hi) {
		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		if (c.compare(a.get(runHi++, tmp), a.get(lo)) < 0) { // Descending
			while (runHi < hi && c.compare(a.get(runHi, tmp), a.get(runHi - 1)) < 0) {
				runHi++;
			}
			reverseRange(lo, runHi);
		} else { // Ascending
			while (runHi < hi && c.compare(a.get(runHi, tmp), a.get(runHi - 1)) >= 0) {
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
		while (temp >= minMerge) {
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
		if (c.compare(k.get(key, tmp), a.get(base + hint)) > 0) {
			int maxOfs = len - hint;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint + ofs)) > 0) {
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

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint - ofs)) <= 0) {
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

			if (c.compare(k.get(key, tmp), a.get(base + m)) > 0) {
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
		if (c.compare(k.get(key, tmp), a.get(base + hint)) < 0) {
			int maxOfs = hint + 1;

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint - ofs)) < 0) {
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

			while (ofs < maxOfs && c.compare(k.get(key, tmp), a.get(base + hint + ofs)) >= 0) {
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

			if (c.compare(k.get(key, tmp), a.get(base + m)) < 0) {
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
		tmpa.clear();
		tmpa.addAll(a, base1, tempLen1);

		int cursor1 = 0; // Indexes into tmp array
		int cursor2 = base2; // Indexes int a
		int dest = base1; // Indexes int a

		a.move(cursor2++, dest++);
		if (--tempLen2 == 0) {
			a.setAll(dest, tmpa, cursor1, tempLen1);
			return;
		}

		if (tempLen1 == 1) {
			a.setAll(dest, a, cursor2, tempLen2);
			a.setAll(dest + tempLen2, tmpa, cursor1, 1);
			return;
		}

		int minGallop = this.minGallop;
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			do {
				if (c.compare(a.get(cursor2), tmpa.get(cursor1)) < 0) {
					a.setAll(dest++, a, cursor2++, 1);
					count2++;
					count1 = 0;
					if (--tempLen2 == 0) {
						break outer;
					}
				} else {
					a.setAll(dest++, tmpa, cursor1++, 1);
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
					a.setAll(dest, tmpa, cursor1, count1);
					dest += count1;
					cursor1 += count1;
					tempLen1 -= count1;
					if (tempLen1 <= 1) // len1 == 1 || len1 == 0
						break outer;
				}

				a.move(cursor2++, dest++);
				if (--tempLen2 == 0) {
					break outer;
				}

				count2 = gallopLeft(tmpa, cursor1, a, cursor2, tempLen2, 0);
				if (count2 != 0) {
					a.setAll(dest, a, cursor2, count2);
					dest += count2;
					cursor2 += count2;
					tempLen2 -= count2;
					if (tempLen2 == 0) {
						break outer;
					}
				}
				a.setAll(dest++, tmpa, cursor1++, 1);
				if (--tempLen1 == 1) {
					break outer;
				}
				minGallop--;
			} while (count1 >= initialMinGallop | count2 >= initialMinGallop);
			if (minGallop < 0)
				minGallop = 0;
			minGallop += 2; // Penalize for leaving gallop mode
		} // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // Write back to field

		if (tempLen1 == 1) {
			a.setAll(dest, a, cursor2, tempLen2);
			a.setAll(dest + tempLen2, tmpa, cursor1, 1);
		} else if (tempLen1 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.setAll(dest, tmpa, cursor1, tempLen1);
		}
	}

	private void mergeHi(int base1, int len1, int base2, int len2) {
		int tempLen1 = len1;
		int tempLen2 = len2;

		ensureCapacity(tempLen2);
		tmpa.clear();
		tmpa.addAll(a, base2, tempLen2);

		int cursor1 = base1 + tempLen1 - 1; // Indexes into a
		int cursor2 = tempLen2 - 1; // Indexes into tmp array
		int dest = base2 + tempLen2 - 1; // Indexes into a

		a.move(cursor1--, dest--);
		if (--tempLen1 == 0) {
			a.setAll(dest - (tempLen2 - 1), tmpa, 0, tempLen2);
			return;
		}

		if (tempLen2 == 1) {
			dest -= tempLen1;
			cursor1 -= tempLen1;
			a.setAll(dest + 1, a, cursor1 + 1, tempLen1);
			a.setAll(dest, tmpa, cursor2, 1);
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
				if (c.compare(tmpa.get(cursor2), a.get(cursor1)) < 0) {
					a.setAll(dest--, a, cursor1--, 1);
					count1++;
					count2 = 0;
					if (--tempLen1 == 0) {
						break outer;
					}
				} else {
					a.setAll(dest--, tmpa, cursor2--, 1);
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
					a.setAll(dest + 1, a, cursor1 + 1, count1);
					if (tempLen1 == 0) {
						break outer;
					}
				}

				a.setAll(dest--, tmpa, cursor2--, 1);
				if (--tempLen2 == 1) {
					break outer;
				}

				count2 = tempLen2 - gallopLeft(a, cursor1, tmpa, 0, tempLen2, tempLen2 - 1);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					tempLen2 -= count2;
					a.setAll(dest + 1, tmpa, cursor2 + 1, count2);
					if (tempLen2 <= 1) {
						break outer;
					}
				}

				a.move(cursor1--, dest--);
				if (--tempLen1 == 0) {
					break outer;
				}

				minGallop--;
			} while (count1 >= initialMinGallop | count2 >= initialMinGallop);

			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // Penalize for leaving gallop mode
		} // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // Write back to field

		if (tempLen2 == 1) {
			dest -= tempLen1;
			cursor1 -= tempLen1;
			a.setAll(dest + 1, a, cursor1 + 1, tempLen1);
			a.setAll(dest, tmpa, cursor2, 1);
		} else if (tempLen2 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.setAll(dest - (tempLen2 - 1), tmpa, 0, tempLen2);
		}
	}

	private void ensureCapacity(int minCapacity) {
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
}
