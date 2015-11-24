package com.gurella.engine.graphics.vector.sfnt.truetype.post;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.StandardNames;

class PostVersion2Table extends PostVersionTable {
	private final ObjectIntMap<String> nameIds = new ObjectIntMap<String>();
	
	PostVersion2Table(PostTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	private int getNumberOfGlyphs() {
		return readUnsignedShort(PostFormat2Offsets.numberOfGlyphs);
	}
	
	@Override
	public int getGlyphId(String name) {
		int glyphId = nameIds.get(name, -1);
		if(glyphId > -1) {
			return glyphId;
		}
		
		glyphId = StandardNames.getIndex(name);
		if(glyphId > -1) {
			return glyphId;
		}
	    
		int numberOfGlyphs = getNumberOfGlyphs();
		int numberOfNonstandardGlyphs = numberOfGlyphs - 258;
		if(nameIds.size >= numberOfNonstandardGlyphs) {
			return 0;
		}
		
		int namesOffset = PostFormat2Offsets.glyphNameIndex.offset + SfntDataType.shortValue.size * numberOfGlyphs;
		for(int i = 0; i < nameIds.size; i++) {
			short numberOfChars = readUnsignedByte(namesOffset);
			namesOffset += SfntDataType.unsignedByteValue.size + numberOfChars;
		}
		
		for(int i = nameIds.size; i < numberOfNonstandardGlyphs; i++) {
			short numberOfChars = readUnsignedByte(namesOffset);
			namesOffset += SfntDataType.unsignedByteValue.size;
			
			String glyphName = readString(namesOffset, numberOfChars);
			glyphId = findGlyphIdByNameIndex(numberOfGlyphs, i + 258);
			nameIds.put(glyphName, glyphId);
			if(glyphName.equals(name)) {
				return glyphId;
			}
			
			namesOffset += numberOfChars;
		}
		
		return 0;
	}
	
	private int findGlyphIdByNameIndex(int numberOfGlyphs, int nameIndex) {
		for(int i = 0; i < numberOfGlyphs; i++) {
			if(nameIndex == readUnsignedShort(PostFormat2Offsets.glyphNameIndex.offset + SfntDataType.shortValue.size * i)) {
				return i;
			}
		}
		
		return 0;
	}
	
	@Override
	public String getGlyphName(int glyphId) {
		int numberOfGlyphs = getNumberOfGlyphs();
		if(glyphId < 0 || glyphId >= numberOfGlyphs) {
			return "";
		} 
		
		int glyphNameIndex = readUnsignedShort(PostFormat2Offsets.glyphNameIndex.offset + SfntDataType.shortValue.size * glyphId);
		if (glyphNameIndex < 258) {
			return StandardNames.getName(glyphNameIndex);
		} else {
			int nonstandardIndex = glyphNameIndex - 258;
			int namesOffset = PostFormat2Offsets.glyphNameIndex.offset + SfntDataType.shortValue.size * numberOfGlyphs;
			
			for(int i = 0; i < nonstandardIndex; i++) {
				short numberOfChars = readUnsignedByte(namesOffset);
				namesOffset += SfntDataType.unsignedByteValue.size + numberOfChars;
			}
			
			short numberOfChars = readUnsignedByte(namesOffset);
			String glyphName = readString(namesOffset + SfntDataType.unsignedByteValue.size, numberOfChars);
			nameIds.put(glyphName, glyphId);
			
			return glyphName;
		}
	}

	private enum PostFormat2Offsets implements Offset {
		numberOfGlyphs(0), 
		glyphNameIndex(2);

		public final int offset;

		private PostFormat2Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}