package com.gurella.engine.graphics.vector.sfnt;

import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.Font.Glyph;

public class SfntGlyph extends Glyph {
	Path outline;
	
	public SfntGlyph(int id, Path outline) {
		super(id);
		this.outline = outline;
	}
	
	@Override
	public Path getOutline() {
		return outline;
	}
}