package com.gurella.engine.graphics.vector.sfnt.truetype.post;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

class PostVersionTable extends SubTable<PostTable> {
	public PostVersionTable(PostTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getGlyphId(String name) {
	    return 0;
	}

	public String getGlyphName(int glyphId) {
	    return "";
	}
}