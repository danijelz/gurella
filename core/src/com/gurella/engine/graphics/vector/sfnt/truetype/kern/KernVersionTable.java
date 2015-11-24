package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.graphics.vector.sfnt.SubTable;

class KernVersionTable extends SubTable<KernTable> {
	public KernVersionTable(KernTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getHorizontalKerning(int leftGlyphId, int rightGlyphId) {
		return 0;
	}
	
	public int getVerticalKerning(int leftGlyphId, int rightGlyphId) {
		return 0;
	}
	
	public int getCrossStreamKerning(int leftGlyphId, int rightGlyphId) {
		return 0;
	}
	
	public Vector2 getKerning(int leftGlyphId, int rightGlyphId, boolean horizontal, Vector2 out) {
		return out;
	}
}