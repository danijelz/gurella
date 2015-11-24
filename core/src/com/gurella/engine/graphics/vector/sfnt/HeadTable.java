package com.gurella.engine.graphics.vector.sfnt;

import java.util.EnumSet;

public class HeadTable extends SfntTable {
	HeadTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public HeadTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}
	
	public float getVersion() {
		return readFixed(HeadOffsets.version);
	}

	public float getFontRevision() {
		return readFixed(HeadOffsets.fontRevision);
	}

	public long getCheckSumAdjustment() {
		return readUnsignedInt(HeadOffsets.checkSumAdjustment);
	}

	public long getMagicNumber() {
		return readUnsignedInt(HeadOffsets.magicNumber);
	}
	
	public EnumSet<Flags> getFlags() {
		return Flags.asSet(readUnsignedShort(HeadOffsets.flags));
	}
	
	public int getUnitsPerEm() {
		return readUnsignedShort(HeadOffsets.unitsPerEm);
	}

	public long getCreated() {
		return readLong(HeadOffsets.created);
	}

	public long getModified() {
		return readLong(HeadOffsets.modified);
	}

	public short getxMin() {
		return readShort(HeadOffsets.xMin);
	}

	public short getyMin() {
		return readShort(HeadOffsets.yMin);
	}

	public short getxMax() {
		return readShort(HeadOffsets.xMax);
	}

	public short getyMax() {
		return readShort(HeadOffsets.yMax);
	}
	
	public EnumSet<MacStyle> getMacStyle() {
		return MacStyle.asSet(readUnsignedShort(HeadOffsets.macStyle));
	}
	
	public int getLowestRecPPEM() {
		return readUnsignedShort(HeadOffsets.lowestRecPPEM);
	}
	
	public FontDirectionHint getFontDirectionHint() {
		return FontDirectionHint.valueOf(readShort(HeadOffsets.fontDirectionHint));
	}
	
	public IndexToLocFormat getIndexToLocFormat() {
		return IndexToLocFormat.valueOf(readShort(HeadOffsets.indexToLocFormat));
	}

	public short getGlyphDataFormat() {
		return readShort(HeadOffsets.glyphDataFormat);
	}
	
	public enum Flags {
	    BaselineAtY0,
	    LeftSidebearingAtX0,
	    InstructionsDependOnPointSize,
	    ForcePPEMToInteger,
	    InstructionsAlterAdvanceWidth,
	    Vertical,
	    Zero,
	    RequiresLayout,
	    GXMetamorphosis,
	    StrongRTL,
	    IndicRearrangement,
	    FontDataLossless,
	    FontConverted,
	    OptimizedForClearType,
	    lastResort,
	    Reserved15;

		public int mask() {
			return 1 << ordinal();
		}

		public static EnumSet<Flags> asSet(int value) {
			EnumSet<Flags> set = EnumSet.noneOf(Flags.class);
			for (Flags flag : Flags.values()) {
				if (flag != Reserved15 && ((value & flag.mask()) != 0)) {
					set.add(flag);
				}
			}
			return set;
		}
	}
	
	public enum MacStyle {
	    Bold,
	    Italic,
	    Underline,
	    Outline,
	    Shadow,
	    Condensed,
	    Extended,
	    Reserved7,
	    Reserved8,
	    Reserved9,
	    Reserved10,
	    Reserved11,
	    Reserved12,
	    Reserved13,
	    Reserved14,
	    Reserved15;
	    
	    private static final EnumSet<MacStyle> reserved = EnumSet.range(MacStyle.Reserved7, MacStyle.Reserved15);

		public int mask() {
			return 1 << ordinal();
		}

		public static EnumSet<MacStyle> asSet(int value) {
			EnumSet<MacStyle> set = EnumSet.noneOf(MacStyle.class);
			for (MacStyle style : MacStyle.values()) {
				if (!reserved.contains(style) && ((value & style.mask()) != 0)) {
					set.add(style);
				}
			}
			return set;
		}
	}
	
	public enum FontDirectionHint {
	    FullyMixed(0),
	    OnlyStrongLTR(1),
	    StrongLTRAndNeutral(2),
	    OnlyStrongRTL(-1),
	    StrongRTLAndNeutral(-2);

	    public final int value;

	    private FontDirectionHint(int value) {
	      this.value = value;
	    }

	    public boolean equals(int value) {
	      return value == this.value;
	    }

	    public static FontDirectionHint valueOf(int value) {
	      for (FontDirectionHint hint : FontDirectionHint.values()) {
	        if (hint.equals(value)) {
	          return hint;
	        }
	      }
	      return null;
	    }
	  }
	
	 public enum IndexToLocFormat {
		shortOffset(0),
		longOffset(1);

		private final int value;

		private IndexToLocFormat(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static IndexToLocFormat valueOf(int value) {
			for (IndexToLocFormat format : IndexToLocFormat.values()) {
				if (format.equals(value)) {
					return format;
				}
			}
			return null;
		}
	}
	
	private enum HeadOffsets implements Offset {
	    version(0),
	    fontRevision(4),
	    checkSumAdjustment(8),
	    magicNumber(12),
	    flags(16),
	    unitsPerEm(18),
	    created(20),
	    modified(28),
	    xMin(36),
	    yMin(38),
	    xMax(40),
	    yMax(42),
	    macStyle(44),
	    lowestRecPPEM(46),
	    fontDirectionHint(48),
	    indexToLocFormat(50),
	    glyphDataFormat(52);

		private final int offset;

		private HeadOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
