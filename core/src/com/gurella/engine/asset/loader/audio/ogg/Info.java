package com.gurella.engine.asset.loader.audio.ogg;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class Info implements Poolable {
	private int version;
	private int channels;
	int rate;

	// Vorbis supports only short and long blocks, but allows the
	// encoder to choose the sizes

	private final int[] blocksizes = new int[2];
	private final Buffer opb = new Buffer();

	public static Info obtain() {
		return Pools.obtain(Info.class);
	}

	private Info() {
	}

	// Header packing/unpacking
	private int unpack_info() {
		version = opb.read(32);
		if (version != 0) {
			return -1;
		}

		channels = opb.read(8);
		rate = opb.read(32);

		/*bitrate_upper =*/ opb.read(32);
		/*bitrate_nominal =*/ opb.read(32);
		/*bitrate_lower =*/ opb.read(32);

		blocksizes[0] = 1 << opb.read(4);
		blocksizes[1] = 1 << opb.read(4);

		if ((rate < 1) || (channels < 1) || (blocksizes[0] < 8) || (blocksizes[1] < blocksizes[0]) || (opb.read(1) != 1)) {
			return -1;
		}

		return (0);
	}

	// The Vorbis header is in three packets; the initial small packet in
	// the first page that identifies basic parameters, a second packet
	// with bitstream comments and a third packet that holds the
	// codebook.
	public int synthesis_headerin(Packet op) {
		if (op != null) {
			opb.init(op.packet_base, op.packet, op.bytes);

			// Which of the three types of header is this?
			// Also verify header-ness, vorbis
			byte[] buffer = new byte[6];
			int packtype = opb.read(8);
			opb.read(buffer, 6);
			if (buffer[0] != 'v' || buffer[1] != 'o' || buffer[2] != 'r' || buffer[3] != 'b' || buffer[4] != 'i' || buffer[5] != 's') {
				// not a vorbis header
				return (-1);
			}
			switch (packtype) {
			case 0x01: // least significant *bit* is read first
				if (op.b_o_s == 0) {
					// Not the initial packet
					return (-1);
				}
				if (rate != 0) {
					// previously initialized info header
					return (-1);
				}
				return (unpack_info());
			case 0x03: // least significant *bit* is read first
				if (rate == 0) {
					// um... we didn't get the initial header
					return (-1);
				}
				return 0;
			case 0x05: // least significant *bit* is read first
				if (rate == 0) {
					// um... we didn;t get the initial header or comments yet
					return (-1);
				}
				return 0;
			default:
				// Not a valid vorbis header type
				// return(-1);
				break;
			}
		}

		return -1;
	}

	public void free() {
		Pools.free(this);
	}

	@Override
	public void reset() {
		version = 0;
		channels = 0;
		rate = 0;
	}
}
