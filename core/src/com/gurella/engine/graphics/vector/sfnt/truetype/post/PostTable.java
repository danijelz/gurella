package com.gurella.engine.graphics.vector.sfnt.truetype.post;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class PostTable extends SfntTable {
	private PostVersionTable postSubTable;
	
	public PostTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
		postSubTable = createPostSubtable();
	}
	
	public PostTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	private PostVersionTable createPostSubtable() {
		float version = getVersion();
		if (version == 1.0f) {
			return new PostVersion1Table(this, offset);
		} else if (version == 2.0f) {
			return new PostVersion2Table(this, offset + PostOffsets.maxMemType1.offset + 4);
		} else if (version == 2.5f) {
			return new PostVersion25Table(this, offset + PostOffsets.maxMemType1.offset + 4);
		} else {
			return new PostVersionTable(this, offset);
		}
	}

	public float getVersion() {
		return readFixed(PostOffsets.version);
	}

	public float getItalicAngle() {
		return readFixed(PostOffsets.italicAngle);
	}

	public short getUnderlinePosition() {
		return readShort(PostOffsets.underlinePosition);
	}

	public short getUnderlineThickness() {
		return readShort(PostOffsets.underlineThickness);
	}

	public long getIsFixedPitch() {
		return readUnsignedInt(PostOffsets.isFixedPitch);
	}
	
	public boolean isFixedPitch() {
		return getIsFixedPitch() != 0;
	}

	public long getMinMemType42() {
		return readUnsignedInt(PostOffsets.minMemType42);
	}

	public long getMaxMemType42() {
		return readUnsignedInt(PostOffsets.maxMemType42);
	}

	public long getMinMemType1() {
		return readUnsignedInt(PostOffsets.minMemType1);
	}

	public long getMaxMemType1() {
		return readUnsignedInt(PostOffsets.maxMemType1);
	}
	
	public int getGlyphId(String name) {
	    return postSubTable.getGlyphId(name);
	}

	public String getGlyphName(int glyphId) {
	    return postSubTable.getGlyphName(glyphId);
	}
	
	private enum PostOffsets implements Offset {
	    version(0),
	    italicAngle(4),
	    underlinePosition(8),
	    underlineThickness(10),
	    isFixedPitch(12),
	    minMemType42(16),
	    maxMemType42(20),
	    minMemType1(24),
	    maxMemType1(28);

		public final int offset;

		private PostOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
