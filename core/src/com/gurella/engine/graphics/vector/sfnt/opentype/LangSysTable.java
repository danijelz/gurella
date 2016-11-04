package com.gurella.engine.graphics.vector.sfnt.opentype;

import java.util.Arrays;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class LangSysTable extends Table {
	public final int tag;
	private int[] featureIndex;
	
	public LangSysTable(RandomAccessFile raf, int offset, int tag) {
		super(raf, offset);
		this.tag = tag;
	}
	
	public int getTag() {
		return tag;
	}
	
	public int getReqFeatureIndex() {
		return readUnsignedShort(LangSysOffsets.reqFeatureIndex);
	}
	
	public int getFeatureCount() {
		return readUnsignedShort(LangSysOffsets.featureCount);
	}
	
	public int[] getFeatureIndex() {
		if(featureIndex == null) {
			featureIndex = readUnsignedShortArray(LangSysOffsets.featureIndex, getFeatureCount());
		}
		return featureIndex;
	}
	
	private enum LangSysOffsets implements Offset {
		lookupOrder(0),
		reqFeatureIndex(2),
		featureCount(4),
		featureIndex(6);

		private final int offset;

		private LangSysOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}