package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.SfntTagUtils;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;
import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable.OpenTypeLookupSubTableFactory;

public abstract class OpenTypeLayoutTable extends SfntTable {
	IntMap<OpenTypeScriptTable> scriptTables = new IntMap<OpenTypeScriptTable>();
	Array<OpenTypeFeatureTable> featureTables = new Array<OpenTypeFeatureTable>();
	Array<OpenTypeLookupTable> lookupTables = new Array<OpenTypeLookupTable>();
	
	OpenTypeLayoutTable(TrueTypeTableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(directoryTable, offset, tag, checkSum, length);
		initSubTables();
	}
	
	OpenTypeLayoutTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
		initSubTables();
	}

	private void initSubTables() {
		parseScripts(readUnsignedShort(OpenTypeLayoutOffset.scriptList));
		parseFeatures(readUnsignedShort(OpenTypeLayoutOffset.featureList));
		parseLookups(readUnsignedShort(OpenTypeLayoutOffset.lookupList));
	}
	
	public float getVersion() {
		return readFixed(OpenTypeLayoutOffset.version);
	}
	
	private void parseScripts(int scriptListOffset) {
		int listsOffset = offset + scriptListOffset;
		int count = readUnsignedShort(scriptListOffset);
		for(int i = 0; i < count; i++) {
			raf.setPosition(listsOffset + 2 + (i * 6));
			int tag = raf.readUnsignedIntAsInt();
			int subTableOffset = raf.readUnsignedShort();
			OpenTypeScriptTable subTable = new OpenTypeScriptTable(this, listsOffset + subTableOffset, tag);
			scriptTables.put(tag, subTable);	
		}
	}
	
	private void parseFeatures(int featureListOffset) {
		int listOffset = offset + featureListOffset;
		int count = readUnsignedShort(featureListOffset);
		for(int i = 0; i < count; i++) {
			raf.setPosition(listOffset + 2 + (i * 6));
			int tag = raf.readUnsignedIntAsInt();
			int subTableOffset = raf.readUnsignedShort();
			OpenTypeFeatureTable subTable = new OpenTypeFeatureTable(this, listOffset + subTableOffset, tag);
			featureTables.add(subTable);
		}
	}
	
	private void parseLookups(int lookupListOffset) {
		int listOffset = offset + lookupListOffset;
		raf.setPosition(listOffset);
		
		int count = raf.readUnsignedShort();
		int lastPosition = raf.getPosition();
		OpenTypeLookupSubTableFactory lookupSubTableFactory = getLookupSubTableFactory();
		for(int i = 0; i < count; i++) {
			raf.setPosition(lastPosition);
			int subTableOffset = raf.readUnsignedShort();
			lastPosition = raf.getPosition();
			OpenTypeLookupTable subTable = new OpenTypeLookupTable(this, listOffset + subTableOffset, lookupSubTableFactory);
			lookupTables.add(subTable);
		}
	}

	protected abstract OpenTypeLookupSubTableFactory getLookupSubTableFactory();
	
	public OpenTypeScriptTable getScriptTable(ScriptTag tag) {
		return scriptTables.get(tag.tag);
	}
	
	public OpenTypeScriptTable getDefaultScriptTable() {
		return getScriptTable(ScriptTag.DFLT);
	}
	
	public OpenTypeScriptTable getScriptTableOrDefault(ScriptTag tag) {
		OpenTypeScriptTable scriptTable = getScriptTable(tag);
		return scriptTable == null ? getDefaultScriptTable() : scriptTable;
	}
	
	public int[] getFeatureIndex(ScriptTag tag, LanguageTag languageTag) {
		OpenTypeScriptTable scriptTable = getScriptTable(tag);
		if(scriptTable == null) {
			return null;
		}
		LangSysTable langSys = scriptTable.getLangSys(languageTag);
		return langSys == null ? null : langSys.getFeatureIndex();
	}
	
	public int[] getFeatureIndexOrDefault(ScriptTag tag, LanguageTag languageTag) {
		OpenTypeScriptTable scriptTable = getScriptTable(tag);
		if(scriptTable == null) {
			scriptTable = getDefaultScriptTable();
		}
		if(scriptTable == null) {
			return null;
		}
		LangSysTable langSys = scriptTable.getLangSys(languageTag);
		if(langSys == null) {
			langSys = scriptTable.getDefaultLangSys();
		}
		return langSys == null ? null : langSys.getFeatureIndex();
	}
	
	public int[] getDefaultFeatureIndex() {
		OpenTypeScriptTable scriptTable = getDefaultScriptTable();
		if(scriptTable == null) {
			return null;
		}
		LangSysTable langSys = scriptTable.getDefaultLangSys();
		return langSys == null ? null : langSys.getFeatureIndex();
	}
	
	private enum OpenTypeLayoutOffset implements Offset {
	    version(0),
	    scriptList(4),
	    featureList(6),
	    lookupList(8);

		private final int offset;

		private OpenTypeLayoutOffset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
