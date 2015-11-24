package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

class KernVersion0SubTable extends KernSubTable {
	public KernVersion0SubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);
	}

	private short getCoverage() {
		return readUnsignedByte(KernVersion0SubOffset.coverage);
	}

	protected boolean isHorizontal() {
		return (getCoverage() & 1) != 0;
	}

	protected boolean isMinimum() {
		return (getCoverage() & 2) != 0;
	}

	protected boolean isCrossStream() {
		return (getCoverage() & 4) != 0;
	}

	protected boolean isOverride() {
		return (getCoverage() & 8) != 0;
	}

	enum KernVersion0SubOffset implements Offset {
		version(0), length(2), format(4), coverage(5);

		final int offset;

		private KernVersion0SubOffset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}