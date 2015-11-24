package com.gurella.engine.graphics.vector.sfnt;

public class MaxpTable extends SfntTable {
	MaxpTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public MaxpTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public float getVersion() {
		return readFixed(MaxpOffset.version);
	}

	public int getNumGlyphs() {
		return readUnsignedShort(MaxpOffset.numGlyphs);
	}

	public int getMaxPoints() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxPoints) : 0;
	}

	public int getMaxContours() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxContours) : 0;
	}

	public int getMaxCompositePoints() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxCompositePoints) : 0;
	}

	public int getMaxCompositeContours() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxCompositeContours) : 0;
	}

	public int getMaxZones() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxZones) : 0;
	}

	public int getMaxTwilightPoints() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxTwilightPoints) : 0;
	}

	public int getMaxStorage() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxStorage) : 0;
	}

	public int getMaxFunctionDefs() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxFunctionDefs) : 0;
	}

	public int getMaxInstructionDefs() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxInstructionDefs) : 0;
	}

	public int getMaxStackElements() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxStackElements) : 0;
	}

	public int getMaxSizeOfInstructions() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxSizeOfInstructions) : 0;
	}

	public int getMaxComponentElements() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxComponentElements) : 0;
	}

	public int getMaxComponentDepth() {
		return getVersion() == 1 ? readUnsignedShort(MaxpOffset.maxComponentDepth) : 0;
	}
	
	private enum MaxpOffset implements Offset {
	    // version 0.5 and 1.0
	    version(0),
	    numGlyphs(4),

	    // version 1.0
	    maxPoints(6),
	    maxContours(8),
	    maxCompositePoints(10),
	    maxCompositeContours(12),
	    maxZones(14),
	    maxTwilightPoints(16),
	    maxStorage(18),
	    maxFunctionDefs(20),
	    maxInstructionDefs(22),
	    maxStackElements(24),
	    maxSizeOfInstructions(26),
	    maxComponentElements(28),
	    maxComponentDepth(30);

		private final int offset;

		private MaxpOffset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
