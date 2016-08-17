package com.gurella.engine.asset.loader.audio.ogg;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class Page implements Poolable {
	private static int[] crc_lookup = new int[256];

	static {
		for (int i = 0; i < crc_lookup.length; i++) {
			crc_lookup[i] = crc_entry(i);
		}
	}

	private static int crc_entry(int index) {
		int r = index << 24;
		for (int i = 0; i < 8; i++) {
			if ((r & 0x80000000) != 0) {
				r = (r << 1) ^ 0x04c11db7;
			} else {
				r <<= 1;
			}
		}
		return (r & 0xffffffff);
	}

	public byte[] header_base;
	public int header;
	public int header_len;
	public byte[] body_base;
	public int body;
	public int body_len;

	public static Page obtain() {
		return Pools.obtain(Page.class);
	}

	int version() {
		return header_base[header + 4] & 0xff;
	}

	int continued() {
		return (header_base[header + 5] & 0x01);
	}

	public int bos() {
		return (header_base[header + 5] & 0x02);
	}

	public int eos() {
		return (header_base[header + 5] & 0x04);
	}

	public long granulepos() {
		long foo = header_base[header + 13] & 0xff;
		foo = (foo << 8) | (header_base[header + 12] & 0xff);
		foo = (foo << 8) | (header_base[header + 11] & 0xff);
		foo = (foo << 8) | (header_base[header + 10] & 0xff);
		foo = (foo << 8) | (header_base[header + 9] & 0xff);
		foo = (foo << 8) | (header_base[header + 8] & 0xff);
		foo = (foo << 8) | (header_base[header + 7] & 0xff);
		foo = (foo << 8) | (header_base[header + 6] & 0xff);
		return (foo);
	}

	public int serialno() {
		return (header_base[header + 14] & 0xff) | ((header_base[header + 15] & 0xff) << 8) | ((header_base[header + 16] & 0xff) << 16)
				| ((header_base[header + 17] & 0xff) << 24);
	}

	int pageno() {
		return (header_base[header + 18] & 0xff) | ((header_base[header + 19] & 0xff) << 8) | ((header_base[header + 20] & 0xff) << 16)
				| ((header_base[header + 21] & 0xff) << 24);
	}

	void checksum() {
		int crc_reg = 0;

		for (int i = 0; i < header_len; i++) {
			crc_reg = (crc_reg << 8) ^ crc_lookup[((crc_reg >>> 24) & 0xff) ^ (header_base[header + i] & 0xff)];
		}
		for (int i = 0; i < body_len; i++) {
			crc_reg = (crc_reg << 8) ^ crc_lookup[((crc_reg >>> 24) & 0xff) ^ (body_base[body + i] & 0xff)];
		}
		header_base[header + 22] = (byte) crc_reg;
		header_base[header + 23] = (byte) (crc_reg >>> 8);
		header_base[header + 24] = (byte) (crc_reg >>> 16);
		header_base[header + 25] = (byte) (crc_reg >>> 24);
	}

	public void free() {
		Pools.free(this);
	}

	@Override
	public void reset() {
		header_base = null;
		header = 0;
		header_len = 0;
		body_base = null;
		body = 0;
		body_len = 0;
	}
}
