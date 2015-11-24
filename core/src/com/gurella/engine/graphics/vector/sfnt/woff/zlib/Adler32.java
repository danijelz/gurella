package com.gurella.engine.graphics.vector.sfnt.woff.zlib;

public class Adler32 implements Checksum {
	// largest prime smaller than 65536
	static final private int BASE = 65521;
	// NMAX is the largest n such that 255n(n+1)/2 + (n+1)(BASE-1) <= 2^32-1
	static final private int NMAX = 5552;

	private long s1 = 1L;
	private long s2 = 0L;

	@Override
	public void reset(long init) {
		s1 = init & 0xffff;
		s2 = (init >> 16) & 0xffff;
	}

	@Override
	public void reset() {
		s1 = 1L;
		s2 = 0L;
	}

	@Override
	public long getValue() {
		return ((s2 << 16) | s1);
	}

	@Override
	public void update(byte[] buf, int index, int len) {
		if (len == 1) {
			s1 += buf[index++] & 0xff;
			s2 += s1;
			s1 %= BASE;
			s2 %= BASE;
			return;
		}

		int len1 = len / NMAX;
		int len2 = len % NMAX;
		while (len1-- > 0) {
			int k = NMAX;
			len -= k;
			while (k-- > 0) {
				s1 += buf[index++] & 0xff;
				s2 += s1;
			}
			s1 %= BASE;
			s2 %= BASE;
		}

		int k = len2;
		len -= k;
		while (k-- > 0) {
			s1 += buf[index++] & 0xff;
			s2 += s1;
		}
		s1 %= BASE;
		s2 %= BASE;
	}
}
