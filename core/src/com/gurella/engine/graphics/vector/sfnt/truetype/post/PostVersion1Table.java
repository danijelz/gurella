package com.gurella.engine.graphics.vector.sfnt.truetype.post;

import com.gurella.engine.graphics.vector.sfnt.StandardNames;

class PostVersion1Table extends PostVersionTable {
	public PostVersion1Table(PostTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	@Override
	public int getGlyphId(String name) {
		int glyphId = StandardNames.getIndex(name);
		return glyphId < 0 ? 0 : glyphId;
	}
	
	@Override
	public String getGlyphName(int glyphId) {
		return StandardNames.getName(glyphId);
	}
}