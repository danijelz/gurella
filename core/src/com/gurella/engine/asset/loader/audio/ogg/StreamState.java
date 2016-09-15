package com.gurella.engine.asset.loader.audio.ogg;

import com.badlogic.gdx.utils.Pool.Poolable;

public class StreamState implements Poolable {
	private byte[] body_data; /* bytes from packet bodies */
	private int body_storage; /* storage elements allocated */
	private int body_fill; /* elements stored; fill mark */
	private int body_returned; /* elements of fill returned */

	/* The values that will go to the segment table */
	private int[] lacing_vals;
	/*
	 * pcm_pos values for headers. Not compact this way, but it is simple coupled to the lacing fifo
	 */
	private long[] granule_vals;
	private int lacing_storage;
	private int lacing_fill;
	private int lacing_packet;
	private int lacing_returned;

	/*
	 * set when we have buffered the last packet in the logical bitstream
	 */
	private int serialno;
	private int pageno;
	private long packetno;

	public StreamState() {
		body_storage = 16 * 1024;
		body_data = new byte[body_storage];
		lacing_storage = 1024;
		lacing_vals = new int[lacing_storage];
		granule_vals = new long[lacing_storage];
	}

	public void init(int serialno) {
		for (int i = 0; i < body_data.length; i++) {
			body_data[i] = 0;
		}
		for (int i = 0; i < lacing_vals.length; i++) {
			lacing_vals[i] = 0;
		}
		for (int i = 0; i < granule_vals.length; i++) {
			granule_vals[i] = 0;
		}
		this.serialno = serialno;
	}

	private void body_expand(int needed) {
		if (body_storage <= body_fill + needed) {
			body_storage += (needed + 1024);
			byte[] foo = new byte[body_storage];
			System.arraycopy(body_data, 0, foo, 0, body_data.length);
			body_data = foo;
		}
	}

	private void lacing_expand(int needed) {
		if (lacing_storage <= lacing_fill + needed) {
			lacing_storage += (needed + 32);
			int[] foo = new int[lacing_storage];
			System.arraycopy(lacing_vals, 0, foo, 0, lacing_vals.length);
			lacing_vals = foo;

			long[] bar = new long[lacing_storage];
			System.arraycopy(granule_vals, 0, bar, 0, granule_vals.length);
			granule_vals = bar;
		}
	}

	public int packetout(Packet op) {
		/*
		 * The last part of decode. We have the stream broken into packet segments. Now we need to group them into
		 * packets (or return the out of sync markers)
		 */
		int ptr = lacing_returned;

		if (lacing_packet <= ptr) {
			return (0);
		}

		if ((lacing_vals[ptr] & 0x400) != 0) {
			/* We lost sync here; let the app know */
			lacing_returned++;

			/*
			 * we need to tell the codec there's a gap; it might need to handle previous packet dependencies.
			 */
			packetno++;
			return (-1);
		}

		/* Gather the whole packet. We'll have no holes or a partial packet */
		{
			int size = lacing_vals[ptr] & 0xff;
			int bytes = 0;

			op.packet_base = body_data;
			op.packet = body_returned;
			op.e_o_s = lacing_vals[ptr] & 0x200; /* last packet of the stream? */
			op.b_o_s = lacing_vals[ptr] & 0x100; /* first packet of the stream? */
			bytes += size;

			while (size == 255) {
				int val = lacing_vals[++ptr];
				size = val & 0xff;
				if ((val & 0x200) != 0)
					op.e_o_s = 0x200;
				bytes += size;
			}

			op.packetno = packetno;
			op.granulepos = granule_vals[ptr];
			op.bytes = bytes;

			body_returned += bytes;

			lacing_returned = ptr + 1;
		}
		packetno++;
		return (1);
	}

	// add the incoming page to the stream state; we decompose the page
	// into packet segments here as well.
	public int pagein(Page og) {
		byte[] header_base = og.header_base;
		int header = og.header;
		byte[] body_base = og.body_base;
		int body = og.body;
		int bodysize = og.body_len;
		int segptr = 0;

		int version = og.version();
		int continued = og.continued();
		int bos = og.bos();
		int eos = og.eos();
		long granulepos = og.granulepos();
		int _serialno = og.serialno();
		int _pageno = og.pageno();
		int segments = header_base[header + 26] & 0xff;

		// clean up 'returned data'
		{
			int lr = lacing_returned;
			int br = body_returned;

			// body data
			if (br != 0) {
				body_fill -= br;
				if (body_fill != 0) {
					System.arraycopy(body_data, br, body_data, 0, body_fill);
				}
				body_returned = 0;
			}

			if (lr != 0) {
				// segment table
				if ((lacing_fill - lr) != 0) {
					System.arraycopy(lacing_vals, lr, lacing_vals, 0, lacing_fill - lr);
					System.arraycopy(granule_vals, lr, granule_vals, 0, lacing_fill - lr);
				}
				lacing_fill -= lr;
				lacing_packet -= lr;
				lacing_returned = 0;
			}
		}

		// check the serial number
		if (_serialno != serialno)
			return (-1);
		if (version > 0)
			return (-1);

		lacing_expand(segments + 1);

		// are we in sequence?
		if (_pageno != pageno) {
			int i;

			// unroll previous partial packet (if any)
			for (i = lacing_packet; i < lacing_fill; i++) {
				body_fill -= lacing_vals[i] & 0xff;
				// System.out.println("??");
			}
			lacing_fill = lacing_packet;

			// make a note of dropped data in segment table
			if (pageno != -1) {
				lacing_vals[lacing_fill++] = 0x400;
				lacing_packet++;
			}

			// are we a 'continued packet' page? If so, we'll need to skip
			// some segments
			if (continued != 0) {
				bos = 0;
				for (; segptr < segments; segptr++) {
					int val = (header_base[header + 27 + segptr] & 0xff);
					body += val;
					bodysize -= val;
					if (val < 255) {
						segptr++;
						break;
					}
				}
			}
		}

		if (bodysize != 0) {
			body_expand(bodysize);
			System.arraycopy(body_base, body, body_data, body_fill, bodysize);
			body_fill += bodysize;
		}

		{
			int saved = -1;
			while (segptr < segments) {
				int val = (header_base[header + 27 + segptr] & 0xff);
				lacing_vals[lacing_fill] = val;
				granule_vals[lacing_fill] = -1;

				if (bos != 0) {
					lacing_vals[lacing_fill] |= 0x100;
					bos = 0;
				}

				if (val < 255) {
					saved = lacing_fill;
				}

				lacing_fill++;
				segptr++;

				if (val < 255) {
					lacing_packet = lacing_fill;
				}
			}

			/* set the granulepos on the last pcmval of the last full packet */
			if (saved != -1) {
				granule_vals[saved] = granulepos;
			}
		}

		if (eos != 0 && lacing_fill > 0) {
			lacing_vals[lacing_fill - 1] |= 0x200;
		}

		pageno = _pageno + 1;
		return 0;
	}

	public void clear() {
		body_data = null;
		lacing_vals = null;
		granule_vals = null;
	}

	@Override
	public void reset() {
		body_fill = 0;
		body_returned = 0;

		lacing_fill = 0;
		lacing_packet = 0;
		lacing_returned = 0;

		pageno = -1;
		packetno = 0;
	}
}
