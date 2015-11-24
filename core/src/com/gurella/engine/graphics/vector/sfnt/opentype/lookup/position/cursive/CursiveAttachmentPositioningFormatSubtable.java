package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.cursive;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class CursiveAttachmentPositioningFormatSubtable extends SubTable<CursiveAttachmentPositioningSubtable>  {
	public CursiveAttachmentPositioningFormatSubtable(CursiveAttachmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
	}

	public boolean isGlyphCovered(int glyphId) {
		return false;
	}

	public int getPosFormat() {
		return readUnsignedShort(0);
	}
	
	public short getEntryXCoordinate(int glyphId) {
		return 0;
	}
	
	public short getEntryYCoordinate(int glyphId) {
		return 0;
	}
	
	public short getExitXCoordinate(int glyphId) {
		return 0;
	}
	
	public short getExitYCoordinate(int glyphId) {
		return 0;
	}
}
