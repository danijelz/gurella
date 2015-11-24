package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktoligature;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class MarkToLigatureAttachmentPositioningFormatSubtable extends SubTable<MarkToLigatureAttachmentPositioningSubtable> {
	public MarkToLigatureAttachmentPositioningFormatSubtable(MarkToLigatureAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getPosFormat() {
		return readUnsignedShort(0);
	}
	
	public short getMarkXCoordinate(int markGlyphId) {
		return 0;
	}
	
	public short getMarkYCoordinate(int markGlyphId) {
		return 0;
	}
	
	public int getMarkAnchorPoint(int markGlyphId) {
		return 0;
	}
	
	public short getBaseXCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return 0;
	}
	
	public short getBaseYCoordinate(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return 0;
	}
	
	public int getBaseAnchorPoint(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return 0;
	}
	
	public boolean isGlyphPairCovered(int markGlyphId, int ligatureGlyphId, int componentIndex) {
		return false;
	}
}
