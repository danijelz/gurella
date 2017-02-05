package com.gurella.engine.asset2.loader.audio.mp3;

@SuppressWarnings("serial")
public class BitstreamException extends Exception {
	/**
	 * A problem occurred reading from the stream.
	 */
	static public final int STREAM_ERROR = 0x100;
	
	/**
	 * The end of the stream was reached prematurely. 
	 */
	static public final int STREAM_EOF = STREAM_ERROR + 1;

	/**
	 * Frame data are missing.
	 */
	static public final int INVALIDFRAME = STREAM_ERROR + 2;
	
	private int errorcode = 0;

	public BitstreamException(String msg, Throwable t) {
		super(msg, t);
	}

	public BitstreamException(int errorcode, Throwable t) {
		this(getErrorString(errorcode), t);
		this.errorcode = errorcode;
	}

	public int getErrorCode() {
		return errorcode;
	}

	static public String getErrorString(int errorcode) {
		return "Bitstream errorcode " + Integer.toHexString(errorcode);
	}
}
