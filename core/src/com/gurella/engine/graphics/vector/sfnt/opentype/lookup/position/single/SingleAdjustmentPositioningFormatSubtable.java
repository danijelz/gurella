package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.single;

import com.gurella.engine.graphics.vector.sfnt.SubTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;

public class SingleAdjustmentPositioningFormatSubtable extends SubTable<SingleAdjustmentPositioningSubtable> {
	public SingleAdjustmentPositioningFormatSubtable(SingleAdjustmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getPosFormat() {
		return readUnsignedShort(0);
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return false;
	}
	
	public int getGlyphValue(int glyphId, ValueFormatType valueFormatType) {
		return 0;
	}
}
