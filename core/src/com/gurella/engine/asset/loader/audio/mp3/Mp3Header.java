package com.gurella.engine.asset.loader.audio.mp3;

import java.util.Arrays;

public class Mp3Header {
	private static final byte[] xing = new byte[] { 'X', 'i', 'n', 'g' };
	private static final byte[] vbri = new byte[] { 'V', 'B', 'R', 'I' };
	private static final double[] h_vbr_time_per_frame = { -1, 384, 1152, 1152 };
	private static final int[][] frequencies = { { 22050, 24000, 16000, 1 }, { 44100, 48000, 32000, 1 }, { 11025, 12000, 8000, 1 } }; // SZD: MPEG25
	private static final float ms_per_frame_array[][] = { { 8.707483f, 8.0f, 12.0f }, { 26.12245f, 24.0f, 36.0f }, { 26.12245f, 24.0f, 36.0f } };
	public static final int bitrates[][][] = {
			{ { 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000, 0 },
					{ 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 },
					{ 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 } },

			{ { 0, 32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000, 0 },
					{ 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000, 0 },
					{ 0, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 0 } },
			{ { 0, 32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000, 0 },
					{ 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 },
					{ 0, 8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0 } },

	};

	private static final int MPEG2_LSF = 0;
	private static final int MPEG25_LSF = 2;
	private static final int MPEG1 = 1;
	private static final int SINGLE_CHANNEL = 3;

	private int h_layer, h_bitrate_index, h_padding_bit;
	private int h_version;
	private int h_mode;
	private int h_sample_frequency;
	private boolean h_vbr;
	private int h_vbr_frames;
	private int h_vbr_bytes;
	private int framesize;
	private byte syncmode = Mp3File.INITIAL_SYNC;

	private byte tmp[] = new byte[4];

	/**
	 * Read a 32-bit header from the bitstream.
	 */
	void read_header(Mp3File stream) throws BitstreamException {
		int headerstring;
		boolean sync = false;
		do {
			headerstring = stream.syncHeader(syncmode);
			if (syncmode == Mp3File.INITIAL_SYNC) {
				h_version = ((headerstring >>> 19) & 1);
				if (((headerstring >>> 20) & 1) == 0) // SZD: MPEG2.5 detection
					if (h_version == MPEG2_LSF)
						h_version = MPEG25_LSF;
					else
						throw stream.newBitstreamException(0);
				if ((h_sample_frequency = ((headerstring >>> 10) & 3)) == 3) {
					throw stream.newBitstreamException(0);
				}
			}
			h_layer = 4 - (headerstring >>> 17) & 3;
			h_bitrate_index = (headerstring >>> 12) & 0xF;
			h_padding_bit = (headerstring >>> 9) & 1;
			h_mode = ((headerstring >>> 6) & 3);
			calculate_framesize();
			int framesizeloaded = stream.read_frame_data(framesize);
			if ((framesize >= 0) && (framesizeloaded != framesize)) {
				// Data loaded does not match to expected framesize,
				// it might be an ID3v1 TAG. (Fix 11/17/04).
				throw stream.newBitstreamException(BitstreamException.INVALIDFRAME);
			}
			if (stream.isSyncCurrentPosition(syncmode)) {
				if (syncmode == Mp3File.INITIAL_SYNC) {
					syncmode = Mp3File.STRICT_SYNC;
					stream.set_syncword(headerstring & 0xFFF80CC0);
				}
				sync = true;
			} else {
				stream.unreadFrame();
			}
		} while (!sync);
		stream.parse_frame();
	}

	/**
	 * Parse frame to extract optionnal VBR frame.
	 * 
	 * @param firstframe
	 * @author E.B (javalayer@javazoom.net)
	 */
	void parseVBR(byte[] firstframe) throws BitstreamException {
		Arrays.fill(tmp, (byte)0);
		// Trying Xing header.
		int offset = 0;
		// Compute "Xing" offset depending on MPEG version and channels.
		if (h_version == MPEG1) {
			if (h_mode == SINGLE_CHANNEL)
				offset = 21 - 4;
			else
				offset = 36 - 4;
		} else {
			if (h_mode == SINGLE_CHANNEL)
				offset = 13 - 4;
			else
				offset = 21 - 4;
		}
		try {
			System.arraycopy(firstframe, offset, tmp, 0, 4);
			// Is "Xing" ?
			if (Arrays.equals(xing, tmp)) {
				h_vbr = true;
				h_vbr_frames = -1;
				h_vbr_bytes = -1;
				int length = 4;
				byte flag = firstframe[offset + length + 3];
				length += 4;
				// Read number of frames (if available).
				if ((flag & (byte) (1 << 0)) != 0) {
					System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
					h_vbr_frames = (tmp[0] << 24) & 0xFF000000 | (tmp[1] << 16) & 0x00FF0000 | (tmp[2] << 8) & 0x0000FF00 | tmp[3] & 0x000000FF;
					length += 4;
				}
				// Read size (if available).
				if ((flag & (byte) (1 << 1)) != 0) {
					System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
					h_vbr_bytes = (tmp[0] << 24) & 0xFF000000 | (tmp[1] << 16) & 0x00FF0000 | (tmp[2] << 8) & 0x0000FF00 | tmp[3] & 0x000000FF;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new BitstreamException("XingVBRHeader Corrupted", e);
		}

		offset = 36 - 4;
		try {
			System.arraycopy(firstframe, offset, tmp, 0, 4);
			// Is "VBRI" ?
			if (Arrays.equals(vbri, tmp)) {
				h_vbr = true;
				h_vbr_frames = -1;
				h_vbr_bytes = -1;
				int length = 4 + 6;
				System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
				h_vbr_bytes = (tmp[0] << 24) & 0xFF000000 | (tmp[1] << 16) & 0x00FF0000 | (tmp[2] << 8) & 0x0000FF00 | tmp[3] & 0x000000FF;
				length += 4;
				System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
				h_vbr_frames = (tmp[0] << 24) & 0xFF000000 | (tmp[1] << 16) & 0x00FF0000 | (tmp[2] << 8) & 0x0000FF00 | tmp[3] & 0x000000FF;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new BitstreamException("VBRIVBRHeader Corrupted", e);
		}
	}

	/**
	 * Returns Layer ID.
	 */
	public int layer() {
		return h_layer;
	}

	/**
	 * Returns Frequency.
	 */
	public int frequency() {
		return frequencies[h_version][h_sample_frequency];
	}

	/**
	 * Calculate Frame size. Calculates framesize in bytes excluding header
	 * size.
	 */
	private int calculate_framesize() {
		if (h_layer == 1) {
			framesize = (12 * bitrates[h_version][0][h_bitrate_index]) / frequencies[h_version][h_sample_frequency];
			if (h_padding_bit != 0)
				framesize++;
			framesize <<= 2; // one slot is 4 bytes long
		} else {
			framesize = (144 * bitrates[h_version][h_layer - 1][h_bitrate_index]) / frequencies[h_version][h_sample_frequency];
			if (h_version == MPEG2_LSF || h_version == MPEG25_LSF)
				framesize >>= 1; // SZD
			if (h_padding_bit != 0)
				framesize++;
		}
		framesize -= 4; // subtract header size
		return framesize;
	}

	/**
	 * Returns the maximum number of frames in the stream.
	 * 
	 * @param streamsize
	 * @return number of frames
	 */
	private long max_number_of_frames(long streamsize) {
		if (h_vbr == true) {
			return h_vbr_frames;
		} else {
			if ((framesize + 4 - h_padding_bit) == 0) {
				return 0;
			} else {
				return (streamsize / (framesize + 4 - h_padding_bit));
			}
		}
	}

	/**
	 * Returns the maximum number of frames in the stream.
	 * 
	 * @param streamsize
	 * @return number of frames
	 */
	public int min_number_of_frames(int streamsize) {
		if (h_vbr == true) {
			return h_vbr_frames;
		} else {
			if ((framesize + 5 - h_padding_bit) == 0) {
				return 0;
			} else {
				return (streamsize / (framesize + 5 - h_padding_bit));
			}
		}
	}

	/**
	 * Returns ms/frame.
	 * 
	 * @return milliseconds per frame
	 */
	public float ms_per_frame() {
		if (h_vbr == true) {
			double tpf = h_vbr_time_per_frame[layer()] / frequency();
			if ((h_version == MPEG2_LSF) || (h_version == MPEG25_LSF))
				tpf /= 2;
			return ((float) (tpf * 1000));
		} else {
			return (ms_per_frame_array[h_layer - 1][h_sample_frequency]);
		}
	}

	/**
	 * Returns total ms.
	 * 
	 * @param streamsize
	 * @return total seconds
	 */
	public float totalDuration(long streamsize) {
		return (max_number_of_frames(streamsize) * ms_per_frame()) / 1000;
	}

	/**
	 * Return Bitrate.
	 * 
	 * @return bitrate in bps and average bitrate for VBR header
	 */
	public int bitrate() {
		if (h_vbr == true) {
			return ((int) ((h_vbr_bytes * 8) / (ms_per_frame() * h_vbr_frames))) * 1000;
		} else {
			return bitrates[h_version][h_layer - 1][h_bitrate_index];
		}
	}

	/**
	 * Return Instant Bitrate. Bitrate for VBR is not constant.
	 * 
	 * @return bitrate in bps
	 */
	public int bitrate_instant() {
		return bitrates[h_version][h_layer - 1][h_bitrate_index];
	}
}
