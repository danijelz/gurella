package com.gurella.engine.graphics.vector.sfnt.woff.zlib;

public class ZStream {
	static final private int MAX_WBITS = 15; // 32K LZ77 window
	static final private int DEF_WBITS = MAX_WBITS;

	static final private int Z_OK = 0;
	static final private int Z_STREAM_ERROR = -2;

	public byte[] next_in; // next input byte
	public int next_in_index;
	public int avail_in; // number of bytes available at next_in
	public long total_in; // total nb of input bytes read so far

	public byte[] next_out; // next output byte should be put there
	public int next_out_index;
	public int avail_out; // remaining free space at next_out
	public long total_out; // total nb of bytes output so far

	public String msg;

	Inflate istate;

	int data_type; // best guess about the data type: ascii or binary

	Checksum checksum;
	
	private Adler32 adler32 = new Adler32();
	private CRC32 crc32 = new CRC32();

	public ZStream() {
		this.checksum = adler32;
	}
	
	void checksumToAdler() {
		adler32.reset();
		checksum = adler32;
	}
	
	void checksumToCrc() {
		crc32.reset();
		checksum = crc32;
	}

	public int inflateInit() {
		return inflateInit(DEF_WBITS);
	}

	public int inflateInit(boolean nowrap) {
		return inflateInit(DEF_WBITS, nowrap);
	}

	public int inflateInit(int w) {
		return inflateInit(w, false);
	}

	public int inflateInit(int w, boolean nowrap) {
		istate = new Inflate(this);
		return istate.inflateInit(nowrap ? -w : w);
	}

	public int inflate(int f) {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflate(f);
	}

	public int inflateEnd() {
		if (istate == null)
			return Z_STREAM_ERROR;
		int ret = istate.inflateEnd();
		return ret;
	}

	public int inflateSync() {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSync();
	}

	public int inflateSyncPoint() {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSyncPoint();
	}

	public int inflateSetDictionary(byte[] dictionary, int dictLength) {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSetDictionary(dictionary, dictLength);
	}

	public boolean inflateFinished() {
		return istate.mode == Inflate.DONE;
	}

	public long getAdler() {
		return checksum.getValue();
	}

	public void free() {
		next_in = null;
		next_out = null;
		msg = null;
	}

	public void setOutput(byte[] buf) {
		setOutput(buf, 0, buf.length);
	}

	public void setOutput(byte[] buf, int off, int len) {
		next_out = buf;
		next_out_index = off;
		avail_out = len;
	}

	public void setInput(byte[] buf) {
		setInput(buf, 0, buf.length, false);
	}

	public void setInput(byte[] buf, boolean append) {
		setInput(buf, 0, buf.length, append);
	}

	public void setInput(byte[] buf, int off, int len, boolean append) {
		if (len <= 0 && append && next_in != null)
			return;

		if (avail_in > 0 && append) {
			byte[] tmp = new byte[avail_in + len];
			System.arraycopy(next_in, next_in_index, tmp, 0, avail_in);
			System.arraycopy(buf, off, tmp, avail_in, len);
			next_in = tmp;
			next_in_index = 0;
			avail_in += len;
		} else {
			next_in = buf;
			next_in_index = off;
			avail_in = len;
		}
	}

	public byte[] getNextIn() {
		return next_in;
	}

	public void setNextIn(byte[] next_in) {
		this.next_in = next_in;
	}

	public int getNextInIndex() {
		return next_in_index;
	}

	public void setNextInIndex(int next_in_index) {
		this.next_in_index = next_in_index;
	}

	public int getAvailIn() {
		return avail_in;
	}

	public void setAvailIn(int avail_in) {
		this.avail_in = avail_in;
	}

	public byte[] getNextOut() {
		return next_out;
	}

	public void setNextOut(byte[] next_out) {
		this.next_out = next_out;
	}

	public int getNextOutIndex() {
		return next_out_index;
	}

	public void setNextOutIndex(int next_out_index) {
		this.next_out_index = next_out_index;
	}

	public int getAvailOut() {
		return avail_out;

	}

	public void setAvailOut(int avail_out) {
		this.avail_out = avail_out;
	}

	public long getTotalOut() {
		return total_out;
	}

	public long getTotalIn() {
		return total_in;
	}

	public String getMessage() {
		return msg;
	}

	/**
	 * Those methods are expected to be override by Inflater and Deflater. In
	 * the future, they will become abstract methods.
	 */
	public int end() {
		return Z_OK;
	}

	public boolean finished() {
		return false;
	}
}
