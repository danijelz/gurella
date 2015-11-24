package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktobase;

import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.MarkArrayTable;

public class MarkToBaseAttachmentPositioningFormat1Subtable extends MarkToBaseAttachmentPositioningFormatSubtable {
	private CoverageTable markCoverage;
	private CoverageTable baseCoverage;
	private MarkArrayTable markArray;
	private BaseArrayTable baseArray;
	
	public MarkToBaseAttachmentPositioningFormat1Subtable(MarkToBaseAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
		markCoverage = new CoverageTable(raf, offset + getMarkCoverageOffset());
		baseCoverage = new CoverageTable(raf, offset + getBaseCoverageOffset());
		markArray = new MarkArrayTable(raf, offset + getMarkArrayOffset());
		baseArray = new BaseArrayTable(raf, offset + getBaseArrayOffset(), getClassCount());
	}
	
	private int getMarkCoverageOffset() {
		return readUnsignedShort(MarkToBaseAttachmentPositioningFormat1Offsets.MarkCoverage);
	}
	
	private int getBaseCoverageOffset() {
		return readUnsignedShort(MarkToBaseAttachmentPositioningFormat1Offsets.BaseCoverage);
	}
	
	private int getClassCount() {
		return readUnsignedShort(MarkToBaseAttachmentPositioningFormat1Offsets.ClassCount);
	}
	
	private int getMarkArrayOffset() {
		return readUnsignedShort(MarkToBaseAttachmentPositioningFormat1Offsets.MarkArray);
	}
	
	private int getBaseArrayOffset() {
		return readUnsignedShort(MarkToBaseAttachmentPositioningFormat1Offsets.BaseArray);
	}
	
	@Override
	public short getMarkXCoordinate(int markGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return markArray.getXCoordinate(markGlyphIndex);
	}
	
	@Override
	public short getMarkYCoordinate(int markGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return markArray.getYCoordinate(markGlyphIndex);
	}

	@Override
	public int getMarkAnchorPoint(int markGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		return markArray.getAnchorPoint(markGlyphIndex);
	}

	@Override
	public short getBaseXCoordinate(int markGlyphId, int baseGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int baseGlyphIndex = baseCoverage.getGlyphIndex(baseGlyphId);
		if(baseGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return baseArray.getXCoordinate(baseGlyphIndex, markClass);
	}

	@Override
	public short getBaseYCoordinate(int markGlyphId, int baseGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int baseGlyphIndex = baseCoverage.getGlyphIndex(baseGlyphId);
		if(baseGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return baseArray.getYCoordinate(baseGlyphIndex, markClass);
	}

	@Override
	public int getBaseAnchorPoint(int markGlyphId, int baseGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int baseGlyphIndex = baseCoverage.getGlyphIndex(baseGlyphId);
		if(baseGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return baseArray.getAnchorPoint(baseGlyphIndex, markClass);
	}

	@Override
	public boolean isGlyphPairCovered(int markGlyphId, int baseGlyphId) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return false;
		}
		
		int baseGlyphIndex = baseCoverage.getGlyphIndex(baseGlyphId);
		if(baseGlyphIndex < 0) {
			return false;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return baseArray.isPairCovered(baseGlyphIndex, markClass);
	}

	private enum MarkToBaseAttachmentPositioningFormat1Offsets implements Offset {
		PosFormat(0), 
		MarkCoverage(2),
		BaseCoverage(4),
		ClassCount(6),
		MarkArray(8),
		BaseArray(10);

		private final int offset;

		private MarkToBaseAttachmentPositioningFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
