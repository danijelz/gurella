package com.gurella.engine.asset.loader.audio.ogg;

public class Buffer {
	private static final int[] mask = { 0x00000000, 0x00000001, 0x00000003, 0x00000007, 0x0000000f, 0x0000001f,
			0x0000003f, 0x0000007f, 0x000000ff, 0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff, 0x00001fff, 0x00003fff,
			0x00007fff, 0x0000ffff, 0x0001ffff, 0x0003ffff, 0x0007ffff, 0x000fffff, 0x001fffff, 0x003fffff, 0x007fffff,
			0x00ffffff, 0x01ffffff, 0x03ffffff, 0x07ffffff, 0x0fffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
			0xffffffff };

	private int ptr = 0;
	private byte[] buffer = null;
	private int endbit = 0;
	private int endbyte = 0;
	private int storage = 0;

	public void init(byte[] buf, int start, int bytes) {
		ptr = start;
		buffer = buf;
		endbit = endbyte = 0;
		storage = bytes;
	}

	public void read(byte[] s, int bytes) {
		int temp = bytes;
		int i = 0;
		while (temp-- != 0) {
			s[i++] = (byte) (read(8));
		}
	}

	public int read(int bits) {
		int temp = bits;
		int ret;
		int m = mask[temp];

		temp += endbit;

		if (endbyte + 4 >= storage) {
			ret = -1;
			if (endbyte + (temp - 1) / 8 >= storage) {
				ptr += temp / 8;
				endbyte += temp / 8;
				endbit = temp & 7;
				return (ret);
			}
		}

		ret = ((buffer[ptr]) & 0xff) >>> endbit;
		if (temp > 8) {
			ret |= ((buffer[ptr + 1]) & 0xff) << (8 - endbit);
			if (temp > 16) {
				ret |= ((buffer[ptr + 2]) & 0xff) << (16 - endbit);
				if (temp > 24) {
					ret |= ((buffer[ptr + 3]) & 0xff) << (24 - endbit);
					if (temp > 32 && endbit != 0) {
						ret |= ((buffer[ptr + 4]) & 0xff) << (32 - endbit);
					}
				}
			}
		}

		ret &= m;

		ptr += temp / 8;
		endbyte += temp / 8;
		endbit = temp & 7;
		return (ret);
	}
}
