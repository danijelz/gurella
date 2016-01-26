package com.gurella.engine.graphics.vector.sfnt.woff;

public class Unzip {
	private static final int[] bitReverse = new int[] { 0x00, 0x80, 0x40, 0xc0, 0x20, 0xa0, 0x60, 0xe0, 0x10, 0x90, 0x50, 0xd0, 0x30, 0xb0, 0x70,
			0xf0, 0x08, 0x88, 0x48, 0xc8, 0x28, 0xa8, 0x68, 0xe8, 0x18, 0x98, 0x58, 0xd8, 0x38, 0xb8, 0x78, 0xf8, 0x04, 0x84, 0x44, 0xc4, 0x24, 0xa4,
			0x64, 0xe4, 0x14, 0x94, 0x54, 0xd4, 0x34, 0xb4, 0x74, 0xf4, 0x0c, 0x8c, 0x4c, 0xcc, 0x2c, 0xac, 0x6c, 0xec, 0x1c, 0x9c, 0x5c, 0xdc, 0x3c,
			0xbc, 0x7c, 0xfc, 0x02, 0x82, 0x42, 0xc2, 0x22, 0xa2, 0x62, 0xe2, 0x12, 0x92, 0x52, 0xd2, 0x32, 0xb2, 0x72, 0xf2, 0x0a, 0x8a, 0x4a, 0xca,
			0x2a, 0xaa, 0x6a, 0xea, 0x1a, 0x9a, 0x5a, 0xda, 0x3a, 0xba, 0x7a, 0xfa, 0x06, 0x86, 0x46, 0xc6, 0x26, 0xa6, 0x66, 0xe6, 0x16, 0x96, 0x56,
			0xd6, 0x36, 0xb6, 0x76, 0xf6, 0x0e, 0x8e, 0x4e, 0xce, 0x2e, 0xae, 0x6e, 0xee, 0x1e, 0x9e, 0x5e, 0xde, 0x3e, 0xbe, 0x7e, 0xfe, 0x01, 0x81,
			0x41, 0xc1, 0x21, 0xa1, 0x61, 0xe1, 0x11, 0x91, 0x51, 0xd1, 0x31, 0xb1, 0x71, 0xf1, 0x09, 0x89, 0x49, 0xc9, 0x29, 0xa9, 0x69, 0xe9, 0x19,
			0x99, 0x59, 0xd9, 0x39, 0xb9, 0x79, 0xf9, 0x05, 0x85, 0x45, 0xc5, 0x25, 0xa5, 0x65, 0xe5, 0x15, 0x95, 0x55, 0xd5, 0x35, 0xb5, 0x75, 0xf5,
			0x0d, 0x8d, 0x4d, 0xcd, 0x2d, 0xad, 0x6d, 0xed, 0x1d, 0x9d, 0x5d, 0xdd, 0x3d, 0xbd, 0x7d, 0xfd, 0x03, 0x83, 0x43, 0xc3, 0x23, 0xa3, 0x63,
			0xe3, 0x13, 0x93, 0x53, 0xd3, 0x33, 0xb3, 0x73, 0xf3, 0x0b, 0x8b, 0x4b, 0xcb, 0x2b, 0xab, 0x6b, 0xeb, 0x1b, 0x9b, 0x5b, 0xdb, 0x3b, 0xbb,
			0x7b, 0xfb, 0x07, 0x87, 0x47, 0xc7, 0x27, 0xa7, 0x67, 0xe7, 0x17, 0x97, 0x57, 0xd7, 0x37, 0xb7, 0x77, 0xf7, 0x0f, 0x8f, 0x4f, 0xcf, 0x2f,
			0xaf, 0x6f, 0xef, 0x1f, 0x9f, 0x5f, 0xdf, 0x3f, 0xbf, 0x7f, 0xff };

	private static final int[] cplens = new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131,
			163, 195, 227, 258, 0, 0 };

	// 99 == invalid
	private static final int[] cplext = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99 }; 

	private static final int[] cpdist = new int[] { 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0007, 0x0009, 0x000d, 0x0011, 0x0019, 0x0021, 0x0031,
			0x0041, 0x0061, 0x0081, 0x00c1, 0x0101, 0x0181, 0x0201, 0x0301, 0x0401, 0x0601, 0x0801, 0x0c01, 0x1001, 0x1801, 0x2001, 0x3001, 0x4001,
			0x6001 };

	private static final int[] cpdext = new int[] { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };

	private static final int[] border = new int[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

	private byte[] barray;
	private int fmax;
	private int[] flens;
	private byte[] outputArr;
	private int outputPos;
	private int[] buf32k = new int[32768];
	private int bIdx = 0;
	private int barraylen;
	private int bytepos = 0;
	private int bb = 1;
	private HufNode[] literalTree = new HufNode[288];
	private HufNode[] distanceTree = new HufNode[32];
	private int treepos = 0;
	private HufNode[] Places = null;
	private int len = 0;
	private int[] fpos = new int[17];

	public Unzip(byte[] barray) {
		this.barray = barray;
		barraylen = barray.length;
	}
	
	public void deflate(byte[] outputArr) {
		this.outputArr = outputArr;
		deflateLoop();
	}

	private int readByte() {
		if (bytepos < barraylen) {
			return barray[bytepos++];
		}

		return -1;
	}

	private void byteAlign() {
		bb = 1;
	}

	private int readBit() {
		int carry;

		carry = (bb & 1);
		bb >>= 1;

		if (bb == 0) {
			bb = readByte();
			carry = (bb & 1);
			bb = (bb >> 1) | 0x80;
		}

		return carry;
	}

	private int readBits(int a) {
		int res = 0;
		int i = a;
		while (i-- != 0) {
			res = (res << 1) | readBit();
		}

		if (a != 0) {
			res = bitReverse[res] >> (8 - a);
		}

		return res;
	}

	private void flushBuffer() {
		bIdx = 0;
	}

	private void addBuffer(int a) {
		buf32k[bIdx++] = a;
		outputArr[outputPos++] = (byte) a;

		if (bIdx == 0x8000) {
			bIdx = 0;
		}
	}

	private int isPat() {
		while (true) {
			if (fpos[len] >= fmax) {
				return -1;
			}

			if (flens[fpos[len]] == len) {
				return fpos[len]++;
			}

			fpos[len]++;
		}
	}

	private int rec() {
		HufNode curplace = Places[treepos];
		int tmp;

		if (len == 17) {
			return -1;
		}
		treepos++;
		len++;

		tmp = isPat();

		if (tmp >= 0) {
			/* leaf cell for 0-bit */
			curplace.b0 = tmp;
		} else {
			/* Not a Leaf cell */
			curplace.b0 = 0x8000;

			if (rec() != 0) {
				return -1;
			}
		}

		tmp = isPat();

		if (tmp >= 0) {
			/* leaf cell for 1-bit */
			curplace.b1 = tmp;
			/* Just for the display routine */
			curplace.jump = null;
		} else {
			/* Not a Leaf cell */
			curplace.b1 = 0x8000;
			curplace.jump = Places[treepos];
			if (rec() != 0) {
				return -1;
			}
		}
		len--;

		return 0;
	}

	private int createTree(HufNode[] currentTree, int numval, int[] lengths) {
		int i;

		Places = currentTree;
		treepos = 0;
		flens = lengths;
		fmax = numval;

		for (i = 0; i < 17; i++) {
			fpos[i] = 0;
		}
		len = 0;

		if (rec() != 0) {
			return -1;
		}

		return 0;
	}

	private int decodeValue(HufNode[] currentTree) {
		int len, i, b, xtreepos = 0;
		HufNode X = currentTree[xtreepos];

		/* decode one symbol of the data */
		while (true) {
			b = readBit();

			if (b != 0) {
				if ((X.b1 & 0x8000) == 0) {
					/* If leaf node, return data */
					return X.b1;
				}

				X = X.jump;
				len = currentTree.length;

				for (i = 0; i < len; i++) {
					if (currentTree[i] == X) {
						xtreepos = i;
						break;
					}
				}
			} else {
				if ((X.b0 & 0x8000) == 0) {
					/* If leaf node, return data */
					return X.b0;
				}
				xtreepos++;
				X = currentTree[xtreepos];
			}
		}
	}

	private int deflateLoop() {
		int last, c, type, i, j, l, len, blockLen, dist, cSum, n, literalCodes, distCodes, lenCodes;
		int[] ll;
		int[] ll2;

		do {
			last = readBit();
			type = readBits(2);

			if (type == 0) {
				// Stored
				byteAlign();
				blockLen = readByte();
				blockLen |= (readByte() << 8);

				cSum = readByte();
				cSum |= (readByte() << 8);

				if (((blockLen ^ ~cSum) & 0xffff) != 0) {
					throw new IllegalStateException("BlockLen checksum mismatch\n");
				}

				while (blockLen-- != 0) {
					c = readByte();
					addBuffer(c);
				}
			} else if (type == 1) {
				/* Fixed Huffman tables -- fixed decode routine */
				while (true) {
					j = (bitReverse[readBits(7)] >> 1);

					if (j > 23) {
						j = (j << 1) | readBit(); /* 48..255 */

						if (j > 199) { /* 200..255 */
							j -= 128; /* 72..127 */
							j = (j << 1) | readBit(); /* 144..255 << */
						} else { /* 48..199 */
							j -= 48; /* 0..151 */
							if (j > 143) {
								j = j + 136; /* 280..287 << */
								/* 0..143 << */
							}
						}
					} else { /* 0..23 */
						j += 256; /* 256..279 << */
					}

					if (j < 256) {
						addBuffer(j);
					} else if (j == 256) {
						/* EOF */
						break;
					} else {
						j -= 256 + 1; /* bytes + EOF */
						len = readBits(cplext[j]) + cplens[j];
						j = bitReverse[readBits(5)] >> 3;

						if (cpdext[j] > 8) {
							dist = readBits(8);
							dist |= (readBits(cpdext[j] - 8) << 8);
						} else {
							dist = readBits(cpdext[j]);
						}

						dist += cpdist[j];

						for (j = 0; j < len; j++) {
							c = buf32k[(bIdx - dist) & 0x7fff];
							addBuffer(c);
						}
					}
				} // while
			} else if (type == 2) {
				// "static" just to preserve stack
				ll = new int[288 + 32];

				// Dynamic Huffman tables
				literalCodes = 257 + readBits(5);
				distCodes = 1 + readBits(5);
				lenCodes = 4 + readBits(4);

				for (j = 0; j < 19; j++) {
					ll[j] = 0;
				}

				// Get the decode tree code lengths

				for (j = 0; j < lenCodes; j++) {
					ll[border[j]] = readBits(3);
				}
				len = distanceTree.length;

				for (i = 0; i < len; i++) {
					distanceTree[i] = new HufNode();
				}

				if (createTree(distanceTree, 19, ll) != 0) {
					flushBuffer();
					return 1;
				}

				// read in literal and distance code lengths
				n = literalCodes + distCodes;
				i = 0;

				while (i < n) {
					j = decodeValue(distanceTree);

					// length of code in bits (0..15)
					if (j < 16) {
						ll[i++] = j;
						// repeat last length 3 to 6 times
					} else if (j == 16) {
						j = 3 + readBits(2);

						if (i + j > n) {
							flushBuffer();
							return 1;
						}
						l = i != 0 ? ll[i - 1] : 0;

						while (j-- != 0) {
							ll[i++] = l;
						}
					} else {
						// 3 to 10 zero length codes
						if (j == 17) {
							j = 3 + readBits(3);
							// j == 18: 11 to 138 zero length codes
						} else {
							j = 11 + readBits(7);
						}

						if (i + j > n) {
							flushBuffer();
							return 1;
						}

						while (j-- != 0) {
							ll[i++] = 0;
						}
					}
				}

				// Can overwrite tree decode tree as it is not used anymore
				len = literalTree.length;
				for (i = 0; i < len; i++) {
					literalTree[i] = new HufNode();
				}

				if (createTree(literalTree, literalCodes, ll) != 0) {
					flushBuffer();
					return 1;
				}

				len = literalTree.length;

				for (i = 0; i < len; i++) {
					distanceTree[i] = new HufNode();
				}

				ll2 = new int[ll.length - literalCodes];

				for (i = literalCodes; i < ll.length; i++) {
					ll2[i - literalCodes] = ll[i];
				}

				if (createTree(distanceTree, distCodes, ll2) != 0) {
					flushBuffer();
					return 1;
				}

				while (true) {
					j = decodeValue(literalTree);

					// In C64: if carry set
					if (j >= 256) {
						j -= 256;
						if (j == 0) {
							// EOF
							break;
						}

						j -= 1;
						len = readBits(cplext[j]) + cplens[j];
						j = decodeValue(distanceTree);

						if (cpdext[j] > 8) {
							dist = readBits(8);
							dist |= (readBits(cpdext[j] - 8) << 8);
						} else {
							dist = readBits(cpdext[j]);
						}

						dist += cpdist[j];

						while (len-- != 0) {
							c = buf32k[(bIdx - dist) & 0x7fff];
							addBuffer(c);
						}
					} else {
						addBuffer(j);
					}
				}
			}
		} while (last == 0);

		flushBuffer();
		byteAlign();

		return 0;
	}

	private static class HufNode {
		int b0 = 0;
		int b1 = 0;
		HufNode jump = null;
	}
}
