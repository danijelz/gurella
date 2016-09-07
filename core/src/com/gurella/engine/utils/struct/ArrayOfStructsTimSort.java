package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.struct.ArrayOfStructs.StructComparator;

public class ArrayOfStructsTimSort {
	private static final int MIN_MERGE = 32;
	private ArrayOfStructs a;

	private StructComparator c;

	private static final int MIN_GALLOP = 7;

	private int minGallop = MIN_GALLOP;

	private static final int INITIAL_TMP_STORAGE_LENGTH = 1024;

	private ArrayOfStructs tmp;

	private int stackSize = 0; // Number of pending runs on stack
	private final int[] runBase;
	private final int[] runLen;

	private static final boolean DEBUG = false;

	ArrayOfStructsTimSort() {
		tmp = new ArrayOfStructs(1, INITIAL_TMP_STORAGE_LENGTH);
		runBase = new int[40];
		runLen = new int[40];
	}

	public void doSort(ArrayOfStructs a, StructComparator c, int lo, int hi) {
		stackSize = 0;
		rangeCheck(a.size, lo, hi);
		int nRemaining = hi - lo;
		if (nRemaining < 2) {
			return; // Arrays of size 0 and 1 are always sorted
		}

		// If array is small, do a "mini-TimSort" with no merges
		if (nRemaining < MIN_MERGE) {
			int initRunLen = countRunAndMakeAscending(a, lo, hi, c);
			binarySort(a, lo, hi, lo + initRunLen, c);
			return;
		}

		this.a = a;
		this.c = c;

		/**
		 * March over the array once, left to right, finding natural runs, extending short natural runs to minRun
		 * elements, and merging runs to maintain stack invariant.
		 */
		int minRun = minRunLength(nRemaining);
		do {
			// Identify next run
			int runLen = countRunAndMakeAscending(a, lo, hi, c);

			// If run is short, extend to min(minRun, nRemaining)
			if (runLen < minRun) {
				int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(a, lo, lo + force, lo + runLen, c);
				runLen = force;
			}

			// Push run onto pending-run stack, and maybe merge
			pushRun(lo, runLen);
			mergeCollapse();

			// Advance to find next run
			lo += runLen;
			nRemaining -= runLen;
		} while (nRemaining != 0);

		// Merge all remaining runs to complete sort
		if (DEBUG) {
			assert lo == hi;
		}
		mergeForceCollapse();
		if (DEBUG) {
			assert stackSize == 1;
		}

		this.a = null;
		this.c = null;
		float[] tmp = this.tmp.buffer;
		for (int i = 0, n = tmp.length; i < n; i++) {
			tmp[i] = 0;
		}
	}

	private ArrayOfStructsTimSort(ArrayOfStructs a, StructComparator c) {
		this.a = a;
		this.c = c;

		// Allocate temp storage (which may be increased later if necessary)
		int len = a.size;
		tmp = new ArrayOfStructs(len < 2 * INITIAL_TMP_STORAGE_LENGTH ? len >>> 1 : INITIAL_TMP_STORAGE_LENGTH,
				a.structSize);

		/*
		 * Allocate runs-to-be-merged stack (which cannot be expanded). The stack length requirements are described in
		 * listsort.txt. The C version always uses the same stack length (85), but this was measured to be too expensive
		 * when sorting "mid-sized" arrays (e.g., 100 elements) in Java. Therefore, we use smaller (but sufficiently
		 * large) stack lengths for smaller arrays. The "magic numbers" in the computation below must be changed if
		 * MIN_MERGE is decreased. See the MIN_MERGE declaration above for more information.
		 */
		int stackLen = (len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40);
		runBase = new int[stackLen];
		runLen = new int[stackLen];
	}

	static void sort(ArrayOfStructs a, StructComparator c) {
		sort(a, 0, a.size, c);
	}

	static void sort(ArrayOfStructs a, int lo, int hi, StructComparator c) {
		if (c == null) {
			throw new NullPointerException("comparator is null");
		}

		rangeCheck(a.size, lo, hi);
		int nRemaining = hi - lo;
		if (nRemaining < 2) {
			return; // Arrays of size 0 and 1 are always sorted
		}

		// If array is small, do a "mini-TimSort" with no merges
		if (nRemaining < MIN_MERGE) {
			int initRunLen = countRunAndMakeAscending(a, lo, hi, c);
			binarySort(a, lo, hi, lo + initRunLen, c);
			return;
		}

		/**
		 * March over the array once, left to right, finding natural runs, extending short natural runs to minRun
		 * elements, and merging runs to maintain stack invariant.
		 */
		ArrayOfStructsTimSort ts = new ArrayOfStructsTimSort(a, c);
		int minRun = minRunLength(nRemaining);
		do {
			// Identify next run
			int runLen = countRunAndMakeAscending(a, lo, hi, c);

			// If run is short, extend to min(minRun, nRemaining)
			if (runLen < minRun) {
				int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(a, lo, lo + force, lo + runLen, c);
				runLen = force;
			}

			// Push run onto pending-run stack, and maybe merge
			ts.pushRun(lo, runLen);
			ts.mergeCollapse();

			// Advance to find next run
			lo += runLen;
			nRemaining -= runLen;
		} while (nRemaining != 0);

		// Merge all remaining runs to complete sort
		if (DEBUG) {
			assert lo == hi;
		}
		ts.mergeForceCollapse();
		if (DEBUG) {
			assert ts.stackSize == 1;
		}
	}

	@SuppressWarnings("fallthrough")
	static void binarySort(ArrayOfStructs a, int lo, int hi, int start, StructComparator c) {
		if (DEBUG) {
			assert lo <= start && start <= hi;
		}
		if (start == lo) {
			start++;
		}

		float[] pivotStruct = new float[a.structSize];

		for (; start < hi; start++) {
			int pivot = start;
			a.getFloatArrayByIndex(pivotStruct, pivot);

			// Set left (and right) to the index where a[start] (pivot) belongs
			int left = lo;
			int right = start;
			if (DEBUG) {
				assert left <= right;
			}
			/*
			 * Invariants: pivot >= all in [lo, left). pivot < all in [right, start).
			 */
			while (left < right) {
				int mid = (left + right) >>> 1;
				if (c.compare(a, pivot, mid) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}

			if (DEBUG) {
				assert left == right;
			}

			/*
			 * The invariants still hold: pivot >= all in [lo, left) and pivot < all in [left, start), so pivot belongs
			 * at left. Note that if there are elements equal to pivot, left points to the first slot after them --
			 * that's why this sort is stable. Slide elements over to make room for pivot.
			 */
			int n = start - left; // The number of elements to move
			// Switch is just an optimization for arraycopy in default case
			switch (n) {
			case 2:
				a.copyTo(left + 1, left + 2);
			case 1:
				a.copyTo(left, left + 1);
				break;
			default:
				a.copyTo(left, left + 1, n);
			}
			a.setFloatArrayByIndex(left, pivotStruct);
		}
	}

	static int countRunAndMakeAscending(ArrayOfStructs a, int lo, int hi, StructComparator c) {
		if (DEBUG) {
			assert lo < hi;
		}

		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		// Find end of run, and reverse range if descending
		if (c.compare(a, runHi++, lo) < 0) { // Descending
			while (runHi < hi && c.compare(a, runHi, runHi - 1) < 0) {
				runHi++;
			}
			reverseRange(a, lo, runHi);
		} else { // Ascending
			while (runHi < hi && c.compare(a, runHi, runHi - 1) >= 0) {
				runHi++;
			}
		}

		return runHi - lo;
	}

	private static void reverseRange(ArrayOfStructs a, int lo, int hi) {
		hi--;
		while (lo < hi) {
			// TODO a.swap(lo++, hi++, tmp);
			a.swap(lo++, hi++);
		}
	}

	private static int minRunLength(int n) {
		if (DEBUG) {
			assert n >= 0;
		}

		int r = 0; // Becomes 1 if any 1 bits are shifted off
		while (n >= MIN_MERGE) {
			r |= (n & 1);
			n >>= 1;
		}
		return n + r;
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
				break; // Invariant is established
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
		if (DEBUG) {
			assert stackSize >= 2;
			assert i >= 0;
			assert i == stackSize - 2 || i == stackSize - 3;
		}

		int base1 = runBase[i];
		int len1 = runLen[i];
		int base2 = runBase[i + 1];
		int len2 = runLen[i + 1];
		if (DEBUG) {
			assert len1 > 0 && len2 > 0;
			assert base1 + len1 == base2;
		}

		/*
		 * Record the length of the combined runs; if i is the 3rd-last run now, also slide over the last run (which
		 * isn't involved in this merge). The current run (i+1) goes away in any case.
		 */
		runLen[i] = len1 + len2;
		if (i == stackSize - 3) {
			runBase[i + 1] = runBase[i + 2];
			runLen[i + 1] = runLen[i + 2];
		}
		stackSize--;

		/*
		 * Find where the first element of run2 goes in run1. Prior elements in run1 can be ignored (because they're
		 * already in place).
		 */
		int k = gallopRight(a[base2], base1, len1, 0, c);
		if (DEBUG) {
			assert k >= 0;
		}
		base1 += k;
		len1 -= k;
		if (len1 == 0) {
			return;
		}

		/*
		 * Find where the last element of run1 goes in run2. Subsequent elements in run2 can be ignored (because they're
		 * already in place).
		 */
		len2 = gallopLeft(a[base1 + len1 - 1], base2, len2, len2 - 1, c);
		if (DEBUG) {
			assert len2 >= 0;
		}
		if (len2 == 0) {
			return;
		}

		// Merge remaining runs, using tmp array with min(len1, len2) elements
		if (len1 <= len2) {
			mergeLo(base1, len1, base2, len2);
		} else {
			mergeHi(base1, len1, base2, len2);
		}
	}

	private static int gallopLeft(int key, ArrayOfStructs a, int base, int len, int hint, StructComparator c) {
		if (DEBUG) {
			assert len > 0 && hint >= 0 && hint < len;
		}
		int lastOfs = 0;
		int ofs = 1;
		if (c.compare(a, key, base + hint) > 0) {
			// Gallop right until a[base+hint+lastOfs] < key <= a[base+hint+ofs]
			int maxOfs = len - hint;
			while (ofs < maxOfs && c.compare(a, key, base + hint + ofs) > 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {// int overflow
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			lastOfs += hint;
			ofs += hint;
		} else { // key <= a[base + hint]
			// Gallop left until a[base+hint-ofs] < key <= a[base+hint-lastOfs]
			final int maxOfs = hint + 1;
			while (ofs < maxOfs && c.compare(a, key, base + hint - ofs) <= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		}
		if (DEBUG) {
			assert -1 <= lastOfs && lastOfs < ofs && ofs <= len;
		}

		/*
		 * Now a[base+lastOfs] < key <= a[base+ofs], so key belongs somewhere to the right of lastOfs but no farther
		 * right than ofs. Do a binary search, with invariant a[base + lastOfs - 1] < key <= a[base + ofs].
		 */
		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(a, key, base + m) > 0) {
				lastOfs = m + 1; // a[base + m] < key
			} else {
				ofs = m; // key <= a[base + m]
			}
		}
		if (DEBUG) {
			assert lastOfs == ofs; // so a[base + ofs - 1] < key <= a[base + ofs]
		}
		return ofs;
	}

	private static int gallopRight(int key, ArrayOfStructs a, int base, int len, int hint, StructComparator c) {
		if (DEBUG) {
			assert len > 0 && hint >= 0 && hint < len;
		}

		int ofs = 1;
		int lastOfs = 0;
		if (c.compare(a, key, base + hint) < 0) {
			// Gallop left until a[b+hint - ofs] <= key < a[b+hint - lastOfs]
			int maxOfs = hint + 1;
			while (ofs < maxOfs && c.compare(a, key, base + hint - ofs) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) {// int overflow
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		} else { // a[b + hint] <= key
			// Gallop right until a[b+hint + lastOfs] <= key < a[b+hint + ofs]
			int maxOfs = len - hint;
			while (ofs < maxOfs && c.compare(a, key, base + hint + ofs) >= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
					ofs = maxOfs;
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			lastOfs += hint;
			ofs += hint;
		}
		if (DEBUG) {
			assert -1 <= lastOfs && lastOfs < ofs && ofs <= len;
		}

		/*
		 * Now a[b + lastOfs] <= key < a[b + ofs], so key belongs somewhere to the right of lastOfs but no farther right
		 * than ofs. Do a binary search, with invariant a[b + lastOfs - 1] <= key < a[b + ofs].
		 */
		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(a, key, base + m) < 0) {
				ofs = m; // key < a[b + m]
			} else {
				lastOfs = m + 1; // a[b + m] <= key
			}
		}
		if (DEBUG) {
			assert lastOfs == ofs; // so a[b + ofs - 1] <= key < a[b + ofs]
		}
		return ofs;
	}

	private void mergeLo(int base1, int len1, int base2, int len2) {
		if (DEBUG) {
			assert len1 > 0 && len2 > 0 && base1 + len1 == base2;
		}

		// Copy first run into temp array
		ArrayOfStructs a = this.a; // For performance
		ArrayOfStructs tmp = this.tmp; // For performance
		tmp.ensureCapacity(len1);
		System.arraycopy(a, base1, tmp, 0, len1);

		int cursor1 = 0; // Indexes into tmp array
		int cursor2 = base2; // Indexes int a
		int dest = base1; // Indexes int a

		// Move first element of second run and deal with degenerate cases
		a.copyTo(cursor2++, dest++);
		if (--len2 == 0) {
			System.arraycopy(tmp, cursor1, a, dest, len1);
			return;
		}
		if (len1 == 1) {
			System.arraycopy(a, cursor2, a, dest, len2);
			a.setItem(tmp, cursor1, dest + len2);// Last elt of run 1 to end of merge
			return;
		}

		StructComparator c = this.c; // Use local variable for performance
		int minGallop = this.minGallop; // " " " " "
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run starts winning consistently.
			 */
			do {
				if (DEBUG) {
					assert len1 > 1 && len2 > 0;
				}
				if (c.compare(a[cursor2], tmp[cursor1]) < 0) {
					a.copyTo(cursor2++, dest++);
					count2++;
					count1 = 0;
					if (--len2 == 0) {
						break outer;
					}
				} else {
					a.setItem(tmp, cursor1++, dest++);
					count1++;
					count2 = 0;
					if (--len1 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that, and continue galloping
			 * until (if ever) neither run appears to be winning consistently anymore.
			 */
			do {
				if (DEBUG) {
					assert len1 > 1 && len2 > 0;
				}
				count1 = gallopRight(a[cursor2], tmp, cursor1, len1, 0, c);
				if (count1 != 0) {
					System.arraycopy(tmp, cursor1, a, dest, count1);
					dest += count1;
					cursor1 += count1;
					len1 -= count1;
					if (len1 <= 1) // len1 == 1 || len1 == 0
						break outer;
				}
				a.copyTo(cursor2++, dest++);
				if (--len2 == 0) {
					break outer;
				}

				count2 = gallopLeft(tmp[cursor1], a, cursor2, len2, 0, c);
				if (count2 != 0) {
					System.arraycopy(a, cursor2, a, dest, count2);
					dest += count2;
					cursor2 += count2;
					len2 -= count2;
					if (len2 == 0) {
						break outer;
					}
				}
				a.setItem(tmp, cursor1++, dest++);
				if (--len1 == 1) {
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

		if (len1 == 1) {
			if (DEBUG) {
				assert len2 > 0;
			}
			a.copyTo(cursor2, dest, len2);
			a.setItem(tmp, cursor1, dest + len2); // Last elt of run 1 to end of merge
		} else if (len1 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			if (DEBUG) {
				assert len2 == 0;
				assert len1 > 1;
			}
			System.arraycopy(tmp, cursor1, a, dest, len1);
		}
	}

	private void mergeHi(int base1, int len1, int base2, int len2) {
		if (DEBUG) {
			assert len1 > 0 && len2 > 0 && base1 + len1 == base2;
		}

		// Copy second run into temp array
		ArrayOfStructs a = this.a; // For performance
		ArrayOfStructs tmp = this.tmp;
		tmp.ensureCapacity(len2);
		System.arraycopy(a, base2, tmp, 0, len2);

		int cursor1 = base1 + len1 - 1; // Indexes into a
		int cursor2 = len2 - 1; // Indexes into tmp array
		int dest = base2 + len2 - 1; // Indexes into a

		// Move last element of first run and deal with degenerate cases
		a.swap(cursor1--, dest--);
		if (--len1 == 0) {
			System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
			return;
		}
		if (len2 == 1) {
			dest -= len1;
			cursor1 -= len1;
			a.copyTo(cursor1 + 1, dest + 1, len1);
			a.setItem(tmp, cursor2, dest);
			return;
		}

		StructComparator c = this.c; // Use local variable for performance
		int minGallop = this.minGallop; // " " " " "
		outer: while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run appears to win consistently.
			 */
			do {
				if (DEBUG) {
					assert len1 > 0 && len2 > 1;
				}
				if (c.compare(tmp[cursor2], a[cursor1]) < 0) {
					a.copyTo(cursor1--, dest--);
					count1++;
					count2 = 0;
					if (--len1 == 0) {
						break outer;
					}
				} else {
					a.setItem(tmp, cursor2--, dest--);
					count2++;
					count1 = 0;
					if (--len2 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that, and continue galloping
			 * until (if ever) neither run appears to be winning consistently anymore.
			 */
			do {
				if (DEBUG) {
					assert len1 > 0 && len2 > 1;
				}
				count1 = len1 - gallopRight(cursor2, tmp, a, base1, len1, len1 - 1, c);
				if (count1 != 0) {
					dest -= count1;
					cursor1 -= count1;
					len1 -= count1;
					System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
					if (len1 == 0)
						break outer;
				}
				a.copyTo(cursor2--, dest--);
				if (--len2 == 1) {
					break outer;
				}

				count2 = len2 - gallopLeft(a[cursor1], tmp, 0, len2, len2 - 1, c);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					len2 -= count2;
					System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
					if (len2 <= 1) // len2 == 1 || len2 == 0
						break outer;
				}
				a.copyTo(cursor1--, dest--);
				if (--len1 == 0)
					break outer;
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0)
				minGallop = 0;
			minGallop += 2; // Penalize for leaving gallop mode
		} // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // Write back to field

		if (len2 == 1) {
			if (DEBUG) {
				assert len1 > 0;
			}
			dest -= len1;
			cursor1 -= len1;
			System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
			a.setItem(tmp, cursor2, dest); // Move first elt of run2 to front of merge
		} else if (len2 == 0) {
			throw new IllegalArgumentException("Comparison method violates its general contract!");
		} else {
			if (DEBUG) {
				assert len1 == 0 && len2 > 0;
			}
			System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
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
