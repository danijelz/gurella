package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.struct.ArrayOfStructs.StructComparator;

/** A stable, adaptive, iterative mergesort that requires far fewer than n lg(n) comparisons when running on partially sorted
 * arrays, while offering performance comparable to a traditional mergesort when run on random arrays. Like all proper mergesorts,
 * this sort is stable and runs O(n log n) time (worst case). In the worst case, this sort requires temporary storage space for
 * n/2 object references; in the best case, it requires only a small constant amount of space.
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
 * "Optimistic Sorting and Information Theoretic Complexity" Peter McIlroy SODA (Fourth Annual ACM-SIAM Symposium on Discrete
 * Algorithms), pp 467-474, Austin, Texas, 25-27 January 1993.
 * 
 * While the API to this class consists solely of static methods, it is (privately) instantiable; a TimSort instance holds the
 * state of an ongoing sort, assuming the input array is large enough to warrant the full-blown TimSort. Small arrays are sorted
 * in place, using a binary insertion sort. */
public class StructTimSort {
	private static final int MIN_MERGE = 32;
	private static final int MIN_GALLOP = 7;
	private static final int INITIAL_TMP_STORAGE_LENGTH = 256;

	private ArrayOfStructs a;
	private StructComparator c;
	private int minGallop = MIN_GALLOP;

	private ArrayOfStructs tmpa;
	private int tmpCount;
	private int stackSize = 0;

	private final int[] runBase;
	private final int[] runLen;

	public StructTimSort() {
		tmpa = new ArrayOfStructs(1, INITIAL_TMP_STORAGE_LENGTH);
		runBase = new int[40];
		runLen = new int[40];
	}

	public void sort(ArrayOfStructs a, StructComparator c, int lo, int hi) {
		int l = lo;
		stackSize = 0;
		rangeCheck(a.size, l, hi);

		int nRemaining = hi - l;
		if (nRemaining < 2)
			return;

		tmpa.size = 0;
		tmpa.structSize = a.structSize;
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
			tmpa.setItem(a, pivot, 0);

			int left = lo;
			int right = start;

			while (left < right) {
				int mid = (left + right) >>> 1;
				if (c.compare(a, pivot, a, mid) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}

			int n = start - left; // The number of elements to move
			switch (n) {
			case 2:
				a.setItem(left + 1, left + 2);
			case 1:
				a.setItem(left, left + 1);
				break;
			default:
				a.setItems(a, left, left + 1, n);
			}
			a.setItem(tmpa, 0, left);
		}
	}

	private int countRunAndMakeAscending(int lo, int hi) {
		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		if (c.compare(a, runHi++, a, lo) < 0) { // Descending
			while (runHi < hi && c.compare(a, runHi, a, runHi - 1) < 0) {
				runHi++;
			}
			reverseRange(lo, runHi);
		} else { // Ascending
			while (runHi < hi && c.compare(a, runHi, a, runHi - 1) >= 0) {
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
			a.swap(h--, l++, tmpa.buffer);
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

	private int gallopLeft(ArrayOfStructs k, int key, ArrayOfStructs a, int base, int len, int hint) {
		int lastOfs = 0;
		int ofs = 1;
		if (c.compare(k, key, a, base + hint) > 0) {
			int maxOfs = len - hint;

			while (ofs < maxOfs && c.compare(k, key, a, base + hint + ofs) > 0) {
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

			while (ofs < maxOfs && c.compare(k, key, a, base + hint - ofs) <= 0) {
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

			if (c.compare(k, key, a, base + m) > 0) {
				lastOfs = m + 1; // a[base + m] < key
			} else {
				ofs = m; // key <= a[base + m]
			}
		}

		return ofs;
	}

	private int gallopRight(ArrayOfStructs k, int key, ArrayOfStructs a, int base, int len, int hint) {
		int ofs = 1;
		int lastOfs = 0;
		if (c.compare(k, key, a, base + hint) < 0) {
			int maxOfs = hint + 1;

			while (ofs < maxOfs && c.compare(k, key, a, base + hint - ofs) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
					ofs = maxOfs;
			}

			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		} else {
			int maxOfs = len - hint;

			while (ofs < maxOfs && c.compare(k, key, a, base + hint + ofs) >= 0) {
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

			if (c.compare(k, key, a, base + m) < 0) {
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
		tmpa.setItems(a, base1, 0, tempLen1);

		int cursor1 = 0; // Indexes into tmp array
		int cursor2 = base2; // Indexes int a
		int dest = base1; // Indexes int a

		a.setItem(cursor2++, dest++);
		if (--tempLen2 == 0) {
			a.setItems(tmpa, cursor1, dest, tempLen1);
			return;
		}

		if (tempLen1 == 1) {
			a.setItems(a, cursor2, dest, tempLen2);
			a.setItem(tmpa, cursor1, dest + tempLen2);
			return;
		}

		int minGallop = this.minGallop;
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			do {
				if (c.compare(a, cursor2, tmpa, cursor1) < 0) {
					a.setItem(a, cursor2++, dest++);
					count2++;
					count1 = 0;
					if (--tempLen2 == 0) {
						break outer;
					}
				} else {
					a.setItem(tmpa, cursor1++, dest++);
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
					a.setItems(tmpa, cursor1, dest, count1);
					dest += count1;
					cursor1 += count1;
					tempLen1 -= count1;
					if (tempLen1 <= 1) // len1 == 1 || len1 == 0
						break outer;
				}

				a.setItem(cursor2++, dest++);
				if (--tempLen2 == 0) {
					break outer;
				}

				count2 = gallopLeft(tmpa, cursor1, a, cursor2, tempLen2, 0);
				if (count2 != 0) {
					a.setItems(a, cursor2, dest, count2);
					dest += count2;
					cursor2 += count2;
					tempLen2 -= count2;
					if (tempLen2 == 0) {
						break outer;
					}
				}
				a.setItem(tmpa, cursor1++, dest++);
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
			a.setItems(a, cursor2, dest, tempLen2);
			a.setItem(tmpa, cursor1, dest + tempLen2);
		} else if (tempLen1 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.setItems(tmpa, cursor1, dest, tempLen1);
		}
	}

	private void mergeHi(int base1, int len1, int base2, int len2) {
		int tempLen1 = len1;
		int tempLen2 = len2;

		ensureCapacity(tempLen2);
		tmpa.setItems(a, base2, 0, tempLen2);

		int cursor1 = base1 + tempLen1 - 1; // Indexes into a
		int cursor2 = tempLen2 - 1; // Indexes into tmp array
		int dest = base2 + tempLen2 - 1; // Indexes into a

		a.setItem(cursor1--, dest--);
		if (--tempLen1 == 0) {
			a.setItems(tmpa, 0, dest - (tempLen2 - 1), tempLen2);
			return;
		}

		if (tempLen2 == 1) {
			dest -= tempLen1;
			cursor1 -= tempLen1;
			a.setItems(a, cursor1 + 1, dest + 1, tempLen1);
			a.setItem(tmpa, cursor2, dest);
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
				if (c.compare(tmpa, cursor2, a, cursor1) < 0) {
					a.setItem(a, cursor1--, dest--);
					count1++;
					count2 = 0;
					if (--tempLen1 == 0) {
						break outer;
					}
				} else {
					a.setItem(tmpa, cursor2--, dest--);
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
					a.setItems(a, cursor1 + 1, dest + 1, count1);
					if (tempLen1 == 0) {
						break outer;
					}
				}

				a.setItem(tmpa, cursor2--, dest--);
				if (--tempLen2 == 1) {
					break outer;
				}

				count2 = tempLen2 - gallopLeft(a, cursor1, tmpa, 0, tempLen2, tempLen2 - 1);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					tempLen2 -= count2;
					a.setItems(tmpa, cursor2 + 1, dest + 1, count2);
					if (tempLen2 <= 1) {
						break outer;
					}
				}

				a.setItem(cursor1--, dest--);
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
			a.setItems(a, cursor1 + 1, dest + 1, tempLen1);
			a.setItem(tmpa, cursor2, dest);
		} else if (tempLen2 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			a.setItems(tmpa, 0, dest - (tempLen2 - 1), tempLen2);
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
				newSize = Math.min(newSize, a.size >>> 1);
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
