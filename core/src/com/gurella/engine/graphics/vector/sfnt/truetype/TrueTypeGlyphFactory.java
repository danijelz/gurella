package com.gurella.engine.graphics.vector.sfnt.truetype;

import com.gurella.engine.graphics.vector.Font.Glyph;
import com.gurella.engine.graphics.vector.sfnt.SfntTableTag;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.SfntFont.GlyphFactory;
import com.gurella.engine.graphics.vector.sfnt.truetype.glyf.GlyfTable;

public class TrueTypeGlyphFactory implements GlyphFactory {
	private GlyfTable glyfTable; 

	public TrueTypeGlyphFactory(TableDirectory tableDirectory) {
		glyfTable = tableDirectory.getTable(SfntTableTag.glyf);
		glyfTable.init();
	}

	@Override
	public Glyph createGlyph(int glyphId) {
		return glyfTable.createGlyph(glyphId);
	}
}
