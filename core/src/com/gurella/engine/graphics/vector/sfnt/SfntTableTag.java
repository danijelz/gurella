package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;

public enum SfntTableTag {
	head("head"), 
	hhea("hhea"), 
	maxp("maxp"), 
	OS_2("OS/2"), 
	hmtx("hmtx"), 
	hdmx("hdmx"), 
	cmap("cmap"), 
	loca("loca"), 
	glyf("glyf"), 
	GPOS("GPOS"), 
	GSUB("GSUB"), 
	kern("kern"), 
	name("name"), 
	post("post"), 
	CFF("CFF "), 
	vhea("vhea"), 
	vmtx("vmtx"), 
	VORG("VORG"), 
	DSIG("DSIG"), 
	unknown("    ");

	private static IntMap<SfntTableTag> tableTypesByTag;

	public final String tableName;
	public final int id;

	SfntTableTag(String tableName) {
		this.tableName = tableName;
		id = SfntTagUtils.tagToInt(tableName);
		getTableTypesByTag().put(id, this);
	}

	private static IntMap<SfntTableTag> getTableTypesByTag() {
		if (tableTypesByTag == null) {
			tableTypesByTag = new IntMap<SfntTableTag>();
		}
		return tableTypesByTag;
	}
	
	public static SfntTableTag getTypeById(int id) {
		return tableTypesByTag.get(id, unknown);
	}
	
    public static SfntTableTag getTypeByTag(byte... tag) {
		return getTypeById(SfntTagUtils.tagToInt(tag));
	}
    
    public static SfntTableTag getTypeByTag(String tag) {
    	return getTypeById(SfntTagUtils.tagToInt(tag));
    }
}
