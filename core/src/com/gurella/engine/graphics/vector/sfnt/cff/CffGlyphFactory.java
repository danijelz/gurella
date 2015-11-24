package com.gurella.engine.graphics.vector.sfnt.cff;

import com.gurella.engine.graphics.vector.Font.Glyph;
import com.gurella.engine.graphics.vector.sfnt.SfntTableTag;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.SfntFont.GlyphFactory;

public class CffGlyphFactory implements GlyphFactory {
	private CffTable cffTable; 

	public CffGlyphFactory(TableDirectory tableDirectory) {
		cffTable = tableDirectory.getTable(SfntTableTag.CFF);
		cffTable.init();
	}

	@Override
	public Glyph createGlyph(int glyphId) {
		return cffTable.createGlyph(glyphId);
	}
}
