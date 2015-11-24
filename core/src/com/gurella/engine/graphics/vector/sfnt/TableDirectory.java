package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.sfnt.SfntEncodings.WindowsEncodingId;
import com.gurella.engine.graphics.vector.sfnt.truetype.cmap.CmapTable;

public abstract class TableDirectory extends Table {
	protected final IntMap<SfntTable> tables = new IntMap<SfntTable>();
	
	public TableDirectory(RandomAccessFile raf, int offset) {
		super(raf, offset);
		
		for (int i = 0; i < getNumTables(); i++) {
			SfntTable table = readTable(i);
			tables.put(table.tag, table);
		}
	}
	
	protected abstract SfntTable readTable(int index);

	public abstract <T extends SfntTable> T getTable(SfntTableTag sfntTableType);
	
	public abstract <T extends SfntTable> T getTable(int tableId);
	
	public abstract int getNumTables();
	
	public int getGlyphId(String code) {
		return getGlyphId(Character.codePointAt(code, 0));
	}
	
	public int getGlyphId(int code) {
		CmapTable cmapTable = getTable(SfntTableTag.cmap);
		return cmapTable.getGlyphId(PlatformId.Windows.value, WindowsEncodingId.UnicodeUCS2.value, 0, code);
		//return cmapTable.mapCharCode(PlatformId.Macintosh.value, MacintoshEncodingId.Roman.value, code);
		//return cmapTable.mapCharCode(PlatformId.Unicode.value, UnicodeEncodingId.Unicode2_0_BMP.value, code);
	}
	
	public int getNumGlyphs() {//TODO remove
		return this.<MaxpTable> getTable(SfntTableTag.maxp).getNumGlyphs();
	}
	
	public boolean hasCffOutlines() {
		return getTable(SfntTableTag.CFF.id) != null;
	}
}
