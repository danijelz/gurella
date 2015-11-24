package com.gurella.engine.graphics.vector.sfnt;

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

public class TrueTypeTableDirectory extends TableDirectory {
	TrueTypeTableDirectory(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}

	@Override
	protected SfntTable readTable(int index)  {
		int sfntTableOffset = SfntHeaderOffsets.tables.offset + TableHeaderOffsets.tableHeaderLength * index;
		int tag = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.tag.offset);
		long checkSum = readUnsignedInt(sfntTableOffset + TableHeaderOffsets.checkSum.offset);
		int offset = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.tableOffset.offset);
		int length = readUnsignedIntAsInt(sfntTableOffset + TableHeaderOffsets.length.offset);
		SfntTableTag tableType = SfntTableTag.getTypeById(tag);
		
		switch (tableType) {//TODO add factory to SfntTableType
		case CFF:
			return new CffTable(this, tag, checkSum, offset, length);
		case cmap:
			return new CmapTable(this, tag, checkSum, offset, length);
		case glyf:
			return new GlyfTable(this, tag, checkSum, offset, length);
		case GPOS:
			return new GposTable(this, tag, checkSum, offset, length);
		case GSUB:
			return new GsubTable(this, tag, checkSum, offset, length);
		case hdmx:
			return new HdmxTable(this, tag, checkSum, offset, length);
		case head:
			return new HeadTable(this, tag, checkSum, offset, length);
		case hhea:
			return new HheaTable(this, tag, checkSum, offset, length);
		case hmtx:
			return new HmtxTable(this, tag, checkSum, offset, length);
		case loca:
			return new LocaTable(this, tag, checkSum, offset, length);
		case maxp:
			return new MaxpTable(this, tag, checkSum, offset, length);
		case name:
			return new NameTable(this, tag, checkSum, offset, length);
		case OS_2:
			return new Os2Table(this, tag, checkSum, offset, length);
		case post:
			return new PostTable(this, tag, checkSum, offset, length);
		case kern:
			return new KernTable(this, tag, checkSum, offset, length);
		case vhea:
			return new VheaTable(this, tag, checkSum, offset, length);
		case vmtx:
			return new VmtxTable(this, tag, checkSum, offset, length);
		case VORG:
			return new VorgTable(this, tag, checkSum, offset, length);
		case DSIG:
			return new DsigTable(this, tag, checkSum, offset, length);
		default:
			return new SfntTable(this, offset, tag, checkSum, length);
		}
	}
	
	public float getVersion() {
		return readFixed(SfntHeaderOffsets.version);
	}
	
	@Override
	public int getNumTables() {
		return readUnsignedShort(SfntHeaderOffsets.numTables);
	}
	
	public int getSearchRange() {
		return readUnsignedShort(SfntHeaderOffsets.searchRange);
	}
	
	public int getEntrySelector() {
		return readUnsignedShort(SfntHeaderOffsets.entrySelector);
	}
	
	public int getRangeShift() {
		return readUnsignedShort(SfntHeaderOffsets.rangeShift);
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
	
	private enum SfntHeaderOffsets implements Offset {
		version(0),
		numTables(4),
		searchRange(6),
		entrySelector(8),
		rangeShift(10),
		tables(12);

		private final int offset;

		private SfntHeaderOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum TableHeaderOffsets implements Offset {
		tag(0),
		checkSum(4),
		tableOffset(8),
		length(12);
		
		public static final int tableHeaderLength = 16;

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
