package com.gurella.engine.graphics.vector.sfnt.cff;

public class CffPrivateDict  extends CffDict {
	int privateDictOffset;
	
	public CffPrivateDict(byte[] valueData, int privateDictOffset) {
		super(valueData);
		this.privateDictOffset = privateDictOffset;
	}
	
	@Override
	CffDictEntryType[] getDictEntryTypes() {
		return CffPrivateDictEntryType.values();
	}
	
	public float[] getBlueValues() {
		return deltaValue(CffPrivateDictEntryType.blueValues.id);
	}

	public float[] getOtherBlues() {
		return deltaValue(CffPrivateDictEntryType.otherBlues.id);
	}

	public float[] getFamilyBlues() {
		return deltaValue(CffPrivateDictEntryType.familyBlues.id);
	}

	public float[] getFamilyOtherBlues() {
		return deltaValue(CffPrivateDictEntryType.familyOtherBlues.id);
	}

	public Number getBlueScale() {
		return numberValue(CffPrivateDictEntryType.blueScale.id);
	}

	public Number getBlueShift() {
		return numberValue(CffPrivateDictEntryType.blueShift.id);
	}

	public Number getBlueFuzz() {
		return numberValue(CffPrivateDictEntryType.blueFuzz.id);
	}

	public Number getStdHW() {
		return numberValue(CffPrivateDictEntryType.stdHW.id);
	}

	public Number getStdVW() {
		return numberValue(CffPrivateDictEntryType.stdVW.id);
	}

	public float[] getStemSnapH() {
		return deltaValue(CffPrivateDictEntryType.stemSnapH.id);
	}

	public float[] getStemSnapV() {
		return deltaValue(CffPrivateDictEntryType.stemSnapV.id);
	}

	public Boolean getForceBold() {
		return booleanValue(CffPrivateDictEntryType.forceBold.id);
	}

	public Number getLanguageGroup() {
		return numberValue(CffPrivateDictEntryType.languageGroup.id);
	}

	public Number getExpansionFactor() {
		return numberValue(CffPrivateDictEntryType.expansionFactor.id);
	}

	public Number getInitialRandomSeed() {
		return numberValue(CffPrivateDictEntryType.initialRandomSeed.id);
	}

	public Integer getSubrs() {
		return integerValue(CffPrivateDictEntryType.subrs.id);
	}

	public Number getDefaultWidthX() {
		return numberValue(CffPrivateDictEntryType.defaultWidthX.id);
	}

	public Number getNominalWidthX() {
		return numberValue(CffPrivateDictEntryType.nominalWidthX.id);
	}
	
	enum CffPrivateDictEntryType implements CffDictEntryType {
		blueValues(6 , new Number[0]),
		otherBlues(7 , new Number[0]),
		familyBlues(8 , new Number[0]),
		familyOtherBlues(9 , new Number[0]),
		blueScale(1209 , new Number[]{new Float(0.039625f)}),
		blueShift(1210 , new Number[]{new Integer(7)}),
		blueFuzz(1211 , new Number[]{new Integer(1)}),
		stdHW(10 , new Number[0]),
		stdVW(11 , new Number[0]),
		stemSnapH(1212 , new Number[0]),
		stemSnapV(1213 , new Number[0]),
		forceBold(1214 , new Number[]{new Integer(0)}),
		languageGroup(1217 , new Number[]{new Integer(0)}),
		expansionFactor(1218 , new Number[]{new Float(0.06f)}),
		initialRandomSeed(1219 , new Number[]{new Integer(0)}),
		subrs(19 , new Number[0]),
		defaultWidthX(20 , new Number[]{new Integer(0)}),
		nominalWidthX(21 , new Number[]{new Integer(0)});

		public final int id;
		public final Number[] defaultValue;

		CffPrivateDictEntryType(int id, Number[] defaultValue) {
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
