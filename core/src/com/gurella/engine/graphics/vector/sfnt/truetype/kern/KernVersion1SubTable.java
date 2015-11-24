package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

class KernVersion1SubTable extends KernSubTable {
	public KernVersion1SubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);
	}

	private short getCoverage() {
		return readUnsignedByte(KernVersion1SubOffset.coverage);
	}

	protected boolean isVertical() {
		return (getCoverage() & 80) != 0;
	}

	protected boolean isCrossStream() {
		return (getCoverage() & 40) != 0;
	}

	protected boolean isVariation() {
		return (getCoverage() & 20) != 0;
	}

	private enum KernVersion1SubOffset implements Offset {
		length(0), format(4), coverage(5), tupleIndex(6);

		private final int offset;

		private KernVersion1SubOffset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}