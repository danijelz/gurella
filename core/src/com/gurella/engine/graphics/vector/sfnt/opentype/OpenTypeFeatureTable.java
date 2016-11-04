package com.gurella.engine.graphics.vector.sfnt.opentype;

public class OpenTypeFeatureTable extends OpenTypeTaggedSubTable {
	private int[] lookupListIndex;

	OpenTypeFeatureTable(OpenTypeLayoutTable openTypeTable, int offset, int tag) {
		super(openTypeTable, offset, tag);
	}

	private int getLookupCount() {
		return readUnsignedShort(OpenTypeFeatureOffsets.lookupCount);
	}

	public int[] getLookupListIndex() {
		if (lookupListIndex == null) {
			lookupListIndex = readUnsignedShortArray(OpenTypeFeatureOffsets.lookupListIndex, getLookupCount());
		}
		return lookupListIndex;
	}

	private enum OpenTypeFeatureOffsets implements Offset {
		featureParams(0), 
		lookupCount(2), 
		lookupListIndex(4);

		private final int offset;

		private OpenTypeFeatureOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
