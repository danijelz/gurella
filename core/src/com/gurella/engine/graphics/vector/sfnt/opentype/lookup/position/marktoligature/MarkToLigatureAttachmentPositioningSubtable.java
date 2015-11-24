package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktoligature;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class MarkToLigatureAttachmentPositioningSubtable extends LookupSubTable {
	private MarkToLigatureAttachmentPositioningFormatSubtable positioningFormatSubtable;
	
	public MarkToLigatureAttachmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}

	private MarkToLigatureAttachmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new MarkToLigatureAttachmentPositioningFormat1Subtable(this, offset);
		default:
			return new MarkToLigatureAttachmentPositioningFormatSubtable(this, offset);
		}
	}
	
	public short getMarkXCoordinate(int markGlyphId) {
		return positioningFormatSubtable.getMarkXCoordinate(markGlyphId);
	}
	
	public short getMarkYCoordinate(int markGlyphId) {
		return positioningFormatSubtable.getMarkYCoordinate(markGlyphId);
	}
	
	public int getMarkAnchorPoint(int markGlyphId) {
		return positioningFormatSubtable.getMarkAnchorPoint(markGlyphId);
	}
	
	public short getBaseXCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return positioningFormatSubtable.getBaseXCoordinate(markGlyphId, ligatureGlyphId, componentIndex);
	}
	
	public short getBaseYCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return positioningFormatSubtable.getBaseYCoordinate(markGlyphId, ligatureGlyphId, componentIndex);
	}
	
	public int getBaseAnchorPoint(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return positioningFormatSubtable.getBaseAnchorPoint(markGlyphId, ligatureGlyphId, componentIndex);
	}
	
	public boolean isGlyphPairCovered(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return positioningFormatSubtable.isGlyphPairCovered(markGlyphId, ligatureGlyphId, componentIndex);
	}
}
