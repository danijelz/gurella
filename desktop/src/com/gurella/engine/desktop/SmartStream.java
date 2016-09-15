//  $Id: SmartStream.java,v 1.1 2006/01/17 03:01:18 mclark Exp $
//  Copyright ©2006 Matthew Clark. All Rights Reserved.
//  See http://www.scriptio.us for licensing restrictions.

package com.gurella.engine.desktop;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

//  SmartStream

public class SmartStream extends InputStream implements Runnable {

	// constants

	static final int CHUNK_SIZE = 8192;

	// Chunk subclass

	class Chunk {
		// fields
		public byte[] data;

		// constructor
		public Chunk() {
			data = new byte[CHUNK_SIZE];
		}
	}

	// instance variables

	private int length = 0, // total data length
			count = 0, // amount of data in buffer
			pos = 0, // current position
			mark = 0; // mark
	private ArrayList buffers = null; // buffer list
	private InputStream sourceStream; // source data input stream

	// constructor

	public SmartStream(byte[] data) {
		sourceStream = null;
		if (data != null) {
			length = data.length;
			buffers = new ArrayList();
			while (count < length) {
				// add chunk
				buffers.add(new Chunk());
				Chunk chunk = this.getChunk(count);
				int bytes = Math.min(length - count, CHUNK_SIZE);
				System.arraycopy(data, count, chunk.data, 0, bytes);
				count += bytes;
			}
		}
	}

	public SmartStream(InputStream is, int aLength) {
		sourceStream = is;
		length = aLength;
		buffers = new ArrayList();
		if (sourceStream != null)
			(new Thread(this)).start();
	}

	public SmartStream(InputStream is) throws IOException {
		this(is, (is != null) ? is.available() : 0);
	}

	// getters

	public int tell() {
		return pos;
	}

	public int getCount() {
		return count;
	}

	public int getLength() {
		return length;
	}

	private Chunk getChunk(int x) {
		return (Chunk) buffers.get(x / CHUNK_SIZE);
	}

	// available

	public int available() {
		return length - pos;
	}

	// close

	public void close() {
		// free up memory
		if (buffers != null) {
			buffers.clear();
			buffers = null;
		}
	}

	// mark & skip

	public boolean markSupported() {
		return true;
	}

	public void mark(int readlimit) {
		mark = pos;
	}

	public void mark() {
		mark = pos;
	}

	public void reset() {
		pos = mark;
	}

	public long skip(long n) {
		int newPos = pos + (int) n;
		if (newPos > length)
			length = newPos;
		else if (newPos < 0)
			newPos = 0;
		long result = newPos - pos;
		pos = newPos;
		return result;
	}

	// read

	public int read() throws IOException {
		if (pos >= count)
			throw new IOException("Attempting to read past buffer.");
		return this.getChunk(pos).data[pos++ % CHUNK_SIZE];
	}

	public int read2() throws IOException {
		int b0 = this.read(), b1 = this.read();
		return ((b1 & 0xff) << 8) | (b0 & 0xff);
	}

	public synchronized int readWait() {
		while (count <= pos)
			Thread.yield();
		return this.getChunk(pos).data[pos++ % CHUNK_SIZE];
	}

	public synchronized int read(byte[] b, int off, int len) {
		int bytesRead = 0;
		// check reading size
		if (pos + len > length)
			len = length - pos;
		// possibly yield
		while ((len > 0) && (pos + len > count))
			Thread.yield();
		// copy data
		while ((len > 0) && (pos < count)) {
			int copy = Math.min(Math.min(CHUNK_SIZE - (pos % CHUNK_SIZE), len), count - pos);
			System.arraycopy(this.getChunk(pos).data, pos % CHUNK_SIZE, b, off, copy);
			off += copy;
			len -= copy;
			pos += copy;
			bytesRead += copy;
		}
		return bytesRead;
	}

	public synchronized int read(byte[] b) {
		return (b != null) ? this.read(b, 0, b.length) : 0;
	}

	// seek

	public void seek(int aPos) throws IOException {
		if (aPos > length)
			throw new IOException("Attempt to seek past end of stream.");
		pos = aPos;
	}

	// get input (threaded)

	public void run() {
		try {
			while (count < length) {
				// add new buffer chunk?
				if ((count % CHUNK_SIZE) == 0)
					buffers.add(new Chunk());
				// calculate # bytes to read
				int copy = Math.min(length - count, CHUNK_SIZE - (count % CHUNK_SIZE));
				// read bytes (may yield thread)
				copy = sourceStream.read(this.getChunk(count).data, count % CHUNK_SIZE, copy);
				count += copy;
				// DEBUG - SLOW DOWN THE DATA CAPTURE
				/*
				 * try { Thread.currentThread().sleep(250); } catch (InterruptedException exception) { }
				 */
			}
			// free up source stream
			sourceStream.close();
			sourceStream = null;
		} catch (IOException exception) {
		}
	}

	public float getProgress() {
		return (length > 0) ? count / (float) length : 0f;
	}
}