package com.gurella.engine.graphics.vector.sfnt.woff.zlib;

public interface Checksum {
	void update(byte[] buf, int index, int len);

	void reset();

	void reset(long init);

	long getValue();
}
