package com.gurella.engine.asset.loader.audio.mp3;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.asset.loader.audio.PushBackArrayInputStream;

// https://github.com/cosenary/Battleship/blob/master/javazoom/jl/decoder/Bitstream.java
public class Mp3File implements Poolable {
	/**
	 * Synchronization control constant for the initial synchronization to the start of a frame.
	 */
	static final byte INITIAL_SYNC = 0;

	/**
	 * Synchronization control constant for non-initial frame synchronizations.
	 */
	static final byte STRICT_SYNC = 1;

	// max. 1730 bytes per frame: 144 * 384kbit/s / 32000 Hz + 2 Bytes CRC
	/**
	 * Maximum size of the frame buffer.
	 */
	private static final int BUFFER_INT_SIZE = 433;

	/**
	 * Number of valid bytes in the frame buffer.
	 */
	private int framesize;

	/**
	 * The bytes read from the stream.
	 */
	private final byte[] frame_bytes = new byte[BUFFER_INT_SIZE * 4];

	/**
	 * Number (0-31, from MSB to LSB) of next bit for get_bits()
	 */
	private int bitindex;
	private int syncword;
	private int fileLength;
	private boolean single_ch_mode;

	private final PushBackArrayInputStream source = new PushBackArrayInputStream();

	private final Mp3Header header = new Mp3Header();
	private final byte syncbuf[] = new byte[4];
	private boolean firstframe = true;

	public static float totalDuration(FileHandle file) {
		Mp3File bitstream = Pools.obtain(Mp3File.class);
		try {
			bitstream.init(file);
			return bitstream.totalDuration();
		} catch (BitstreamException e) {
			throw new GdxRuntimeException(e);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} finally {
			Pools.free(bitstream);
		}
	}

	Mp3File() {
	}

	/**
	 * Construct a IBitstream that reads data from a given InputStream.
	 *
	 * @param in
	 *            The InputStream to read from.
	 * @throws IOException
	 */
	public void init(FileHandle file) throws IOException {
		fileLength = (int) file.length();
		source.init(file.read());
		loadID3v2();
		// source.skip(BUFFER_INT_SIZE * 4);
		firstframe = true;
		closeFrame();
	}

	private void loadID3v2() {
		int size = -1;
		try {
			// Read ID3v2 header (10 bytes).
			source.mark(10);
			size = readID3v2Header();
		} catch (IOException e) {
		} finally {
			try {
				// Unread ID3v2 header (10 bytes).
				source.reset();
			} catch (IOException e) {
			}
		}

		try {
			if (size > 0) {
				source.skip(size);
			}
		} catch (IOException e) {
		}
	}

	private int readID3v2Header() throws IOException {
		int size = -10;
		source.read(frame_bytes, 0, 3);
		// Look for ID3v2
		if (frame_bytes[0] == 'I' && frame_bytes[1] == 'D' && frame_bytes[2] == '3') {
			source.read(frame_bytes, 0, 3);
			source.read(frame_bytes, 0, 4);
			size = (frame_bytes[0] << 21) + (frame_bytes[1] << 14) + (frame_bytes[2] << 7) + frame_bytes[3];
		}
		return size + 10;
	}

	/**
	 * Reads and parses the next frame from the input source.
	 * 
	 * @return the Header describing details of the frame read, or null if the end of the stream has been reached.
	 */
	public Mp3Header readFrame() throws BitstreamException {
		Mp3Header result = null;
		try {
			result = readNextFrame();
			// E.B, Parse VBR (if any) first frame.
			if (firstframe == true) {
				result.parseVBR(frame_bytes);
				firstframe = false;
			}
		} catch (BitstreamException ex) {
			if ((ex.getErrorCode() == BitstreamException.INVALIDFRAME)) {
				// Try to skip this frame.
				try {
					closeFrame();
					result = readNextFrame();
				} catch (BitstreamException e) {
					if ((e.getErrorCode() != BitstreamException.STREAM_EOF)) {
						// wrap original exception so stack trace is maintained.
						throw newBitstreamException(e.getErrorCode(), e);
					}
				}
			} else if ((ex.getErrorCode() != BitstreamException.STREAM_EOF)) {
				// wrap original exception so stack trace is maintained.
				throw newBitstreamException(ex.getErrorCode(), ex);
			} else {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Read next MP3 frame.
	 * 
	 * @return MP3 frame header.
	 * @throws BitstreamException
	 */
	private Mp3Header readNextFrame() throws BitstreamException {
		if (framesize == -1) {
			header.read_header(this);
		}
		return header;
	}

	/**
	 * Unreads the bytes read from the frame.
	 */
	void unreadFrame() throws BitstreamException {
		if (bitindex == -1 && (framesize > 0)) {
			try {
				source.unread(framesize);
			} catch (IOException ex) {
				throw newBitstreamException(BitstreamException.STREAM_ERROR);
			}
		}
	}

	/**
	 * Close MP3 frame.
	 */
	public void closeFrame() {
		framesize = -1;
		bitindex = -1;
	}

	/**
	 * Determines if the next 4 bytes of the stream represent a frame header.
	 */
	public boolean isSyncCurrentPosition(int syncmode) throws BitstreamException {
		int read = readSyncBuf(0, 4);
		int headerstring = ((syncbuf[0] << 24) & 0xFF000000) | ((syncbuf[1] << 16) & 0x00FF0000)
				| ((syncbuf[2] << 8) & 0x0000FF00) | ((syncbuf[3] << 0) & 0x000000FF);

		try {
			source.unread(read);
		} catch (IOException ex) {
		}

		switch (read) {
		case 0:
			return true;
		case 4:
			return isSyncMark(headerstring, syncmode);
		default:
			return false;
		}
	}

	protected BitstreamException newBitstreamException(int errorcode) {
		return new BitstreamException(errorcode, null);
	}

	protected BitstreamException newBitstreamException(int errorcode, Throwable throwable) {
		return new BitstreamException(errorcode, throwable);
	}

	/**
	 * Get next 32 bits from bitstream. They are stored in the headerstring. syncmod allows Synchro flag ID The returned
	 * value is False at the end of stream.
	 */
	int syncHeader(byte syncmode) throws BitstreamException {
		if (readSyncBuf(0, 3) != 3) {
			throw newBitstreamException(BitstreamException.STREAM_EOF, null);
		}

		int headerstring = ((syncbuf[0] << 16) & 0x00FF0000) | ((syncbuf[1] << 8) & 0x0000FF00)
				| ((syncbuf[2] << 0) & 0x000000FF);

		do {
			headerstring <<= 8;
			headerstring |= readAndCheckForEof();

		} while (!isSyncMark(headerstring, syncmode));

		return headerstring;
	}

	private byte readAndCheckForEof() throws BitstreamException {
		try {
			int result = source.read();
			if (result < 0) {
				throw newBitstreamException(BitstreamException.STREAM_EOF, null);
			}
			return (byte) result;

		} catch (IOException e) {
			throw newBitstreamException(BitstreamException.STREAM_ERROR, null);
		}
	}

	public boolean isSyncMark(int headerstring, int syncmode) {
		if (!checkSyncMarkByMode(headerstring, syncmode)) {
			return false;
		}

		// filter out invalid sample rate
		if (((headerstring >>> 10) & 3) == 3) {
			return false;
		}

		// filter out invalid layer
		if (((headerstring >>> 17) & 3) == 0) {
			return false;
		}

		// filter out invalid version
		return (((headerstring >>> 19) & 3) != 1);
	}

	protected boolean checkSyncMarkByMode(int headerstring, int syncmode) {
		return syncmode == INITIAL_SYNC ? ((headerstring & 0xFFE00000) == 0xFFE00000)
				: ((headerstring & 0xFFF80C00) == syncword)
						&& (((headerstring & 0x000000C0) == 0x000000C0) == single_ch_mode);
	}

	/**
	 * Reads the data for the next frame. The frame is not parsed until parse frame is called.
	 */
	int read_frame_data(int bytesize) throws BitstreamException {
		int numread = readFrameBytes(bytesize);
		framesize = bytesize;
		bitindex = -1;
		return numread;
	}

	/**
	 * Parses the data previously read with read_frame_data().
	 */
	void parse_frame() {
		bitindex = 0;
	}

	/**
	 * Set the word we want to sync the header to. In Big-Endian byte order
	 */
	void set_syncword(int syncword0) {
		syncword = syncword0 & 0xFFFFFF3F;
		single_ch_mode = ((syncword0 & 0x000000C0) == 0x000000C0);
	}

	/**
	 * Reads the exact number of bytes from the source input stream into a byte array.
	 */
	private int readFrameBytes(int len) throws BitstreamException {
		try {
			int bytesread = source.read(frame_bytes, 0, len);
			if (bytesread < len) {
				int readOffset = bytesread < 0 ? 0 : bytesread;
				int readlen = bytesread < 0 ? len : len - bytesread;
				while (readlen-- > 0) {
					frame_bytes[readOffset++] = 0;
				}
			}
			return bytesread;
		} catch (IOException ex) {
			throw newBitstreamException(BitstreamException.STREAM_ERROR, ex);
		}
	}

	/**
	 * Similar to readFully, but doesn't throw exception when EOF is reached.
	 */
	private int readSyncBuf(int offs, int len) throws BitstreamException {
		try {
			int bytesread = source.read(syncbuf, offs, len);
			return bytesread < 0 ? 0 : bytesread;
		} catch (IOException ex) {
			throw newBitstreamException(BitstreamException.STREAM_ERROR, ex);
		}
	}

	public float totalDuration() throws BitstreamException {
		return readFrame().totalDuration(fileLength);
	}

	@Override
	public void reset() {
		framesize = 0;
		bitindex = 0;
		syncword = 0;
		single_ch_mode = false;
		firstframe = true;
		source.poolableReset();
		header.reset();
	}
}
