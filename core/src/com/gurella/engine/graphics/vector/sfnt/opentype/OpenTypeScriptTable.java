package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.badlogic.gdx.utils.IntMap;

public class OpenTypeScriptTable extends OpenTypeTaggedSubTable {
	private LangSysTable defaultLangSys;
	private IntMap<LangSysTable> langSysByTag;

	OpenTypeScriptTable(OpenTypeLayoutTable openTypeTable, int offset, int tag) {
		super(openTypeTable, offset, tag);
	}

	@Override
	protected void init() {
		super.init();
		langSysByTag = new IntMap<LangSysTable>();
		int defaultLangSysOffset = readUnsignedShort(OpenTypeScriptOffsets.defaultLangSys);
		if (defaultLangSysOffset != 0) {
			defaultLangSys = new LangSysTable(raf, offset + defaultLangSysOffset, LanguageTag.DFLT.tag);
		}

		int langSysCount = readUnsignedShort(OpenTypeScriptOffsets.langSysCount);
		for (int i = 0; i < langSysCount; i++) {
			int tag = readUnsignedIntAsInt(OpenTypeScriptOffsets.langSysRecords.offset + i * 6);
			int langSysOffset = readUnsignedShort(OpenTypeScriptOffsets.langSysRecords.offset + 4 + i * 6);
			langSysByTag.put(tag, new LangSysTable(raf, offset + langSysOffset, tag));
		}
	}

	public LangSysTable getDefaultLangSys() {
		return defaultLangSys;
	}

	public LangSysTable getLangSys(LanguageTag languageTag) {
		return langSysByTag.get(languageTag.tag);
	}

	public LangSysTable getLangSysOrDefault(LanguageTag languageTag) {
		return langSysByTag.get(languageTag.tag, defaultLangSys);
	}

	private enum OpenTypeScriptOffsets implements Offset {
		defaultLangSys(0), langSysCount(2), langSysRecords(4);

		private final int offset;

		private OpenTypeScriptOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
