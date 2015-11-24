package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import java.util.EnumSet;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

//TODO older versions
public class Os2Table extends SfntTable {
	public Os2Table(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public Os2Table(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public int getVersion() {
		return readUnsignedShort(OS2Offset.version);
	}

	public short getAvgCharWidth() {
		return readShort(OS2Offset.xAvgCharWidth);
	}

	public WeightClass getWeightClass() {
		return WeightClass.valueOf(readUnsignedShort(OS2Offset.usWeightClass));
	}

	public WidthClass getWidthClass() {
		return WidthClass.valueOf(readUnsignedShort(OS2Offset.usWidthClass));
	}

	public EnumSet<EmbeddingFlags> getFsType() {
		return EmbeddingFlags.asSet(readUnsignedShort(OS2Offset.fsType));
	}

	public short getSubscriptXSize() {
		return readShort(OS2Offset.ySubscriptXSize);
	}

	public short getSubscriptYSize() {
		return readShort(OS2Offset.ySubscriptYSize);
	}

	public short getSubscriptXOffset() {
		return readShort(OS2Offset.ySubscriptXOffset);
	}

	public short getSubscriptYOffset() {
		return readShort(OS2Offset.ySubscriptYOffset);
	}

	public short getSuperscriptXSize() {
		return readShort(OS2Offset.ySuperscriptXSize);
	}

	public short getSuperscriptYSize() {
		return readShort(OS2Offset.ySuperscriptYSize);
	}

	public short getSuperscriptXOffset() {
		return readShort(OS2Offset.ySuperscriptXOffset);
	}

	public short getSuperscriptYOffset() {
		return readShort(OS2Offset.ySuperscriptYOffset);
	}

	public short getStrikeoutSize() {
		return readShort(OS2Offset.yStrikeoutSize);
	}

	public short getStrikeoutPosition() {
		return readShort(OS2Offset.yStrikeoutPosition);
	}

	FamilyClass getFamilyClass() {
		short sFamilyClass = readShort(OS2Offset.sFamilyClass);
		byte familyClassIndex = (byte) ((sFamilyClass >> 8) & 0xff);
		return FamilyClass.valueOf(familyClassIndex);
	}

	Enum<? extends FamilySubclass> getFamilySubclass() {
		short sFamilyClass = readShort(OS2Offset.sFamilyClass);
		byte familySubclassClassIndex = (byte) (sFamilyClass & 0xff);
		switch (getFamilyClass()) {
		case OldStyleSerifs:
			return OldStyleSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case TransitionalSerifs:
			return TransitionalSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case ModernSerifs:
			return ModernSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case ClarendonSerifs:
			return ClarendonSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case SlabSerifs:
			return SlabSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case FreeformSerifs:
			return FreeformSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case SansSerifs:
			return SansSerifsFamilySubclass.valueOf(familySubclassClassIndex);
		case Ornamentals:
			return OrnamentalsFamilySubclass.valueOf(familySubclassClassIndex);
		case Scripts:
			return ScriptsFamilySubclass.valueOf(familySubclassClassIndex);
		case Symbolic:
			return SymbolicFamilySubclass.valueOf(familySubclassClassIndex);
		default:
			return null;
		}
	}

	FamilyType getFamilyType() {
		byte bFamilyType = readByte(OS2Offset.panose);
		FamilyType[] values = FamilyType.values();
		return values.length < bFamilyType ? values[bFamilyType] : null;
	}

	SerifStyle getSerifStyle() {
		byte bSerifStyle = readByte(OS2Offset.panose.offset + 1);
		SerifStyle[] values = SerifStyle.values();
		return values.length < bSerifStyle ? values[bSerifStyle] : null;
	}

	Weight getWeight() {
		byte bWeight = readByte(OS2Offset.panose.offset + 2);
		Weight[] values = Weight.values();
		return values.length < bWeight ? values[bWeight] : null;
	}

	Proportion getProportion() {
		byte bProportion = readByte(OS2Offset.panose.offset + 3);
		Proportion[] values = Proportion.values();
		return values.length < bProportion ? values[bProportion] : null;
	}

	Contrast getContrast() {
		byte bContrast = readByte(OS2Offset.panose.offset + 4);
		Contrast[] values = Contrast.values();
		return values.length < bContrast ? values[bContrast] : null;
	}

	StrokeVariation getStrokeVariation() {
		byte bStrokeVariation = readByte(OS2Offset.panose.offset + 5);
		StrokeVariation[] values = StrokeVariation.values();
		return values.length < bStrokeVariation ? values[bStrokeVariation] : null;
	}

	ArmStyle getArmStyle() {
		byte bArmStyle = readByte(OS2Offset.panose.offset + 6);
		ArmStyle[] values = ArmStyle.values();
		return values.length < bArmStyle ? values[bArmStyle] : null;
	}

	Letterform getLetterform() {
		byte bLetterform = readByte(OS2Offset.panose.offset + 7);
		Letterform[] values = Letterform.values();
		return values.length < bLetterform ? values[bLetterform] : null;
	}

	Midline getMidline() {
		byte bMidline = readByte(OS2Offset.panose.offset + 8);
		Midline[] values = Midline.values();
		return values.length < bMidline ? values[bMidline] : null;
	}

	XHeight getXHeight() {
		byte bXHeight = readByte(OS2Offset.panose.offset + 9);
		XHeight[] values = XHeight.values();
		return values.length < bXHeight ? values[bXHeight] : null;
	}

	public EnumSet<UnicodeRange> getUnicodeRanges() {
		return UnicodeRange.asSet(readUnsignedInt(OS2Offset.ulUnicodeRange1), readUnsignedInt(OS2Offset.ulUnicodeRange2),
				readUnsignedInt(OS2Offset.ulUnicodeRange3), readUnsignedInt(OS2Offset.ulUnicodeRange4));
	}

	public long getUnicodeRange1() {
		return readUnsignedInt(OS2Offset.ulUnicodeRange1);
	}

	public long getUnicodeRange2() {
		return readUnsignedInt(OS2Offset.ulUnicodeRange2);
	}

	public long getUnicodeRange3() {
		return readUnsignedInt(OS2Offset.ulUnicodeRange3);
	}

	public long getUnicodeRange4() {
		return readUnsignedInt(OS2Offset.ulUnicodeRange4);
	}

	public String getAchVendId() {
		return readString(OS2Offset.achVendId, 4);
	}

	public EnumSet<FsSelection> getFsSelection() {
		return FsSelection.asSet(readUnsignedShort(OS2Offset.fsSelection));
	}

	public int getFirstCharIndex() {
		return readUnsignedShort(OS2Offset.usFirstCharIndex);
	}

	public int getLastCharIndex() {
		return readUnsignedShort(OS2Offset.usLastCharIndex);
	}

	public short getTypoAscender() {
		return getVersion() >= 1 ? readShort(OS2Offset.sTypoAscender) : 0;
	}

	public short getTypoDescender() {
		return getVersion() >= 1 ? readShort(OS2Offset.sTypoDescender) : 0;
	}

	public short getTypoLineGap() {
		return getVersion() >= 1 ? readShort(OS2Offset.sTypoLineGap) : 0;
	}

	public int getWinAscent() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usWinAscent) : 0;
	}

	public int getWinDescent() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usWinDescent) : 0;
	}

	public long getCodePageRange1() {
		return getVersion() >= 1 ? readUnsignedInt(OS2Offset.ulCodePageRange1) : -1;
	}

	public long getCodePageRange2() {
		return getVersion() >= 1 ? readUnsignedInt(OS2Offset.ulCodePageRange2) : -1;
	}

	public EnumSet<CodePageRange> getCodePageRange() {
		return getVersion() >= 1 ? CodePageRange.asSet(getCodePageRange1(), getCodePageRange2()) : EnumSet.noneOf(CodePageRange.class);
	}

	public short getHeight() {
		return getVersion() >= 1 ? readShort(OS2Offset.sxHeight) : -1;
	}

	public short getCapHeight() {
		return getVersion() >= 1 ? readShort(OS2Offset.sCapHeight) : -1;
	}

	public int getDefaultChar() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usDefaultChar) : 0;
	}

	public int getBreakChar() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usBreakChar) : -1;
	}

	public int getMaxContext() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usMaxContext) : -1;
	}

	public int getLowerOpticalPointSize() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usLowerOpticalPointSize) : -1;
	}

	public int getUpperOpticalPointSize() {
		return getVersion() >= 1 ? readUnsignedShort(OS2Offset.usUpperOpticalPointSize) : -1;
	}

	private enum OS2Offset implements Offset {
		version(0),
		xAvgCharWidth(2),
		usWeightClass(4),
		usWidthClass(6),
		fsType(8),
		ySubscriptXSize(10),
		ySubscriptYSize(12),
		ySubscriptXOffset(14),
		ySubscriptYOffset(16),
		ySuperscriptXSize(18),
		ySuperscriptYSize(20),
		ySuperscriptXOffset(22),
		ySuperscriptYOffset(24),
		yStrikeoutSize(26),
		yStrikeoutPosition(28),
		sFamilyClass(30),
		panose(32),
		ulUnicodeRange1(42),
		ulUnicodeRange2(46),
		ulUnicodeRange3(50),
		ulUnicodeRange4(54),
		achVendId(58),
		fsSelection(62),
		usFirstCharIndex(64),
		usLastCharIndex(66),
		sTypoAscender(68),
		sTypoDescender(70),
		sTypoLineGap(72),
		usWinAscent(74),
		usWinDescent(76),
		ulCodePageRange1(78),
		ulCodePageRange2(82),
		sxHeight(86),
		sCapHeight(88),
		usDefaultChar(90),
		usBreakChar(92),
		usMaxContext(94),
		usLowerOpticalPointSize(96),
		usUpperOpticalPointSize(98);

		private final int offset;

		private OS2Offset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
