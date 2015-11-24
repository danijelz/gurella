package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.pair;

import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;
import com.gurella.engine.graphics.vector.sfnt.opentype.classdef.ClassDefTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

class PairAdjustmentPositioningFormat2Subtable extends PairAdjustmentPositioningFormatSubtable {
	private CoverageTable coverageTable;
	private ClassDefTable classDef1Table; 
	private ClassDefTable classDef2Table; 
	
	public PairAdjustmentPositioningFormat2Subtable(PairAdjustmentPositioningSubtable positioningSubtable, int offset) {
		super(positioningSubtable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
		classDef1Table = new ClassDefTable(raf, offset + getClassDef1());
		classDef2Table = new ClassDefTable(raf, offset + getClassDef2());
	}
	
	@Override
	public boolean isGlyphPairCovered(int firstGlyphId, int secondGlyphId) {
		int firstGlyphIndex = coverageTable.getGlyphIndex(firstGlyphId);
		if(firstGlyphIndex < 0) {
			return false;
		}
		
		int class1Index = classDef1Table.getGlyphClass(firstGlyphId);
		if(class1Index < 0) {
			return false;
		}
		
		int class2Index = classDef2Table.getGlyphClass(secondGlyphId);
		return class2Index >= 0;
	}

	public int getCoverage() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.coverage);
	}

	public int getValueFormat1() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.valueFormat1);
	}

	public int getValueFormat2() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.valueFormat2);
	}
	
	public int getClassDef1() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.classDef1);
	}

	public int getClassDef2() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.classDef2);
	}

	public int getClass1Count() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.class1Count);
	}

	public int getClass2Count() {
		return readUnsignedShort(PairAdjustmentPositioningFormat2Offsets.class2Count);
	}
	
	private int getClass1RecordSize() {
		return getClass2Count() * getClass2RecordSize();
	}
	
	private int getClass2RecordSize() {
		return ValueFormatType.getValuesSize(getValueFormat1(), getValueFormat2());
	}
	
	private int getClass1Offset(int index) {
		return PairAdjustmentPositioningFormat2Offsets.class1Record.offset + getClass1RecordSize() * index;
	}
	
	private int getClass2Offset(int class1Index, int class2Index) {
		return getClass1Offset(class1Index) + getClass2RecordSize() * class2Index;
	}
	
	private int getValue1FormatTypeIndex(ValueFormatType valueFormatType) {
		return ValueFormatType.getFormatTypeIndex(getValueFormat1(), valueFormatType);
	}
	
	private int readValue(ValueFormatType valueFormatType, int valueOffset) {
		return valueFormatType.isSignedValue() ? readShort(valueOffset) : readUnsignedShort(valueOffset);
	}
	
	private int getValue2FormatTypeIndex(ValueFormatType valueFormatType) {
		return ValueFormatType.getFormatTypeIndex(getValueFormat2(), valueFormatType);
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
		
		int class1Index = classDef1Table.getGlyphClass(firstGlyphId);
		if(class1Index < 0) {
			return 0;
		}
		
		int class2Index = classDef2Table.getGlyphClass(secondGlyphId);
		if(class2Index < 0) {
			return 0;
		}
		
		int class2Offset = getClass2Offset(class1Index, class2Index);
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat1(), valueFormatType);
		return readValue(valueFormatType, class2Offset + formatValueOffset);
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
		
		int class1Index = classDef1Table.getGlyphClass(firstGlyphId);
		if(class1Index < 0) {
			return 0;
		}
		
		int class2Index = classDef2Table.getGlyphClass(secondGlyphId);
		if(class2Index < 0) {
			return 0;
		}
		
		int class2Offset = getClass2Offset(class1Index, class2Index);
		int valueFormat1Size = ValueFormatType.getValueSize(getValueFormat1());
		int formatValueOffset = ValueFormatType.getFormatTypeOffset(getValueFormat2(), valueFormatType);
		return readValue(valueFormatType, class2Offset + valueFormat1Size + formatValueOffset);
	}

	private enum PairAdjustmentPositioningFormat2Offsets implements Offset {
		posFormat(0), 
		coverage(2), 
		valueFormat1(4), 
		valueFormat2(6), 
		classDef1(8), 
		classDef2(10),
		class1Count(12),
		class2Count(14),
		class1Record(16);

		private final int offset;

		private PairAdjustmentPositioningFormat2Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}