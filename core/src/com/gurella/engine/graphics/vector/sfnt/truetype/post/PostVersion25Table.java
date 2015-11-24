package com.gurella.engine.graphics.vector.sfnt.truetype.post;

import com.gurella.engine.graphics.vector.sfnt.StandardNames;

class PostVersion25Table extends PostVersionTable {
	PostVersion25Table(PostTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	private int getNumberOfGlyphs() {
		return readUnsignedShort(PostFormat25Offsets.numberOfGlyphs);
	}
	
	@Override
	public int getGlyphId(String name) {
		int glyphNameIndex = StandardNames.getIndex(name);
		if(glyphNameIndex < 0) {
			return 0;
		}
		
		int offsetToGlyphNameOffsets = PostFormat25Offsets.glyphNameIndex.offset;
		int numberOfGlyphs = getNumberOfGlyphs();
		for(int i = 0; i < numberOfGlyphs; i++) {
			if(glyphNameIndex == i - readByte(offsetToGlyphNameOffsets + i)) {
				return i;
			}
		}
		
		return 0;
	}
	
	@Override
	public String getGlyphName(int glyphId) {
		if(glyphId < 0 || glyphId >= StandardNames.length() || glyphId >= getNumberOfGlyphs()) {
			return "";
		} else {
			int offsetToGlyphNameOffset = PostFormat25Offsets.glyphNameIndex.offset + glyphId;
			byte glyphNameOffset = readByte(offsetToGlyphNameOffset);
			int glyphNameIndex = glyphId + glyphNameOffset;
			return StandardNames.getName(glyphNameIndex);
		}
	}

	private enum PostFormat25Offsets implements Offset {
		numberOfGlyphs(0), 
		glyphNameIndex(2);

		public final int offset;

		private PostFormat25Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}