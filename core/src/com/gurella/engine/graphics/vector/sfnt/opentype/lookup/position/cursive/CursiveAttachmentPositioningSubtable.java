package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.cursive;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class CursiveAttachmentPositioningSubtable extends LookupSubTable {
	private CursiveAttachmentPositioningFormatSubtable positioningFormatSubtable;
	
	public CursiveAttachmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}

	private CursiveAttachmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new CursiveAttachmentPositioningFormat1Subtable(this, offset);
		default:
			return new CursiveAttachmentPositioningFormatSubtable(this, offset);
		}
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return positioningFormatSubtable.isGlyphCovered(glyphId);
	}
	
	public short getEntryXCoordinate(int glyphId) {
		return positioningFormatSubtable.getEntryXCoordinate(glyphId);
	}
	
	public short getEntryYCoordinate(int glyphId) {
		return positioningFormatSubtable.getEntryYCoordinate(glyphId);
	}
	
	public short getExitXCoordinate(int glyphId) {
		return positioningFormatSubtable.getExitXCoordinate(glyphId);
	}
	
	public short getExitYCoordinate(int glyphId) {
		return positioningFormatSubtable.getExitYCoordinate(glyphId);
	}
}
