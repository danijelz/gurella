package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktomark;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.AnchorTableReader;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.MarkArrayTable;

public class MarkToMarkAttachmentPositioningFormat1Subtable extends MarkToMarkAttachmentPositioningFormatSubtable {
	private CoverageTable mark1Coverage;
	private CoverageTable mark2Coverage;
	private MarkArrayTable mark1Array;
	
	private AnchorTableReader anchorTableReader;
	
	public MarkToMarkAttachmentPositioningFormat1Subtable(MarkToMarkAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
		mark1Coverage = new CoverageTable(raf, offset + getMark1CoverageOffset());
		mark2Coverage = new CoverageTable(raf, offset + getMark2CoverageOffset());
		mark1Array = new MarkArrayTable(raf, offset + getMark1ArrayOffset());
		
		anchorTableReader = new AnchorTableReader(raf);
	}
	
	private int getMark1CoverageOffset() {
		return readUnsignedShort(MarkToMarkAttachmentPositioningFormat1Offsets.Mark1Coverage);
	}
	
	private int getMark2CoverageOffset() {
		return readUnsignedShort(MarkToMarkAttachmentPositioningFormat1Offsets.Mark2Coverage);
	}
	
	private int getClassCount() {
		return readUnsignedShort(MarkToMarkAttachmentPositioningFormat1Offsets.ClassCount);
	}
	
	private int getMark1ArrayOffset() {
		return readUnsignedShort(MarkToMarkAttachmentPositioningFormat1Offsets.Mark1Array);
	}
	
	private int getMark2ArrayOffset() {
		return readUnsignedShort(MarkToMarkAttachmentPositioningFormat1Offsets.Mark2Array);
	}
	
	@Override
	public short getMark1XCoordinate(int mark1GlyphId) {
		int markGlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return mark1Array.getXCoordinate(markGlyphIndex);
	}
	
	@Override
	public short getMark1YCoordinate(int mark1GlyphId) {
		int markGlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return mark1Array.getYCoordinate(markGlyphIndex);
	}

	@Override
	public int getMark1AnchorPoint(int mark1GlyphId) {
		int markGlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return mark1Array.getAnchorPoint(markGlyphIndex);
	}
	
	private int getMark2Count() {
		return readUnsignedShort(getMark2ArrayOffset());
	}
	
	private int getMark2RecordOffset(int index) {
		int mark2RecordSize = getClassCount() * SfntDataType.offsetValue.size;
		return getMark2ArrayOffset() + SfntDataType.unsignedShortValue.size + (mark2RecordSize * index);
	}

	@Override
	public short getMark2XCoordinate(int mark1GlyphId, int mark2GlyphId) {
		int mark1GlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(mark1GlyphIndex < 0) {
			return 0;
		}
		
		int mark2GlyphIndex = mark2Coverage.getGlyphIndex(mark2GlyphId);
		if(mark2GlyphIndex < 0 || mark2GlyphIndex >= getMark2Count()) {
			return 0;
		}
		
		int markClass = mark1Array.getMarkClass(mark1GlyphIndex);
		if(markClass < 0 || markClass >= getClassCount()) {
			return 0;
		}
		
		int mark2AnchorOffset = readUnsignedShort(getMark2RecordOffset(mark2GlyphIndex) + (markClass * SfntDataType.offsetValue.size));
		int mark2ArrayAbsoluteOffset = offset + getMark2ArrayOffset();
		return anchorTableReader.getXCoordinate(mark2ArrayAbsoluteOffset + mark2AnchorOffset);
	}

	@Override
	public short getMark2YCoordinate(int mark1GlyphId, int mark2GlyphId) {
		int mark1GlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(mark1GlyphIndex < 0) {
			return 0;
		}
		
		int mark2GlyphIndex = mark2Coverage.getGlyphIndex(mark2GlyphId);
		if(mark2GlyphIndex < 0 || mark2GlyphIndex >= getMark2Count()) {
			return 0;
		}
		
		int markClass = mark1Array.getMarkClass(mark1GlyphIndex);
		if(markClass < 0 || markClass >= getClassCount()) {
			return 0;
		}
		
		int mark2AnchorOffset = readUnsignedShort(getMark2RecordOffset(mark2GlyphIndex) + (markClass * SfntDataType.offsetValue.size));
		int mark2ArrayAbsoluteOffset = offset + getMark2ArrayOffset();
		return anchorTableReader.getYCoordinate(mark2ArrayAbsoluteOffset + mark2AnchorOffset);
	}

	@Override
	public int getMark2AnchorPoint(int mark1GlyphId, int mark2GlyphId) {
		int mark1GlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(mark1GlyphIndex < 0) {
			return 0;
		}
		
		int mark2GlyphIndex = mark2Coverage.getGlyphIndex(mark2GlyphId);
		if(mark2GlyphIndex < 0 || mark2GlyphIndex >= getMark2Count()) {
			return 0;
		}
		
		int markClass = mark1Array.getMarkClass(mark1GlyphIndex);
		if(markClass < 0 || markClass >= getClassCount()) {
			return 0;
		}
		
		int mark2AnchorOffset = readUnsignedShort(getMark2RecordOffset(mark2GlyphIndex) + (markClass * SfntDataType.offsetValue.size));
		int mark2ArrayAbsoluteOffset = offset + getMark2ArrayOffset();
		return anchorTableReader.getAnchorPoint(mark2ArrayAbsoluteOffset + mark2AnchorOffset);
	}

	@Override
	public boolean isMarkPairCovered(int mark1GlyphId, int mark2GlyphId) {
		int mark1GlyphIndex = mark1Coverage.getGlyphIndex(mark1GlyphId);
		if(mark1GlyphIndex < 0) {
			return false;
		}
		
		int mark2GlyphIndex = mark2Coverage.getGlyphIndex(mark2GlyphId);
		if(mark2GlyphIndex < 0 || mark2GlyphIndex >= getMark2Count()) {
			return false;
		}
		
		int markClass = mark1Array.getMarkClass(mark1GlyphIndex);
		return markClass >= 0 && markClass < getClassCount();
	}

	private enum MarkToMarkAttachmentPositioningFormat1Offsets implements Offset {
		PosFormat(0), 
		Mark1Coverage(2),
		Mark2Coverage(4),
		ClassCount(6),
		Mark1Array(8),
		Mark2Array(10);

		private final int offset;

		private MarkToMarkAttachmentPositioningFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
