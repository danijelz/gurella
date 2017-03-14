package com.gurella.engine.graphics.vector.bitmapfont;

import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.Font;
import com.gurella.engine.graphics.vector.Path;

public class BitmapFont extends Font {
	private com.badlogic.gdx.graphics.g2d.BitmapFont gdxFont;
	private BitmapFontData data;
	
	private final IntMap<Glyph> glyphs = new IntMap<Glyph>();
	
	public BitmapFont(com.badlogic.gdx.graphics.g2d.BitmapFont rawFont) {
		this.gdxFont = rawFont;
		data = rawFont.getData();
	}

	@Override
	public Glyph getGlyph(int code) {
		char casted = (char) code;
		char ch = casted == code ? casted : 0;
		Glyph glyph = glyphs.get(ch);

		if (glyph == null) {
			glyph = new BitmapGlyph(ch, data.getGlyph(ch));
			glyphs.put(ch, glyph);
		}
		
		return glyph;
	}

	@Override
	public Glyph getGlyph(String code) {
		return getGlyph(Character.codePointAt(code, 0));
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
	public float getHorisontalKerning(Glyph first, Glyph second) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVerticalKerning(Glyph first, Glyph second) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final class BitmapGlyph extends Glyph {
		private com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph gdxGlyph;
		
		public BitmapGlyph(int id, com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph rawGlyph) {
			super(id);
			this.gdxGlyph = rawGlyph;
		}

		@Override
		public Path getOutline() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
