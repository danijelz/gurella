package com.gurella.engine.graphics.vector.sfnt.woff;

import com.gurella.engine.graphics.vector.sfnt.HdmxTable;
import com.gurella.engine.graphics.vector.sfnt.HeadTable;
import com.gurella.engine.graphics.vector.sfnt.HmtxTable;
import com.gurella.engine.graphics.vector.sfnt.LocaTable;
import com.gurella.engine.graphics.vector.sfnt.MaxpTable;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.SfntTableTag;
import com.gurella.engine.graphics.vector.sfnt.SfntTagUtils;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.VheaTable;
import com.gurella.engine.graphics.vector.sfnt.VmtxTable;
import com.gurella.engine.graphics.vector.sfnt.cff.CffTable;
import com.gurella.engine.graphics.vector.sfnt.cff.VorgTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.DsigTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.GposTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.GsubTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.HheaTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.NameTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.cmap.CmapTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.glyf.GlyfTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.kern.KernTable;
import com.gurella.engine.graphics.vector.sfnt.truetype.os2.Os2Table;
import com.gurella.engine.graphics.vector.sfnt.truetype.post.PostTable;
import com.gurella.engine.graphics.vector.sfnt.woff.zlib.Inflater;

//https://github.com/jsxgraph/jsxgraph/blob/master/src/utils/zip.js
//https://github.com/nodeca/pako
//https://code.google.com/p/miniz/source/browse/trunk/miniz.c
//https://github.com/madler/zlib
public class WoffTableDirectory extends TableDirectory {
	public WoffTableDirectory(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	@Override
	protected SfntTable readTable(int index) {
		int sfntTableOffset = WoffHeaderOffsets.tables.offset + TableHeaderOffsets.tableHeaderLength * index;
		int tag = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.tag.offset);
		long checkSum = readUnsignedInt(sfntTableOffset + TableHeaderOffsets.origChecksum.offset);
		int offset = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.tableOffset.offset);
		int length = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.origLength.offset);

		System.out.println(SfntTagUtils.stringValue(tag));

		SfntTableTag tableType = SfntTableTag.getTypeById(tag);
		if (SfntTableTag.unknown == tableType) {
			return new SfntTable(this, offset, tag, checkSum, length);
		} else {
			int compLength = readUnsignedIntAsInt(sfntTableOffset+ TableHeaderOffsets.compLength.offset);
			if(compLength == length) {
				return createTable(raf, tag, checkSum, offset, length);
			}
			
			///byte[] uncompressedData2 = new Inflate().inflate(readBytes(offset + 2, compLength));
			//new Unzip(compressedData).deflate(uncompressedData);
			byte[] compressedData = readBytes(offset, compLength);
			byte[] uncompressedData = new byte[length];

			Inflater inflater = new Inflater(Inflater.DEF_WBITS + 32);
			inflater.setInput(compressedData);
			inflater.setOutput(uncompressedData);

			int err;
			while (inflater.total_out < length && inflater.total_in < compLength) {
				inflater.avail_in = inflater.avail_out = 1; // force small buffers
				err = inflater.inflate(Inflater.Z_NO_FLUSH);
				if (err == Inflater.Z_STREAM_END)
					break;
				if (err != Inflater.Z_OK) {
					 throw new IllegalStateException();
				}
			}

			err = inflater.end();
			if (err != Inflater.Z_OK) {
				 throw new IllegalStateException();
			}
			
			return createTable(new RandomAccessFile(uncompressedData), tag, checkSum, 0, length);
		}
	}
	
	private SfntTable createTable(RandomAccessFile raf, int tag, long checkSum, int offset, int length)  {
		SfntTableTag tableType = SfntTableTag.getTypeById(tag);
		
		switch (tableType) {//TODO add factory to SfntTableType
		case CFF:
			return new CffTable(raf, this, tag, checkSum, offset, length);
		case cmap:
			return new CmapTable(raf, this, tag, checkSum, offset, length);
		case glyf:
			return new GlyfTable(raf, this, tag, checkSum, offset, length);
		case GPOS:
			return new GposTable(raf, this, tag, checkSum, offset, length);
		case GSUB:
			return new GsubTable(raf, this, tag, checkSum, offset, length);
		case hdmx:
			return new HdmxTable(raf, this, tag, checkSum, offset, length);
		case head:
			return new HeadTable(raf, this, tag, checkSum, offset, length);
		case hhea:
			return new HheaTable(raf, this, tag, checkSum, offset, length);
		case hmtx:
			return new HmtxTable(raf, this, tag, checkSum, offset, length);
		case loca:
			return new LocaTable(raf, this, tag, checkSum, offset, length);
		case maxp:
			return new MaxpTable(raf, this, tag, checkSum, offset, length);
		case name:
			return new NameTable(raf, this, tag, checkSum, offset, length);
		case OS_2:
			return new Os2Table(raf, this, tag, checkSum, offset, length);
		case post:
			return new PostTable(raf, this, tag, checkSum, offset, length);
		case kern:
			return new KernTable(raf, this, tag, checkSum, offset, length);
		case vhea:
			return new VheaTable(raf, this, tag, checkSum, offset, length);
		case vmtx:
			return new VmtxTable(raf, this, tag, checkSum, offset, length);
		case VORG:
			return new VorgTable(raf, this, tag, checkSum, offset, length);
		case DSIG:
			return new DsigTable(raf, this, tag, checkSum, offset, length);
		default:
			return new SfntTable(raf, this, offset, tag, checkSum, length);
		}
	}
	
	public int getSignature() {
		return readUnsignedIntAsInt(WoffHeaderOffsets.signature);
	}
	
	public int getFlavor() {
		return readUnsignedIntAsInt(WoffHeaderOffsets.flavor);
	}
	
	public int getLength() {
		return readUnsignedIntAsInt(WoffHeaderOffsets.length);
	}
	
	@Override
	public int getNumTables() {
		return readUnsignedShort(WoffHeaderOffsets.numTables);
	}
	
	public int getMajorVersion() {
		return readUnsignedShort(WoffHeaderOffsets.majorVersion);
	}
	
	public int getMinorVersion() {
		return readUnsignedShort(WoffHeaderOffsets.minorVersion);
	}
	
	@Override
	public <T extends SfntTable> T getTable(SfntTableTag sfntTableType) {
		@SuppressWarnings("unchecked")
		T casted = (T) tables.get(sfntTableType.id);
		return casted;
	}
	
	@Override
	public <T extends SfntTable> T getTable(int tableId) {
		@SuppressWarnings("unchecked")
		T casted = (T) tables.get(tableId);
		return casted;
	}
	
	private enum WoffHeaderOffsets implements Offset {
		signature(0),
		flavor(4),
		length(8),
		numTables(12),
		reserved(14),
		totalSfntSize(16),
		majorVersion(20),
		minorVersion(22),
		metaOffset(24),
		metaLength(28),
		metaOrigLength(32),
		privOffset(36),
		privLength(40),
		tables(44);

		private final int offset;

		private WoffHeaderOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum TableHeaderOffsets implements Offset {
		tag(0),
		tableOffset(4),
		compLength(8),
		origLength(12),
		origChecksum(16);
		
		public static final int tableHeaderLength = 20;

		private final int offset;

		private TableHeaderOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
