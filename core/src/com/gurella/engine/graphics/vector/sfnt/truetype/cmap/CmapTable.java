package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class CmapTable extends SfntTable {
	private static final int subtableHeaderSize = 8;

	private IntMap<IntMap<IntMap<CmapSubTable>>> subtables = new IntMap<IntMap<IntMap<CmapSubTable>>>();

	public CmapTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
		initSubTables();
	}
	
	public CmapTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
		initSubTables();
	}

	private void initSubTables() {
		for (int i = 0; i < getNumTables(); i++) {
			int subtableHeaderOffset = CmapOffsets.subtables.offset + (i * subtableHeaderSize);
			int platformId = readUnsignedShort(subtableHeaderOffset);
			int encodingId = readUnsignedShort(subtableHeaderOffset + CmapSubtableHeaderOffsets.encodingID.offset);
			int subTableOffset = readUnsignedIntAsInt(subtableHeaderOffset + CmapSubtableHeaderOffsets.subTableOffset.offset);
			appendCmapSubtable(platformId, encodingId, createCmapSubtable(subTableOffset));
		}
	}

	public int getVersion() {
		return readUnsignedShort(CmapOffsets.version);
	}

	private short getNumTables() {
		return readShort(CmapOffsets.numTables);
	}

	private void appendCmapSubtable(int platformId, int encodingId, CmapSubTable cmapSubTable) {
		IntMap<IntMap<CmapSubTable>> cmapsByPatform = subtables.get(platformId);
		if (cmapsByPatform == null) {
			cmapsByPatform = new IntMap<IntMap<CmapSubTable>>();
			subtables.put(platformId, cmapsByPatform);
		}

		IntMap<CmapSubTable> cmapsByEncoding = cmapsByPatform.get(encodingId);
		if (cmapsByEncoding == null) {
			cmapsByEncoding = new IntMap<CmapSubTable>();
			cmapsByPatform.put(encodingId, cmapsByEncoding);
		}

		cmapsByEncoding.put(cmapSubTable.getLanguage(), cmapSubTable);
	}

	private CmapSubTable createCmapSubtable(int subTableRelativeOffset) {
		int format = readUnsignedShort(subTableRelativeOffset);
		int subTableOffset = offset + subTableRelativeOffset;
		switch (format) {
		case 0:
			return new CmapFormat0SubTable(this, subTableOffset);
		case 2:
			return new CmapFormat2SubTable(this, subTableOffset);
		case 4:
			return new CmapFormat4SubTable(this, subTableOffset);
		case 6:
			return new CmapFormat6SubTable(this, subTableOffset);
		case 8:
			return new CmapFormat8SubTable(this, subTableOffset);
		case 10:
			return new CmapFormat10SubTable(this, subTableOffset);
		case 12:
			return new CmapFormat12SubTable(this, subTableOffset);
		case 13:
			return new CmapFormat13SubTable(this, subTableOffset);
		case 14:
			return new CmapFormat14SubTable(this, subTableOffset);
		default:
			return new CmapSubTable(this, subTableOffset);
		}
	}

	public int getGlyphId(int platformId, int encodingId, int languageId, int charCode) {
		IntMap<IntMap<CmapSubTable>> cmapsByPatform = subtables.get(platformId);
		if (cmapsByPatform == null) {
			return 0;
		}

		IntMap<CmapSubTable> cmapsByEncoding = cmapsByPatform.get(encodingId);
		if (cmapsByEncoding == null) {
			return 0;
		}

		CmapSubTable cmapSubTable = cmapsByEncoding.get(languageId);
		if (cmapSubTable == null) {
			return 0;
		}

		return cmapSubTable.getGlyphId(charCode);
	}

	private enum CmapOffsets implements Offset {
		version(0), numTables(2), subtables(4);

		private final int offset;

		private CmapOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}

	private enum CmapSubtableHeaderOffsets implements Offset {
		platformID(0), encodingID(2), subTableOffset(4);

		private final int offset;

		private CmapSubtableHeaderOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
