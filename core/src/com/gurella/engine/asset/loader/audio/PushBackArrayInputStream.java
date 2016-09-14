package com.gurella.engine.asset.loader.audio;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;
import com.gurella.engine.pool.PoolService;

public class PushBackArrayInputStream extends InputStream {
	private static final int fragmentSize = 512;
	/*
	 * private static final Pool<byte[]> fragmentsPool = new Pool<byte[]>() {
	 * 
	 * @Override protected byte[] newObject() { return new byte[fragmentSize]; } };
	 */

	private Array<byte[]> buffer = new Array<byte[]>();
	private long bufferSize;

	private long pos;
	private long markpos = -1;
	private boolean eof;

	private InputStream in;

	public void init(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		int bufferFragment = (int) (pos / fragmentSize);
		if (bufferFragment >= buffer.size) {
			if (!fill()) {
				return -1;
			}
		}
		return buffer.get(bufferFragment)[(int) (pos++ % fragmentSize)] & 0xff;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int offset = off;
		if (b == null) {
			throw new NullPointerException();
		} else if (offset < 0 || len < 0 || len > b.length - offset) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int available = (int) (bufferSize - pos);
		if (available < 1 && eof) {
			return -1;
		}

		while (available < len && fill()) {
			available = (int) (bufferSize - pos);
		}

		int count = Math.min(available, len);
		while (count > 0) {
			int bufferFragment = (int) (pos / fragmentSize);
			int fragmentPos = (int) (pos % fragmentSize);
			byte[] fragment = buffer.get(bufferFragment);
			int fragmentCount = Math.min(fragmentSize - fragmentPos, count);
			System.arraycopy(fragment, fragmentPos, b, offset, fragmentCount);
			offset += fragmentCount;
			count -= fragmentCount;
			pos += fragmentCount;
		}
		return Math.min(available, len);
	}

	@Override
	public long skip(long n) throws IOException {
		if (n <= 0) {
			return 0;
		}

		long available = bufferSize - pos;
		while (available < n && fill()) {
			available = bufferSize - pos;
		}

		long skipped = Math.min(available, n);
		pos += skipped;
		return skipped;
	}

	private boolean fill() throws IOException {
		if (eof) {
			return false;
		}

		byte[] fragment = PoolService.obtainByteArray(fragmentSize, 0);
		buffer.add(fragment);
		int count = in.read(fragment);

		if (count > 0) {
			bufferSize += count;
		}

		if (count < fragmentSize) {
			eof = true;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int available() throws IOException {
		return (int) (bufferSize - pos);
	}

	public void unread(int len) throws IOException {
		if (len > pos) {
			throw new IOException("Push back buffer is full");
		}

		pos -= len;
	}

	@Override
	public synchronized void mark(int readlimit) {
		markpos = pos;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public synchronized void reset() throws IOException {
		if (markpos < 0) {
			throw new IOException("Resetting to invalid mark");
		}
		pos = markpos;
	}

	@Override
	public void close() throws IOException {
		StreamUtils.closeQuietly(in);
	}

	private void closeQuietly() {
		StreamUtils.closeQuietly(in);
	}

	public void seek(long n) throws IOException {
		if (n < 0) {
			throw new IOException("Negative seek offset");
		}

		if (n > pos) {
			skip(n - pos);
		}

		pos = n;
	}

	public void poolableReset() {
		closeQuietly();
		in = null;
		bufferSize = 0;
		pos = 0;
		markpos = -1;
		eof = false;
		for (int i = 0; i < buffer.size; i++) {
			PoolService.free(buffer.get(i));
		}
		buffer.clear();
	}
}
