package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class KernVersion0Table extends KernVersionTable {
	private Array<KernVersion0SubTable> subtables = new Array<KernVersion0SubTable>();

	public KernVersion0Table(KernTable parentTable, int offset) {
		super(parentTable, offset);
		createSubTables();
	}

	private int getNumTables() {
		return readUnsignedShort(KernVersion0Offset.nTables);
	}

	private void createSubTables() {
		int subTablesLengthSum = 0;

		for (int i = 0; i < getNumTables(); i++) {
			int subTableRelativeOffset = KernVersion0Offset.tables.offset + subTablesLengthSum;
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
			KernVersion0SubTable kernSubTable = subtables.get(i);
			if (kernSubTable.isHorizontal() && !kernSubTable.isMinimum() && !kernSubTable.isCrossStream()) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				kerning = kernSubTable.isOverride() ? subTableKerning : kerning + subTableKerning;
			}
		}
		return kerning;
	}

	@Override
	public int getVerticalKerning(int leftGlyphId, int rightGlyphId) {
		int kerning = 0;
		for (int i = 0; i < subtables.size; i++) {
			KernVersion0SubTable kernSubTable = subtables.get(i);
			if (!kernSubTable.isHorizontal() && !kernSubTable.isMinimum() && !kernSubTable.isCrossStream()) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				kerning = kernSubTable.isOverride() ? subTableKerning : kerning + subTableKerning;
			}
		}
		return kerning;
	}

	@Override
	public int getCrossStreamKerning(int leftGlyphId, int rightGlyphId) {
		int kerning = 0;
		for (int i = 0; i < subtables.size; i++) {
			KernVersion0SubTable kernSubTable = subtables.get(i);
			if (!kernSubTable.isMinimum() && kernSubTable.isCrossStream()) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				kerning = kernSubTable.isOverride() ? subTableKerning : kerning + subTableKerning;
			}
		}
		return kerning;
	}

	@Override
	public Vector2 getKerning(int leftGlyphId, int rightGlyphId, boolean horizontal, Vector2 out) {
		int x = 0;
		int y = 0;

		for (int i = 0; i < subtables.size; i++) {
			KernVersion0SubTable kernSubTable = subtables.get(i);
			if (kernSubTable.isMinimum()) {
				continue;
			}

			boolean tableHorizontal = kernSubTable.isHorizontal();
			boolean crossStream = kernSubTable.isCrossStream();
			boolean override = kernSubTable.isOverride();

			if (horizontal && tableHorizontal && !crossStream) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				x = override ? subTableKerning : x + subTableKerning;
			} else if (!horizontal && !tableHorizontal && !crossStream) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				y = override ? subTableKerning : y + subTableKerning;
			} else if (crossStream) {
				int subTableKerning = kernSubTable.getKerning(leftGlyphId, rightGlyphId);
				if (horizontal && !tableHorizontal) {
					y = override ? subTableKerning : y + subTableKerning;
				} else if (!horizontal && tableHorizontal) {
					x = override ? subTableKerning : x + subTableKerning;
				}
			}
		}

		return out.set(x, y);
	}

	private KernVersion0SubTable createSubTable(short format, int subTableOffset) {
		switch (format) {
		case 0:
			return new KernVersion0Format0SubTable(this, subTableOffset);
		case 2:
			return new KernVersion0Format2SubTable(this, subTableOffset);
		default:
			return new KernVersion0SubTable(this, subTableOffset);
		}
	}

	private enum KernVersion0Offset implements Offset {
		version(0), nTables(2), tables(4);

		private final int offset;

		private KernVersion0Offset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}