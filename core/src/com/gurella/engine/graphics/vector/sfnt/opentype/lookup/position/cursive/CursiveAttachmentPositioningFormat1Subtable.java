package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.cursive;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.AnchorTableReader;

public class CursiveAttachmentPositioningFormat1Subtable extends CursiveAttachmentPositioningFormatSubtable {
	private static final short entryExitRecordSize = 4;
	
	private CoverageTable coverageTable;
	private AnchorTableReader anchorTableReader;
	
	public CursiveAttachmentPositioningFormat1Subtable(CursiveAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
		anchorTableReader = new AnchorTableReader(raf);
	}
	
	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}
	
	private int getCoverage() {
		return readUnsignedShort(CursiveAttachmentPositioningFormat1Offsets.coverage);
	}
	
	private int getEntryExitCount() {
		return readUnsignedShort(CursiveAttachmentPositioningFormat1Offsets.entryExitCount);
	}
	
	private int getEntryAnchorOffset(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if(glyphIndex < 0) {
			return -1;
		}
		
		if(glyphIndex >= getEntryExitCount()) {
			return -1;
		}
		
		return readUnsignedShort(CursiveAttachmentPositioningFormat1Offsets.entryExitRecords.offset + (glyphIndex * entryExitRecordSize));
	}
	
	private int getExitAnchorOffset(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if(glyphIndex < 0) {
			return -1;
		}
		
		if(glyphIndex >= getEntryExitCount()) {
			return -1;
		}
		
		return readUnsignedShort(CursiveAttachmentPositioningFormat1Offsets.entryExitRecords.offset + (glyphIndex * entryExitRecordSize) + SfntDataType.offsetValue.size);
	}
	
	@Override
	public short getEntryXCoordinate(int glyphId) {
		int entryAnchorOffset = getEntryAnchorOffset(glyphId);
		if(entryAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getXCoordinate(entryAnchorOffset);
	}
	
	@Override
	public short getEntryYCoordinate(int glyphId) {
		int entryAnchorOffset = getEntryAnchorOffset(glyphId);
		if(entryAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getYCoordinate(entryAnchorOffset);
	}
	
	@Override
	public short getExitXCoordinate(int glyphId) {
		int exitAnchorOffset = getExitAnchorOffset(glyphId);
		if(exitAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getXCoordinate(exitAnchorOffset);
	}
	
	@Override
	public short getExitYCoordinate(int glyphId) {
		int exitAnchorOffset = getExitAnchorOffset(glyphId);
		if(exitAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getYCoordinate(exitAnchorOffset);
	}

	private enum CursiveAttachmentPositioningFormat1Offsets implements Offset {
		posFormat(0), 
		coverage(2), 
		entryExitCount(4), 
		entryExitRecords(6);

		private final int offset;

		private CursiveAttachmentPositioningFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
