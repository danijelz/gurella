package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class KernVersion1Table extends KernVersionTable {
	private Array<KernVersion1SubTable> subtables = new Array<KernVersion1SubTable>();

	public KernVersion1Table(KernTable parentTable, int offset) {
		super(parentTable, offset);
		createSubTables();
	}

	private int getNumTables() {
		return readUnsignedIntAsInt(KernVersion1Offset.nTables);
	}

	private void createSubTables() {
		int subTablesLengthSum = 0;

		for (int i = 0; i < getNumTables(); i++) {
			int subTableRelativeOffset = KernVersion1Offset.tables.offset + subTablesLengthSum;
			int length = readUnsignedShort(subTableRelativeOffset + KernVersion0SubTable.KernVersion0SubOffset.length.offset);
			subTablesLengthSum += length;
			short format = readUnsignedByte(subTableRelativeOffset + KernVersion0SubTable.KernVersion0SubOffset.format.offset);
			subtables.add(createSubTable(format, offset + subTableRelativeOffset));
		}
	}

	@Override
	public int getHorizontalKerning(int leftGlyphId, int rightGlyphId) {
		int kerning = 0;
		for (int i = 0; i < subtables.size; i++) {
			KernVersion1SubTable kernSubTable = subtables.get(i);
			if (!kernSubTable.isVertical() && !kernSubTable.isVariation() && !kernSubTable.isCrossStream()) {
				kerning += kernSubTable.getKerning(leftGlyphId, rightGlyphId);
			}
		}
		return kerning;
	}

	@Override
	public int getVerticalKerning(int leftGlyphId, int rightGlyphId) {
		int kerning = 0;
		for (int i = 0; i < subtables.size; i++) {
			KernVersion1SubTable kernSubTable = subtables.get(i);
			if (kernSubTable.isVertical() && !kernSubTable.isVariation() && !kernSubTable.isCrossStream()) {
				kerning += kernSubTable.getKerning(leftGlyphId, rightGlyphId);
			}
		}
		return kerning;
	}

	@Override
	public int getCrossStreamKerning(int leftGlyphId, int rightGlyphId) {
		int kerning = 0;
		for (int i = 0; i < subtables.size; i++) {
			KernVersion1SubTable kernSubTable = subtables.get(i);
			if (!kernSubTable.isVariation() && kernSubTable.isCrossStream()) {
				kerning += kernSubTable.getKerning(leftGlyphId, rightGlyphId);
			}
		}
		return kerning;
	}

	@Override
	public Vector2 getKerning(int leftGlyphId, int rightGlyphId, boolean horizontal, Vector2 out) {
		int x = 0;
		int y = 0;

		for (int i = 0; i < subtables.size; i++) {
			KernVersion1SubTable kernSubTable = subtables.get(i);
			if (kernSubTable.isVariation()) {
				continue;
			}

			boolean tableHorizontal = !kernSubTable.isVertical();
			boolean crossStream = kernSubTable.isCrossStream();

			if (horizontal && tableHorizontal && !crossStream) {
				x += kernSubTable.getKerning(leftGlyphId, rightGlyphId);
			} else if (!horizontal && !tableHorizontal && !crossStream) {
				y += kernSubTable.getKerning(leftGlyphId, rightGlyphId);
			} else if (crossStream) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				if (horizontal && !tableHorizontal) {
					y += subTableKerning;
				} else if (!horizontal && tableHorizontal) {
					x += subTableKerning;
				}
			}
		}

		return out.set(x, y);
	}

	private KernVersion1SubTable createSubTable(short format, int subTableOffset) {
		switch (format) {
		case 0:
			return new KernVersion1Format0SubTable(this, subTableOffset);
		case 1:
			return new KernVersion1Format1SubTable(this, subTableOffset);
		case 2:
			return new KernVersion1Format2SubTable(this, subTableOffset);
		case 3:
			return new KernVersion1Format3SubTable(this, subTableOffset);
		default:
			return new KernVersion1SubTable(this, subTableOffset);
		}
	}

	private enum KernVersion1Offset implements Offset {
		version(0), nTables(4), tables(8);

		private final int offset;

		private KernVersion1Offset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}