package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktomark;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class MarkToMarkAttachmentPositioningFormatSubtable extends SubTable<MarkToMarkAttachmentPositioningSubtable> {
	public MarkToMarkAttachmentPositioningFormatSubtable(MarkToMarkAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getPosFormat() {
		return readUnsignedShort(0);
	}
	
	public short getMark1XCoordinate(int mark1GlyphId) {
		return 0;
	}
	
	public short getMark1YCoordinate(int mark1GlyphId) {
		return 0;
	}
	
	public int getMark1AnchorPoint(int mark1GlyphId) {
		return 0;
	}
	
	public short getMark2XCoordinate(int mark1GlyphId, int mark2GlyphId) {
		return 0;
	}
	
	public short getMark2YCoordinate(int mark1GlyphId, int mark2GlyphId) {
		return 0;
	}
	
	public int getMark2AnchorPoint(int mark1GlyphId, int mark2GlyphId) {
		return 0;
	}
	
	public boolean isMarkPairCovered(int mark1GlyphId, int mark2GlyphId) {
		return false;
	}
}
