package com.gurella.engine.graphics.vector.sfnt.cff;

public class CffTopDict extends CffDict {
	public CffTopDict(byte[] valueData) {
		super(valueData);
	}

	@Override
	CffDictEntryType[] getDictEntryTypes() {
		return CffTopDictEntryType.values();
	}
	
	public Integer versionSID() {
		return sidValue(CffTopDictEntryType.version.id);
	}

	public Integer noticeSID() {
		return sidValue(CffTopDictEntryType.notice.id);
	}

	public Integer copyrightSID() {
		return sidValue(CffTopDictEntryType.copyright.id);
	}

	public Integer fullNameSID() {
		return sidValue(CffTopDictEntryType.fullName.id);
	}

	public Integer familyNameSID() {
		return sidValue(CffTopDictEntryType.familyName.id);
	}

	public Integer weightSID() {
		return sidValue(CffTopDictEntryType.weight.id);
	}

	public Boolean isFixedPitch() {
		return booleanValue(CffTopDictEntryType.isFixedPitch.id);
	}

	public Float italicAngle() {
		return floatValue(CffTopDictEntryType.italicAngle.id);
	}

	public Number underlinePosition() {
		return numberValue(CffTopDictEntryType.underlinePosition.id);
	}

	public Number underlineThickness() {
		return numberValue(CffTopDictEntryType.underlineThickness.id);
	}

	public Number paintType() {
		return numberValue(CffTopDictEntryType.paintType.id);
	}

	public Number charstringType() {
		return numberValue(CffTopDictEntryType.charstringType.id);
	}

	public float[] fontMatrix() {
		return floatArrayValue(CffTopDictEntryType.fontMatrix.id);
	}

	public Number uniqueID() {
		return numberValue(CffTopDictEntryType.uniqueID.id);
	}

	public float[] fontBBox() {
		return floatArrayValue(CffTopDictEntryType.fontBBox.id);
	}

	public Number strokeWidth() {
		return numberValue(CffTopDictEntryType.strokeWidth.id);
	}

	public float[] xuid() {
		return floatArrayValue(CffTopDictEntryType.XUID.id);
	}

	public Integer charset() {
		return integerValue(CffTopDictEntryType.charset.id);
	}

	public Integer encoding() {
		return integerValue(CffTopDictEntryType.encoding.id);
	}

	public Integer charStrings() {
		return integerValue(CffTopDictEntryType.charStrings.id);
	}
	
	public Integer[] privateDict() {
		return rangeValue(CffTopDictEntryType.privateDict.id);
	}

	public Number syntheticBase() {
		return numberValue(CffTopDictEntryType.syntheticBase.id);
	}

	public Integer postscriptSID() {
		return sidValue(CffTopDictEntryType.postScript.id);
	}

	public Integer baseFontNameSID() {
		return sidValue(CffTopDictEntryType.baseFontName.id);
	}

	public float[] baseFontBlend() {
		return deltaValue(CffTopDictEntryType.baseFontBlend.id);
	}
	
	enum CffTopDictEntryType implements CffDictEntryType {
		version(0 , new Number[0]),
		notice(1 , new Number[0]),
		copyright(1200 , new Number[0]),
		fullName(2 , new Number[0]),
		familyName(3 , new Number[0]),
		weight(4 , new Number[0]),
		isFixedPitch(1201 , new Number[]{new Integer(0)}),
		italicAngle(1202 , new Number[]{new Integer(0)}),
		underlinePosition(1203 , new Number[]{new Integer(-100)}),
		underlineThickness(1204 , new Number[]{new Integer(50)}),
		paintType(1205 , new Number[]{new Integer(0)}),
		charstringType(1206 , new Number[]{new Integer(2)}),
		fontMatrix(1207 , new Number[]{new Float(0.001f), new Float(0), new Float(0), new Float(0.001f), new Float(0), new Float(0)}),
		uniqueID(13 , new Number[0]),
		fontBBox(5 , new Number[]{new Integer(0), new Integer(0), new Integer(0), new Integer(0)}),
		strokeWidth(1208 , new Number[]{new Integer(0)}),
		XUID(14 , new Number[0]),
		charset(15 , new Number[]{new Integer(0)}),
		encoding(16 , new Number[]{new Integer(0)}),
		charStrings(17 , new Number[]{new Integer(0)}),
		privateDict(18 , new Number[]{new Integer(0), new Integer(0)}),
		syntheticBase(1220 , new Number[0]),
		postScript(1221 , new Number[0]),
		baseFontName(1222 , new Number[0]),
		baseFontBlend(1223 , new Number[0]),
		ROS(1230 , new Number[0]),
		CIDFontVersion(1231 , new Number[]{new Integer(0)}),
		CIDFontRevision(1232 , new Number[]{new Integer(0)}),
		CIDFontType(1233 , new Number[]{new Integer(0)}),
		CIDCount(1234 , new Number[]{new Integer(8720)}),
		UIDBase(1235 , new Number[0]),
		FDArray(1236 , new Number[0]),
		FDSelect(1237 , new Number[0]),
		fontName(1238 , new Number[0]);
		
		public final int id;
		public final Number[] defaultValue;
		
		CffTopDictEntryType(int id, Number[] defaultValue) {
			this.id = id;
			this.defaultValue = defaultValue;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Number[] getDefaultValue() {
			return defaultValue;
		}
	}
}
