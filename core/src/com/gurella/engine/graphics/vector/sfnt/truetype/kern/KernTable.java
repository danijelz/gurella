package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class KernTable extends SfntTable {
	private KernVersionTable kernVersionTable;

	public KernTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
		kernVersionTable = createKernVersionTable();
	}
	
	public KernTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	private KernVersionTable createKernVersionTable() {
		int version = readUnsignedShort(0);
		switch (version) {
		case 0:
			return new KernVersion0Table(this, offset);
		case 1:
			float version1 = readFixed(0);
			if (version1 == 1) {
				return new KernVersion1Table(this, offset);
			} else {
				return new KernVersionTable(this, offset);
			}
		default:
			return new KernVersionTable(this, offset);
		}
	}

	public float getHorizontalKerning(int leftGlyphId, int rightGlyphId) {
		return kernVersionTable.getHorizontalKerning(leftGlyphId, rightGlyphId);
	}

	public float getVerticalKerning(int leftGlyphId, int rightGlyphId) {
		return kernVersionTable.getVerticalKerning(leftGlyphId, rightGlyphId);
	}

	public float getCrossStreamKerning(int leftGlyphId, int rightGlyphId) {
		return kernVersionTable.getCrossStreamKerning(leftGlyphId, rightGlyphId);
	}

	public Vector2 getKerning(int leftGlyphId, int rightGlyphId, boolean horizontal, Vector2 out) {
		return kernVersionTable.getKerning(leftGlyphId, rightGlyphId, horizontal, out);
	}
}
