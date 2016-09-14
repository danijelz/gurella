package com.gurella.engine.asset.loader.audio.ogg;

import java.math.BigDecimal;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.asset.loader.audio.PushBackArrayInputStream;

// https://github.com/MWisBest/JOrbis/blob/master/src/com/jcraft/jorbis/VorbisFile.java
public class VorbisFile implements Poolable {
	private static final int CHUNKSIZE = 8500;

	private static final int OV_FALSE = -1;
	private static final int OV_EOF = -2;

	private static final int OV_EREAD = -128;
	private static final int OV_EFAULT = -129;
	private static final int OV_ENOTVORBIS = -132;

	private final PushBackArrayInputStream datasource = new PushBackArrayInputStream();
	private final StreamState os = new StreamState();
	private final Packet op = new Packet();
	private final SyncState oy = new SyncState();

	private long fileLength;

	private int links;
	private final LongArray offsets = new LongArray();
	private final LongArray pcmlengths = new LongArray();
	private final Array<Info> vi = new Array<Info>();

	public static void main(String[] args) {
		float time_total = totalDuration(new FileHandle("/home/danijel/Music/orc_pain.ogg"));
		BigDecimal decimal = new BigDecimal(time_total);
		System.out.println(decimal.toPlainString());
	}

	public static float totalDuration(FileHandle file) {
		VorbisFile vorbisFile = Pools.obtain(VorbisFile.class);
		try {
			vorbisFile.init(file);
			return vorbisFile.time_total();
		} finally {
			Pools.free(vorbisFile);
		}
	}

	private VorbisFile() {
	}

	public void init(FileHandle fileHandle) {
		fileLength = fileHandle.length();
		try {
			datasource.init(fileHandle.read());
			int ret = open_seekable();
			if (ret != 0) {
				reset();
			}
			if (ret == -1) {
				throw new GdxRuntimeException("VorbisFile: open return -1");
			}
		} catch (Exception e) {
			throw new GdxRuntimeException("VorbisFile: " + e.toString(), e);
		}
	}

	@Override
	public void reset() {
		datasource.poolableReset();
		os.reset();
		op.reset();
		oy.reset();

		fileLength = 0;
		links = 0;
		offsets.clear();
		pcmlengths.clear();
		for (int i = 0; i < vi.size; i++) {
			vi.get(i).free();
		}
		vi.clear();
	}

	private int open_seekable() {
		Info initial_i = Info.obtain();
		Page og = Page.obtain();
		int[] foo = new int[1];
		int ret = fetch_headers(initial_i, foo, null);
		int serialno = foo[0];
		os.reset();

		if (ret == -1) {
			initial_i.free();
			return -1;
		}

		if (ret < 0) {
			initial_i.free();
			return ret;
		}

		// We get the offset for the last page of the physical bitstream.
		// Most OggVorbis files will contain a single logical bitstream
		long end = get_prev_page(og);
		// moer than one logical bitstream?
		if (og.serialno() != serialno) {
			// Chained bitstream. Bisect-search each logical bitstream
			// section. Do so based on serial number only
			if (bisect_forward_serialno(0, 0, end + 1, serialno, 0) < 0) {
				og.free();
				initial_i.free();
				return OV_EREAD;
			}
		} else {
			// Only one logical bitstream
			if (bisect_forward_serialno(0, end, end + 1, serialno, 0) < 0) {
				og.free();
				initial_i.free();
				return OV_EREAD;
			}
		}

		prefetch_all_headers(initial_i);
		og.free();

		return 0;
	}

	private int fetch_headers(Info vi, int[] serialno, Page og_ptr) {
		Page temp = og_ptr;
		Page og = Page.obtain();
		int ret;

		if (temp == null) {
			ret = get_next_page(og, CHUNKSIZE);
			if (ret == OV_EREAD) {
				og.free();
				return OV_EREAD;
			}
			if (ret < 0) {
				og.free();
				return OV_ENOTVORBIS;
			}
			temp = og;
		}

		if (serialno != null)
			serialno[0] = temp.serialno();

		os.init(temp.serialno());

		int i = 0;
		while (i < 3) {
			os.pagein(temp);
			while (i < 3) {
				int result = os.packetout(op);
				if (result == 0) {
					break;
				}
				if (result == -1) {
					op.reset();
					os.reset();
					og.free();
					return -1;
				}
				if (vi.synthesis_headerin(op) != 0) {
					op.reset();
					os.reset();
					og.free();
					return -1;
				}
				i++;
			}

			if (i < 3)
				if (get_next_page(temp, 1) < 0) {
					op.reset();
					os.reset();
					og.free();
					return -1;
				}
		}

		op.reset();
		og.free();
		return 0;
	}

	private void prefetch_all_headers(Info first_i) {
		Page og = Page.obtain();
		int ret;

		vi.ensureCapacity(links - vi.size);
		vi.size = links;
		pcmlengths.ensureCapacity(links - pcmlengths.size);// = new long[links];
		pcmlengths.size = links;

		for (int i = 0; i < links; i++) {
			if (first_i != null && i == 0) {
				// we already grabbed the initial header earlier. This just
				// saves the waste of grabbing it again
				vi.set(i, first_i);
			} else {
				// seek to the location of the initial header
				seek(offsets.get(i)); // !!!
				Info info = Info.obtain();
				vi.set(i, info);

				if (fetch_headers(info, null, null) != -1) {
					os.reset();
				}
			}

			// get the serial number and PCM length of this link. To do this,
			// get the last page of the stream
			{
				long end = offsets.get(i + 1); // !!!
				seek(end);

				while (true) {
					ret = get_prev_page(og);
					if (ret == -1) {
						// this should not be possible
						break;
					}
					if (og.granulepos() != -1) {
						pcmlengths.set(i, og.granulepos());
						break;
					}
				}
			}
		}

		og.free();
	}

	private int get_prev_page(Page page) {
		long begin = fileLength; // !!!
		int ret;
		int offst = -1;
		while (offst == -1) {
			begin -= CHUNKSIZE;
			if (begin < 0)
				begin = 0;
			seek(begin);
			while (fileLength < begin + CHUNKSIZE) {
				ret = get_next_page(page, begin + CHUNKSIZE - fileLength);
				if (ret == OV_EREAD) {
					return OV_EREAD;
				}
				if (ret < 0) {
					if (offst == -1)
						throw new GdxRuntimeException("");
					break;
				}
				offst = ret;
			}
		}
		seek(offst); // !!!
		ret = get_next_page(page, CHUNKSIZE);
		if (ret < 0) {
			return OV_EFAULT;
		}

		return offst;
	}

	private int get_next_page(Page page, long boundary) {
		if (boundary > 0) {
			boundary += fileLength;
		}

		while (true) {
			int more;
			if (boundary > 0 && fileLength >= boundary)
				return OV_FALSE;
			more = oy.pageseek(page);
			if (more < 0) {
				fileLength -= more;
			} else {
				if (more == 0) {
					if (boundary == 0)
						return OV_FALSE;
					int ret = get_data();
					if (ret == 0) {
						return OV_EOF;
					}
					if (ret < 0) {
						return OV_EREAD;
					}
				} else {
					int ret = (int) fileLength; // !!!
					fileLength += more;
					return ret;
				}
			}
		}
	}

	private int get_data() {
		int index = oy.buffer(CHUNKSIZE);
		byte[] buffer = oy.data;
		int bytes = 0;

		try {
			bytes = datasource.read(buffer, index, CHUNKSIZE);
		} catch (Exception e) {
			return OV_EREAD;
		}

		oy.wrote(bytes);
		if (bytes == -1) {
			bytes = 0;
		}
		return bytes;
	}

	private int bisect_forward_serialno(long begin, long searched, long end, int currentno, int m) {
		long endsearched = end;
		long next = end;
		Page page = Page.obtain();
		int ret;

		while (searched < endsearched) {
			long bisect;
			if (endsearched - searched < CHUNKSIZE) {
				bisect = searched;
			} else {
				bisect = (searched + endsearched) / 2;
			}

			seek(bisect);
			ret = get_next_page(page, -1);
			if (ret == OV_EREAD) {
				page.free();
				return OV_EREAD;
			}
			if (ret < 0 || page.serialno() != currentno) {
				endsearched = bisect;
				if (ret >= 0) {
					next = ret;
				}
			} else {
				searched = ret + page.header_len + page.body_len;
			}
		}

		seek(next);
		ret = get_next_page(page, -1);
		if (ret == OV_EREAD) {
			page.free();
			return OV_EREAD;
		}

		if (searched >= end || ret == -1) {
			links = m + 1;
			int newOffsetsCapacity = m + 2;
			offsets.ensureCapacity(newOffsetsCapacity - offsets.size);// = new long[m + 2];
			offsets.size = newOffsetsCapacity;
			offsets.set(m + 1, searched);
		} else {
			ret = bisect_forward_serialno(next, fileLength, end, page.serialno(), m + 1);
			if (ret == OV_EREAD) {
				page.free();
				return OV_EREAD;
			}
		}

		offsets.set(m, begin);
		page.free();
		return 0;
	}

	private void seek(long offst) {
		try {
			datasource.seek(offst);
		} catch (Exception e) {
		}
		this.fileLength = offst;
		oy.resetState();
	}

	public float time_total() {
		float acc = 0;
		for (int j = 0; j < links; j++) {
			acc += (((float) pcmlengths.get(j)) / vi.get(j).rate);
		}
		return (acc);
	}
}
