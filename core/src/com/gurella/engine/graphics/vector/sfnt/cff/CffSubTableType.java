package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.IntMap;

//TODO unused
public enum CffSubTableType {
	HEADER(1), 
	NAME_INDEX(2),
	TOP_DICT_INDEX(3), 
	STRING_INDEX(4),
	GLOBAL_SUBR_INDEX(5),
	ENCODINGS(6),
	CHARSETS(7),
	FDSELECT(8),
	CHARSTRINGS_INDEX(9),
	FONT_DICT_INDEX(10),
	PRIVATE_DICT(11),
	LOCAL_SUBR_INDEX(12),
	NOTICES(13);
	
	private static IntMap<CffSubTableType> tableTypesByTag;

	public final int id;

	CffSubTableType(int id) {
		this.id = id;
		getTableTypesByTag().put(id, this);
	}

	private static IntMap<CffSubTableType> getTableTypesByTag() {
		if (tableTypesByTag == null) {
			tableTypesByTag = new IntMap<CffSubTableType>();
		}
		return tableTypesByTag;
	}
	
	public static CffSubTableType getTypeById(int id) {
		return tableTypesByTag.get(id);
	}
}
