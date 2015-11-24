package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;
import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable.OpenTypeLookupSubTableFactory;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.cursive.CursiveAttachmentPositioningSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktobase.MarkToBaseAttachmentPositioningSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktoligature.MarkToLigatureAttachmentPositioningSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktomark.MarkToMarkAttachmentPositioningSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.pair.PairAdjustmentPositioningSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.single.SingleAdjustmentPositioningSubtable;

public class GposTable extends OpenTypeLayoutTable {
	public GposTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, tag, checkSum, offset, length);
	}
	
	public GposTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, tag, checkSum, offset, length);
	}

	@Override
	protected OpenTypeLookupSubTableFactory getLookupSubTableFactory() {
		return GposOpenTypeLookupSubTableFactory.instance;
	}
	
	private static class GposOpenTypeLookupSubTableFactory implements OpenTypeLookupSubTableFactory {
		static final GposOpenTypeLookupSubTableFactory instance = new GposOpenTypeLookupSubTableFactory();
		
		@Override
		public LookupSubTable create(int lookupType, OpenTypeLookupTable openTypeLookupTable, int offset) {
			switch (lookupType) {
			case 1:
				return new SingleAdjustmentPositioningSubtable(openTypeLookupTable, offset);
			case 2:
				return new PairAdjustmentPositioningSubtable(openTypeLookupTable, offset);
			case 3:
				return new CursiveAttachmentPositioningSubtable(openTypeLookupTable, offset);
			case 4:
				return new MarkToBaseAttachmentPositioningSubtable(openTypeLookupTable, offset);
			case 5:
				return new MarkToLigatureAttachmentPositioningSubtable(openTypeLookupTable, offset);
			case 6:
				return new MarkToMarkAttachmentPositioningSubtable(openTypeLookupTable, offset);
				//TODO other cases
			case 9:
				RandomAccessFile raf = openTypeLookupTable.raf;
				raf.setPosition(offset + SfntDataType.unsignedShortValue.size);
				int extensionLookupType = raf.readUnsignedShort();
				int extensionOffset = raf.readUnsignedIntAsInt();
				return create(extensionLookupType, openTypeLookupTable, extensionOffset);
			default:
				return new LookupSubTable(openTypeLookupTable, offset);
			}
		}
	}
}
