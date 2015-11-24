package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.Font;
import com.gurella.engine.graphics.vector.sfnt.cff.CffGlyphFactory;
import com.gurella.engine.graphics.vector.sfnt.truetype.TrueTypeGlyphFactory;
import com.gurella.engine.graphics.vector.sfnt.truetype.kern.KernTable;

public class SfntFont extends Font {
	final TableDirectory tableDirectory;
	
	private GlyphFactory glyphFactory;
	private final IntMap<Glyph> glyphs = new IntMap<Glyph>();

	SfntFont(TableDirectory tableDirectory) {
		this.tableDirectory = tableDirectory;
		glyphFactory = createGlyphFactory();
	}

	private GlyphFactory createGlyphFactory() {
		if(tableDirectory.hasCffOutlines()) {
			return new CffGlyphFactory(tableDirectory);
		} else {
			return new TrueTypeGlyphFactory(tableDirectory);
		}
	}

	@Override
	public float getHorisontalKerning(Glyph first, Glyph second) {
		KernTable kernTable = tableDirectory.getTable(SfntTableTag.kern);
		if (kernTable == null) {
			return 0;
		} else {
			return kernTable.getHorizontalKerning(first.id, second.id);
		}
		// TODO Auto-generated method stub
	}

	@Override
	public Glyph getGlyph(String code) {
		return getGlyph(tableDirectory.getGlyphId(code));
	}

	@Override
	public Glyph getGlyph(int code) {
		int glyphId = tableDirectory.getGlyphId(code);
		Glyph glyph = glyphs.get(glyphId);

		if (glyph == null) {
			glyph = glyphFactory.createGlyph(glyphId);
			HmtxTable hmtxTable = tableDirectory.getTable(SfntTableTag.hmtx);
			glyph.advanceWidth = hmtxTable.getAdvanceWidth(glyphId);
			glyphs.put(glyphId, glyph);
		}

		return glyph;
	}
	
	@Override
	public Glyph getGlyphFromName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontMetrics getFontMetrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getVerticalKerning(Glyph first, Glyph second) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public interface GlyphFactory {
		public Glyph createGlyph(int glyphId);
	}
	
	public interface CharacterMapper {
		
	}
}
