package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktomark;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class MarkToMarkAttachmentPositioningSubtable extends LookupSubTable {
	private MarkToMarkAttachmentPositioningFormatSubtable positioningFormatSubtable;
	
	public MarkToMarkAttachmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}

	private MarkToMarkAttachmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new MarkToMarkAttachmentPositioningFormat1Subtable(this, offset);
		default:
			return new MarkToMarkAttachmentPositioningFormatSubtable(this, offset);
		}
	}
	
	public short getMark1XCoordinate(int mark1GlyphId) {
		return positioningFormatSubtable.getMark1XCoordinate(mark1GlyphId);
	}
	
	public short getMark1YCoordinate(int mark1GlyphId) {
		return positioningFormatSubtable.getMark1YCoordinate(mark1GlyphId);
	}
	
	public int getMark1AnchorPoint(int mark1GlyphId) {
		return positioningFormatSubtable.getMark1AnchorPoint(mark1GlyphId);
	}
	
	public short getMark2XCoordinate(int mark1GlyphId, int mark2GlyphId) {
		return positioningFormatSubtable.getMark2XCoordinate(mark1GlyphId, mark2GlyphId);
	}
	
	public short getMark2YCoordinate(int mark1GlyphId, int mark2GlyphId) {
		return positioningFormatSubtable.getMark2YCoordinate(mark1GlyphId, mark2GlyphId);
	}
	
	public int getMark2AnchorPoint(int mark1GlyphId, int mark2GlyphId) {
		return positioningFormatSubtable.getMark2AnchorPoint(mark1GlyphId, mark2GlyphId);
	}
	
	public boolean isMarkPairCovered(int mark1GlyphId, int mark2GlyphId) {
		return positioningFormatSubtable.isMarkPairCovered(mark1GlyphId, mark2GlyphId);
	}
}
