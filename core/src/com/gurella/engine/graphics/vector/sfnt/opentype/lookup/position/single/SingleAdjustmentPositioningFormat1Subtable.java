package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.single;

import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class SingleAdjustmentPositioningFormat1Subtable extends SingleAdjustmentPositioningFormatSubtable {
	private CoverageTable coverageTable;
	
	public SingleAdjustmentPositioningFormat1Subtable(SingleAdjustmentPositioningSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}
	
	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}
	
	private int getCoverage() {
		return readUnsignedShort(SingleAdjustmentPositioningFormat1Offsets.coverage);
	}
	
	public int getValueFormat() {
		return readUnsignedShort(SingleAdjustmentPositioningFormat1Offsets.valueFormat);
	}
	
	@Override
	public int getGlyphValue(int glyphId, ValueFormatType valueFormatType) {
		int valueFormat = getValueFormat();
		int valueIndex = ValueFormatType.getFormatTypeIndex(valueFormat, valueFormatType);
		if(valueIndex < 0) {
			return 0;
		}
		
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if(glyphIndex < 0) {
			return 0;
		}
		
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(valueFormat, valueFormatType);
		int valueOffset = SingleAdjustmentPositioningFormat1Offsets.value.offset + formatValueOffset;
		return valueFormatType.isSignedValue() ? readShort(valueOffset) : readUnsignedShort(valueOffset);
	}

	private enum SingleAdjustmentPositioningFormat1Offsets implements Offset {
		posFormat(0), 
		coverage(2), 
		valueFormat(4), 
		value(6);

		private final int offset;

		private SingleAdjustmentPositioningFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
