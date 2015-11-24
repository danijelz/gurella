package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.pair;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

class PairAdjustmentPositioningFormat1Subtable extends PairAdjustmentPositioningFormatSubtable {
	private static final int secondGlyphDataSize = SfntDataType.glyphIdValue.size;
	private CoverageTable coverageTable;
	
	public PairAdjustmentPositioningFormat1Subtable(PairAdjustmentPositioningSubtable positioningSubtable, int offset) {
		super(positioningSubtable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}
	
	@Override
	public boolean isGlyphPairCovered(int firstGlyphId, int secondGlyphId) {
		int firstGlyphIndex = coverageTable.getGlyphIndex(firstGlyphId);
		if(firstGlyphIndex < 0) {
			return false;
		}
		
		int pairSetOffset = getPairSetOffset(firstGlyphIndex);
		if(pairSetOffset < 0) {
			return false;
		}
		
		int pairValueIndex = getPairValueIndex(pairSetOffset, secondGlyphId);
		return pairValueIndex >= 0;
	}

	public int getCoverage() {
		return readUnsignedShort(PairAdjustmentPositioning1FormatOffsets.coverage);
	}

	public int getValueFormat1() {
		return readUnsignedShort(PairAdjustmentPositioning1FormatOffsets.valueFormat1);
	}

	public int getValueFormat2() {
		return readUnsignedShort(PairAdjustmentPositioning1FormatOffsets.valueFormat2);
	}

	public int getPairSetCount() {
		return readUnsignedShort(PairAdjustmentPositioning1FormatOffsets.pairSetCount);
	}

	private int getPairSetOffset(int pairSetIndex) {
		if(pairSetIndex < 0 || pairSetIndex >= getPairSetCount()) {
			return -1;
		}
		int pairSetIndexOffset = pairSetIndex * SfntDataType.offsetValue.size;
		return readUnsignedShort(PairAdjustmentPositioning1FormatOffsets.pairSetOffsets.offset + pairSetIndexOffset);
	}
	
	public int getPairValueCount(int pairSetIndex) {
		int pairSetOffset = getPairSetOffset(pairSetIndex);
		if(pairSetOffset < 0) {
			return -1;
		}
		return readUnsignedShort(pairSetOffset);
	}
	
	public int getSecondGlyph(int pairSetIndex, int pairValueIndex) {
		int pairSetOffset = getPairSetOffset(pairSetIndex);
		if(pairSetOffset < 0) {
			return -1;
		}
		
		int pairValueCount = readUnsignedShort(pairSetOffset);
		if(pairValueIndex < 0 || pairValueIndex >= pairValueCount) {
			return -1;
		}
		
		int pairValueRecordsOffset = pairSetOffset + PairSetOffsets.pairValueRecord.offset;
		return readUnsignedShort(pairValueRecordsOffset + (pairValueIndex * getPairValueSize()));
	}

	private int getPairValueSize() {
		return secondGlyphDataSize + ValueFormatType.getValuesSize(getValueFormat1(), getValueFormat2());
	}
	
	public int getValue1(int pairSetIndex, int pairValueIndex, ValueFormatType valueFormatType) {
		int valueIndex = getValue1FormatTypeIndex(valueFormatType);
		if(valueIndex < 0) {
			return 0;
		}
		
		int pairSetOffset = getPairSetOffset(pairSetIndex);
		if(pairSetOffset < 0) {
			return 0;
		}
		
		int pairValueCount = readUnsignedShort(pairSetOffset);
		if(pairValueIndex < 0 || pairValueIndex >= pairValueCount) {
			return 0;
		}
		
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat1(), valueFormatType);
		int valueOffset = getValue1Offset(pairSetOffset, pairValueIndex, formatValueOffset);
		return readValue(valueFormatType, valueOffset);
	}

	private int readValue(ValueFormatType valueFormatType, int valueOffset) {
		return valueFormatType.isSignedValue() ? readShort(valueOffset) : readUnsignedShort(valueOffset);
	}

	private int getValue1FormatTypeIndex(ValueFormatType valueFormatType) {
		return ValueFormatType.getFormatTypeIndex(getValueFormat1(), valueFormatType);
	}
	
	private int getValue2FormatTypeIndex(ValueFormatType valueFormatType) {
		return ValueFormatType.getFormatTypeIndex(getValueFormat2(), valueFormatType);
	}

	private int getValue1Offset(int pairSetOffset, int pairValueIndex, int formatValueOffset) {
		return getPairValueOffset(pairSetOffset, pairValueIndex) + secondGlyphDataSize + formatValueOffset;
	}

	private int getPairValueOffset(int pairSetOffset, int pairValueIndex) {
		int pairValueRecordsOffset = pairSetOffset + PairSetOffsets.pairValueRecord.offset;
		return pairValueRecordsOffset + (pairValueIndex * getPairValueSize());
	}
	
	public int getValue2(int pairSetIndex, int pairValueIndex, ValueFormatType valueFormatType) {
		int valueIndex = getValue2FormatTypeIndex(valueFormatType);
		if(valueIndex < 0) {
			return 0;
		}
		
		int pairSetOffset = getPairSetOffset(pairSetIndex);
		if(pairSetOffset < 0) {
			return 0;
		}
		
		int pairValueCount = readUnsignedShort(pairSetOffset);
		if(pairValueIndex < 0 || pairValueIndex >= pairValueCount) {
			return 0;
		}
		
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat2(), valueFormatType);
		int valueOffset = getValue2Offset(pairSetOffset, pairValueIndex, formatValueOffset);
		return readValue(valueFormatType, valueOffset);
	}

	private int getValue2Offset(int pairSetOffset, int pairValueIndex, int formatValueOffset) {
		int valueFormat1Size = ValueFormatType.getValueSize(getValueFormat1());
		return getPairValueOffset(pairSetOffset, pairValueIndex) + secondGlyphDataSize + valueFormat1Size + formatValueOffset;
	}
	
	@Override
	public int getFirstGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		int valueIndex = getValue1FormatTypeIndex(valueFormatType);
		if(valueIndex < 0) {
			return 0;
		}
		
		int firstGlyphIndex = coverageTable.getGlyphIndex(firstGlyphId);
		if(firstGlyphIndex < 0) {
			return 0;
		}
		
		int pairSetOffset = getPairSetOffset(firstGlyphIndex);
		if(pairSetOffset < 0) {
			return 0;
		}
		
		int pairValueIndex = getPairValueIndex(pairSetOffset, secondGlyphId);
		if(pairValueIndex < 0) {
			return 0;
		}
		
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat1(), valueFormatType);
		int valueOffset = getValue1Offset(pairSetOffset, pairValueIndex, formatValueOffset);
		return readValue(valueFormatType, valueOffset);
	}
	
	@Override
	public int getSecondGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		int valueIndex = getValue2FormatTypeIndex(valueFormatType);
		if(valueIndex < 0) {
			return 0;
		}
		
		int firstGlyphIndex = coverageTable.getGlyphIndex(firstGlyphId);
		if(firstGlyphIndex < 0) {
			return 0;
		}
		
		int pairSetOffset = getPairSetOffset(firstGlyphIndex);
		if(pairSetOffset < 0) {
			return 0;
		}
		
		int pairValueIndex = getPairValueIndex(pairSetOffset, secondGlyphId);
		if(pairValueIndex < 0) {
			return 0;
		}
		
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat2(), valueFormatType);
		int valueOffset = getValue2Offset(pairSetOffset, pairValueIndex, formatValueOffset);
		return readValue(valueFormatType, valueOffset);
	}
	
	private int getPairValueIndex(int pairSetOffset, int secondGlyphId) {
		int pairValueRecordsOffset = pairSetOffset + PairSetOffsets.pairValueRecord.offset;
		int pairValueSize = getPairValueSize();
		
		int lo = 0;
        int hi = readUnsignedShort(pairSetOffset) - 1;
        
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midGlyphId = readUnsignedShort(pairValueRecordsOffset + (mid * pairValueSize));
            
			if(secondGlyphId < midGlyphId) {
				hi = mid - 1;
			} else if (secondGlyphId > midGlyphId) {
				lo = mid + 1;
			} else {
				return mid;
			}
        }
        return -1;
	}

	private enum PairAdjustmentPositioning1FormatOffsets implements Offset {
		posFormat(0), 
		coverage(2), 
		valueFormat1(4), 
		valueFormat2(6), 
		pairSetCount(8), 
		pairSetOffsets(10);

		private final int offset;

		private PairAdjustmentPositioning1FormatOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum PairSetOffsets implements Offset {
		pairValueCount(0), 
		pairValueRecord(2);
		
		private final int offset;
		
		private PairSetOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}