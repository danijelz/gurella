package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

//TODO unused
public class SfntFontManager {
	private static final ObjectMap<String, SfntFile> fontsByFile = new ObjectMap<String, SfntFile>();
	private static final ObjectMap<SfntFontInfo, SfntFont> managedFonts = new ObjectMap<SfntFontInfo, SfntFont>();
	
	static Array<SfntFont> manage(String ttfFile) {
		return manage(Gdx.files.internal(ttfFile));
	}
	
	public static synchronized Array<SfntFont> manage(FileHandle ttfFile) { //TODO make package private
		String filePath = ttfFile.path();
		SfntFile fontFile = fontsByFile.get(filePath);
		if(fontFile == null) {
			RandomAccessFile raf = new RandomAccessFile(ttfFile);
			fontFile = new SfntFile(raf);
			fontsByFile.put(filePath, fontFile);
		}
		return fontFile.getFonts();
	}
	
	static class SfntFontInfo {
		
	}
}
