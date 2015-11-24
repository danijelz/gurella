package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktoligature;

import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.MarkArrayTable;

public class MarkToLigatureAttachmentPositioningFormat1Subtable extends MarkToLigatureAttachmentPositioningFormatSubtable {
	private CoverageTable markCoverage;
	private CoverageTable ligatureCoverage;
	private MarkArrayTable markArray;
	private LigatureArrayTable ligatureArray;
	
	public MarkToLigatureAttachmentPositioningFormat1Subtable(MarkToLigatureAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
		markCoverage = new CoverageTable(raf, offset + getMarkCoverageOffset());
		ligatureCoverage = new CoverageTable(raf, offset + getLigatureCoverageOffset());
		markArray = new MarkArrayTable(raf, offset + getMarkArrayOffset());
		ligatureArray = new LigatureArrayTable(raf, offset + getLigatureArrayOffset(), getClassCount());
	}
	
	private int getMarkCoverageOffset() {
		return readUnsignedShort(MarkToLigatureAttachmentPositioningFormat1Offsets.MarkCoverage);
	}
	
	private int getLigatureCoverageOffset() {
		return readUnsignedShort(MarkToLigatureAttachmentPositioningFormat1Offsets.LigatureCoverage);
	}
	
	private int getClassCount() {
		return readUnsignedShort(MarkToLigatureAttachmentPositioningFormat1Offsets.ClassCount);
	}
	
	private int getMarkArrayOffset() {
		return readUnsignedShort(MarkToLigatureAttachmentPositioningFormat1Offsets.MarkArray);
	}
	
	private int getLigatureArrayOffset() {
		return readUnsignedShort(MarkToLigatureAttachmentPositioningFormat1Offsets.LigatureArray);
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
	public short getBaseXCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int ligatureGlyphIndex = ligatureCoverage.getGlyphIndex(ligatureGlyphId);
		if(ligatureGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return ligatureArray.getXCoordinate(ligatureGlyphIndex, componentIndex, markClass);
	}

	@Override
	public short getBaseYCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int ligatureGlyphIndex = ligatureCoverage.getGlyphIndex(ligatureGlyphId);
		if(ligatureGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return ligatureArray.getYCoordinate(ligatureGlyphIndex, componentIndex, markClass);
	}

	@Override
	public int getBaseAnchorPoint(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return 0;
		}
		
		int ligatureGlyphIndex = ligatureCoverage.getGlyphIndex(ligatureGlyphId);
		if(ligatureGlyphIndex < 0) {
			return 0;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return ligatureArray.getAnchorPoint(ligatureGlyphIndex, componentIndex, markClass);
	}

	@Override
	public boolean isGlyphPairCovered(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		int markGlyphIndex = markCoverage.getGlyphIndex(markGlyphId);
		if(markGlyphIndex < 0) {
			return false;
		}
		
		int ligatureGlyphIndex = ligatureCoverage.getGlyphIndex(ligatureGlyphId);
		if(ligatureGlyphIndex < 0) {
			return false;
		}
		
		int markClass = markArray.getMarkClass(markGlyphIndex);
		return ligatureArray.isPairCovered(ligatureGlyphIndex, componentIndex, markClass);
	}

	private enum MarkToLigatureAttachmentPositioningFormat1Offsets implements Offset {
		PosFormat(0), 
		MarkCoverage(2),
		LigatureCoverage(4),
		ClassCount(6),
		MarkArray(8),
		LigatureArray(10);

		private final int offset;

		private MarkToLigatureAttachmentPositioningFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
