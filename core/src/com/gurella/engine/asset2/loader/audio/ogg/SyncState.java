package com.gurella.engine.asset2.loader.audio.ogg;

import com.badlogic.gdx.utils.Pool.Poolable;

public class SyncState implements Poolable {
	public byte[] data;
	private int storage;
	private int fill;
	private int returned;

	private int headerbytes;
	private int bodybytes;

	private final Page pageseek = new Page();
	private final byte[] chksum = new byte[4];

	public int buffer(int size) {
		// first, clear out any space that has been previously returned
		if (returned != 0) {
			fill -= returned;
			if (fill > 0) {
				System.arraycopy(data, returned, data, 0, fill);
			}
			returned = 0;
		}

		if (size > storage - fill) {
			// We need to extend the internal buffer
			int newsize = size + fill + 4096; // an extra page to be nice
			if (data != null) {
				byte[] foo = new byte[newsize];
				System.arraycopy(data, 0, foo, 0, data.length);
				data = foo;
			} else {
				data = new byte[newsize];
			}
			storage = newsize;
		}

		return fill;
	}

	public int wrote(int bytes) {
		if (fill + bytes > storage) {
			return -1;
		}
		fill += bytes;
		return 0;
	}

	// sync the stream. This is meant to be useful for finding page
	// boundaries.
	//
	// return values for this:
	// -n) skipped n bytes
	// 0) page not ready; more data (no bytes skipped)
	// n) page synced at current location; page length n bytes
	public int pageseek(Page og) {
		int page = returned;
		int next;
		int bytes = fill - returned;

		if (headerbytes == 0) {
			int _headerbytes, i;
			if (bytes < 27) {
				return 0; // not enough for a header
			}

			/* verify capture pattern */
			if (data[page] != 'O' || data[page + 1] != 'g' || data[page + 2] != 'g' || data[page + 3] != 'S') {
				headerbytes = 0;
				bodybytes = 0;

				// search for possible capture
				next = 0;
				for (int ii = 0; ii < bytes - 1; ii++) {
					if (data[page + 1 + ii] == 'O') {
						next = page + 1 + ii;
						break;
					}
				}
				// next=memchr(page+1,'O',bytes-1);
				if (next == 0)
					next = fill;

				returned = next;
				return (-(next - page));
			}
			_headerbytes = (data[page + 26] & 0xff) + 27;
			if (bytes < _headerbytes) {
				return 0; // not enough for header + seg table
			}

			// count up body length in the segment table

			for (i = 0; i < (data[page + 26] & 0xff); i++) {
				bodybytes += (data[page + 27 + i] & 0xff);
			}
			headerbytes = _headerbytes;
		}

		if (bodybytes + headerbytes > bytes) {
			return 0;
		}

		// The whole test page is buffered. Verify the checksum
		synchronized (chksum) {
			// Grab the checksum bytes, set the header field to zero

			System.arraycopy(data, page + 22, chksum, 0, 4);
			data[page + 22] = 0;
			data[page + 23] = 0;
			data[page + 24] = 0;
			data[page + 25] = 0;

			// set up a temp page struct and recompute the checksum
			pageseek.header_base = data;
			pageseek.header = page;
			pageseek.header_len = headerbytes;

			pageseek.body_base = data;
			pageseek.body = page + headerbytes;
			pageseek.body_len = bodybytes;
			pageseek.checksum();
			pageseek.reset();

			// Compare
			if (chksum[0] != data[page + 22] || chksum[1] != data[page + 23] || chksum[2] != data[page + 24] || chksum[3] != data[page + 25]) {
				// D'oh. Mismatch! Corrupt page (or miscapture and not a page at all)
				// replace the computed checksum with the one actually read in
				System.arraycopy(chksum, 0, data, page + 22, 4);
				// Bad checksum. Lose sync */

				headerbytes = 0;
				bodybytes = 0;
				// search for possible capture
				next = 0;
				for (int ii = 0; ii < bytes - 1; ii++) {
					if (data[page + 1 + ii] == 'O') {
						next = page + 1 + ii;
						break;
					}
				}
				
				if (next == 0) {
					next = fill;
				}
				returned = next;
				return (-(next - page));
			}
		}

		// yes, have a whole page all ready to go
		page = returned;

		if (og != null) {
			og.header_base = data;
			og.header = page;
			og.header_len = headerbytes;
			og.body_base = data;
			og.body = page + headerbytes;
			og.body_len = bodybytes;
		}

		returned += (bytes = headerbytes + bodybytes);
		headerbytes = 0;
		bodybytes = 0;
		return bytes;
	}

	// clear things to an initial state. Good to call, eg, before seeking
	public void resetState() {
		fill = 0;
		returned = 0;
		headerbytes = 0;
		bodybytes = 0;
	}

	@Override
	public void reset() {
		fill = 0;
		returned = 0;
		headerbytes = 0;
		bodybytes = 0;
	}
}
