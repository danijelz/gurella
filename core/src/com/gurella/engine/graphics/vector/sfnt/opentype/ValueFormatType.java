package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

public enum ValueFormatType {
	XPlacement(0x0001, SfntDataType.shortValue),
	YPlacement(0x0002, SfntDataType.shortValue),
	XAdvance(0x0004, SfntDataType.shortValue),
	YAdvance(0x0008, SfntDataType.shortValue),
	XPlaDevice(0x0010, SfntDataType.offsetValue),
	YPlaDevice(0x0020, SfntDataType.offsetValue),
	XAdvDevice(0x0040, SfntDataType.offsetValue),
	YAdvDevice(0x0080, SfntDataType.offsetValue);

	public final int bits;
	public final SfntDataType dataType;

	ValueFormatType(int bits, SfntDataType dataType) {
		this.bits = bits;
		this.dataType = dataType;
	}

	public boolean isSignedValue() {
		return dataType.signed;
	}
	
	public static int getNumOfValues(int valueFormat) {
		int count = 0;
		ValueFormatType[] values = values();
		for (int i = 0; i < values.length; i++) {
			if ((values[i].bits & valueFormat) != 0) {
				count++;
			}
		}
		return count;
	}
	
	public static int getNumOfValues(int valueFormat1, int valueFormat2) {
		return getNumOfValues(valueFormat1) + getNumOfValues(valueFormat2);
	}
	
	public static int getValueSize(int valueFormat) {
		int size = 0;
		ValueFormatType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ValueFormatType value = values[i];
			if ((value.bits & valueFormat) != 0) {
				size += value.dataType.size;
			}
		}
		return size;
	}
	
	public static int getValuesSize(int valueFormat1, int valueFormat2) {
		return getValueSize(valueFormat1) + getValueSize(valueFormat2);
	}
	
	public static int getFormatTypeIndex(int valueFormat, ValueFormatType valueFormatType) {
		int index = -1;
		ValueFormatType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ValueFormatType value = values[i];
			if ((value.bits & valueFormat) != 0) {
				index++;
			}
			
			if(value == valueFormatType) {
				return index;
			}
		}
		
		return index;
	}
	
	public static int getFormatTypeOffset(int valueFormat, ValueFormatType valueFormatType) {
		int offset = 0;
		ValueFormatType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ValueFormatType value = values[i];
			if(value == valueFormatType) {
				return offset;
			} else if ((value.bits & valueFormat) != 0) {
				offset += value.dataType.size;
			}
		}
		
		return -1;
	}
}
