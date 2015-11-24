package com.gurella.engine.graphics.vector.sfnt.cff;

class CffTopDictIndexSubTable extends CffIndexSubTable<CffTopDict> {
	CffTopDictIndexSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}

	@Override
	CffTopDict createValue(byte[] valueData) {
		return new CffTopDict(valueData);
	}

	public Integer versionSID() {
		return values.get(0).versionSID();
	}

	public Integer noticeSID() {
		return values.get(0).noticeSID();
	}

	public Integer copyrightSID() {
		return values.get(0).copyrightSID();
	}

	public Integer fullNameSID() {
		return values.get(0).fullNameSID();
	}

	public Integer familyNameSID() {
		return values.get(0).familyNameSID();
	}

	public Integer weightSID() {
		return values.get(0).weightSID();
	}

	public Boolean isFixedPitch() {
		return values.get(0).isFixedPitch();
	}

	public Float italicAngle() {
		return values.get(0).italicAngle();
	}

	public Number underlinePosition() {
		return values.get(0).underlinePosition();
	}

	public Number underlineThickness() {
		return values.get(0).underlineThickness();
	}

	public Number paintType() {
		return values.get(0).paintType();
	}

	public Number charstringType() {
		return values.get(0).charstringType();
	}

	public float[] fontMatrix() {
		return values.get(0).fontMatrix();
	}

	public Number uniqueID() {
		return values.get(0).uniqueID();
	}

	public float[] fontBBox() {
		return values.get(0).fontBBox();
	}

	public Number strokeWidth() {
		return values.get(0).strokeWidth();
	}

	public float[] xuid() {
		return values.get(0).xuid();
	}

	public Integer charset() {
		return values.get(0).charset();
	}

	public Integer encoding() {
		return values.get(0).encoding();
	}

	public Integer charStrings() {
		return values.get(0).charStrings();
	}

	public Integer[] privateDict() {
		return values.get(0).privateDict();
	}

	public Number syntheticBase() {
		return values.get(0).syntheticBase();
	}

	public Integer postscriptSID() {
		return values.get(0).postscriptSID();
	}

	public Integer baseFontNameSID() {
		return values.get(0).baseFontNameSID();
	}

	public float[] baseFontBlend() {
		return values.get(0).baseFontBlend();
	}
}
