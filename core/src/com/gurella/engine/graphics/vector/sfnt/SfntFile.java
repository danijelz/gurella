package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.vector.sfnt.opentype.DsigSubTable;
import com.gurella.engine.graphics.vector.sfnt.woff.WoffTableDirectory;

public class SfntFile extends Table {
	private static final int ttcTag = SfntTagUtils.tagToInt("ttcf");
	private static final int woffTag = SfntTagUtils.tagToInt("wOFF");
	private static final int woff2Tag = SfntTagUtils.tagToInt("wOF2");
	private static final int dsigExistsTag = SfntTagUtils.tagToInt("DSIG");
	
	private Array<SfntFont> fonts;
	private DsigSubTable dsigSubTable;
	
	public SfntFile(RandomAccessFile raf) {
		super(raf, 0);
		
		fonts = new Array<SfntFont>();
		if(isCollectionFontFile()) {
			for (int i = 0; i < getNumFonts(); i++) {
				fonts.add(createCollectionFont(i));
			}
		} else if(isWoffFontFile()) {
			fonts.add(new SfntFont(new WoffTableDirectory(raf, 0)));
		} else if(isWoff2FontFile()) {
			throw new IllegalArgumentException("wOF2 fonts not supported");
		} else {
			fonts.add(new SfntFont(new TrueTypeTableDirectory(raf, 0)));
		}
	}
	
	public boolean isCollectionFontFile() {
	    return ttcTag == readUnsignedIntAsInt(0);
	}
	
	public boolean isWoffFontFile() {
		return woffTag == readUnsignedIntAsInt(0);
	}
	
	public boolean isWoff2FontFile() {
		return woff2Tag == readUnsignedIntAsInt(0);
	}
	
	public float getTtcVersion() {
		return isCollectionFontFile() ? readFixed(TtcFileOffsets.version) : 0;
	}
	
	public int getNumFonts() {
		return isCollectionFontFile() ? readUnsignedIntAsInt(TtcFileOffsets.numFonts) : 1;
	}
	
	private SfntFont createCollectionFont(int index) {
		int fontTableOffset = TtcFileOffsets.offsetTable.offset + index * 4;
		return new SfntFont(new TrueTypeTableDirectory(raf, readUnsignedIntAsInt(fontTableOffset)));
	}
	
	public boolean hasDsigTable() {
		if(isCollectionFontFile() && getTtcVersion() > 1) {
			int dsigTag = readUnsignedIntAsInt(getDsigInfoOffset());
			return dsigExistsTag == dsigTag; 
		} else {
			return false;
		}
	}
	
	public DsigSubTable getDsigTable() {
		if(dsigSubTable == null && hasDsigTable()) {
			int dsigSubTableOffset = readUnsignedIntAsInt(getDsigInfoOffset() + TtcFileOffsets.ulDsigOffset.offset);
			dsigSubTable = new DsigSubTable(this, dsigSubTableOffset);
		}
		return dsigSubTable;
	}

	private int getDsigInfoOffset() {
		return TtcFileOffsets.offsetTable.offset + getNumFonts() * 4;
	}
	
	public Array<SfntFont> getFonts() {
		return fonts;
	}

	private enum TtcFileOffsets implements Offset {
		ttcTag(0),
		version(4),
		numFonts(8),
		offsetTable(12),
		//version 2.0
		ulDsigTag(0),
		ulDsigLength(4),
		ulDsigOffset(8);

		private final int offset;

		private TtcFileOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
