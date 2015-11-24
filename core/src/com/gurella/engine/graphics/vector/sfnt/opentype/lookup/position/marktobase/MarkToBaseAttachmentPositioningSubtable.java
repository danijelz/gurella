package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktobase;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class MarkToBaseAttachmentPositioningSubtable extends LookupSubTable {
	private MarkToBaseAttachmentPositioningFormatSubtable positioningFormatSubtable;
	
	public MarkToBaseAttachmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}

	private MarkToBaseAttachmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new MarkToBaseAttachmentPositioningFormat1Subtable(this, offset);
		default:
			return new MarkToBaseAttachmentPositioningFormatSubtable(this, offset);
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
	
	public short getBaseXCoordinate(int markGlyphId, int baseGlyphId) {
		return positioningFormatSubtable.getBaseXCoordinate(markGlyphId, baseGlyphId);
	}
	
	public short getBaseYCoordinate(int markGlyphId, int baseGlyphId) {
		return positioningFormatSubtable.getBaseYCoordinate(markGlyphId, baseGlyphId);
	}
	
	public int getBaseAnchorPoint(int markGlyphId, int baseGlyphId) {
		return positioningFormatSubtable.getBaseAnchorPoint(markGlyphId, baseGlyphId);
	}
	
	public boolean isGlyphPairCovered(int markGlyphId, int baseGlyphId) {
		return positioningFormatSubtable.isGlyphPairCovered(markGlyphId, baseGlyphId);
	}
}
