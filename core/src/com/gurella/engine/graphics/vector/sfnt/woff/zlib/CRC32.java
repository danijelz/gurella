package com.gurella.engine.graphics.vector.sfnt.woff.zlib;

public class CRC32 implements Checksum {
	/*
	 * The following logic has come from RFC1952.
	 */
	private int v = 0;
	private static int[] crc_table = null;
	static {
		crc_table = new int[256];
		for (int n = 0; n < 256; n++) {
			int c = n;
			for (int k = 8; --k >= 0;) {
				if ((c & 1) != 0)
					c = 0xedb88320 ^ (c >>> 1);
				else
					c = c >>> 1;
			}
			crc_table[n] = c;
		}
	}

	@Override
	public void update(byte[] buf, int index, int len) {
		int c = ~v;
		while (--len >= 0)
			c = crc_table[(c ^ buf[index++]) & 0xff] ^ (c >>> 8);
		v = ~c;
	}

	@Override
	public void reset() {
		v = 0;
	}

	@Override
	public void reset(long vv) {
		v = (int) (vv & 0xffffffffL);
	}

	@Override
	public long getValue() {
		return v & 0xffffffffL;
	}
}
