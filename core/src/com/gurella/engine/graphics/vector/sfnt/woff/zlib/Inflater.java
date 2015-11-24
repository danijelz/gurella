package com.gurella.engine.graphics.vector.sfnt.woff.zlib;

//https://github.com/ymnk/jzlib
public class Inflater extends ZStream {
	static final public int MAX_WBITS = 15; // 32K LZ77 window
	static final public int DEF_WBITS = MAX_WBITS;

	static final public int Z_NO_FLUSH = 0;
	static final public int Z_PARTIAL_FLUSH = 1;
	static final public int Z_SYNC_FLUSH = 2;
	static final public int Z_FULL_FLUSH = 3;
	static final public int Z_FINISH = 4;
	
	static final public int Z_OK = 0;
	static final public int Z_STREAM_END = 1;
	static final public int Z_NEED_DICT = 2;
	static final public int Z_ERRNO = -1;
	static final public int Z_STREAM_ERROR = -2;
	static final public int Z_DATA_ERROR = -3;
	static final public int Z_MEM_ERROR = -4;
	static final public int Z_BUF_ERROR = -5;
	static final public int Z_VERSION_ERROR = -6;

	public Inflater() {
		super();
		init();
	}

	public Inflater(int w) {
		this(w, false);
	}

	public Inflater(boolean nowrap) {
		this(DEF_WBITS, nowrap);
	}

	public Inflater(int w, boolean nowrap) {
		super();
		int ret = init(w, nowrap);
		if (ret != Z_OK)
			throw new IllegalArgumentException(ret + ": " + msg);
	}

	public int init() {
		return init(DEF_WBITS);
	}

	public int init(boolean nowrap) {
		return init(DEF_WBITS, nowrap);
	}

	public int init(int w) {
		return init(w, false);
	}

	public int init(int w, boolean nowrap) {
		istate = new Inflate(this);
		return istate.inflateInit(nowrap ? -w : w);
	}

	@Override
	public int inflate(int f) {
		if (istate == null)
			return Z_STREAM_ERROR;
		int ret = istate.inflate(f);
		return ret;
	}

	@Override
	public int end() {
		if (istate == null)
			return Z_STREAM_ERROR;
		int ret = istate.inflateEnd();
		return ret;
	}

	public int sync() {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSync();
	}

	public int syncPoint() {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSyncPoint();
	}

	public int setDictionary(byte[] dictionary, int dictLength) {
		if (istate == null)
			return Z_STREAM_ERROR;
		return istate.inflateSetDictionary(dictionary, dictLength);
	}

	@Override
	public boolean finished() {
		return istate.mode == Inflate.DONE;
	}
}
