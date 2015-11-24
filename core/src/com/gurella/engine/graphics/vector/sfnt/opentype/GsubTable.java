package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;
import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable.OpenTypeLookupSubTableFactory;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.alternate.AlternateSubstitutionSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.ligature.LigatureSubstitutionSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.multiple.MultipleSubstitutionSubtable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.single.SingleSubstitutionSubtable;

public class GsubTable extends OpenTypeLayoutTable {
	public GsubTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, tag, checkSum, offset, length);
	}
	
	public GsubTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, tag, checkSum, offset, length);
	}

	@Override
	protected OpenTypeLookupSubTableFactory getLookupSubTableFactory() {
		return GsubOpenTypeLookupSubTableFactory.instance;
	}

	private static class GsubOpenTypeLookupSubTableFactory implements OpenTypeLookupSubTableFactory {
		static final GsubOpenTypeLookupSubTableFactory instance = new GsubOpenTypeLookupSubTableFactory();

		@Override
		public LookupSubTable create(int lookupType, OpenTypeLookupTable openTypeLookupTable, int offset) {
			switch (lookupType) {
			case 1:
				return new SingleSubstitutionSubtable(openTypeLookupTable, offset);
			case 2:
				return new MultipleSubstitutionSubtable(openTypeLookupTable, offset);
			case 3:
				return new AlternateSubstitutionSubtable(openTypeLookupTable, offset);
			case 4:
				return new LigatureSubstitutionSubtable(openTypeLookupTable, offset);
				//TODO other lookupTypes
			case 7:
				RandomAccessFile raf = openTypeLookupTable.raf;
				raf.setPosition(offset + SfntDataType.unsignedShortValue.size);
				int extensionLookupType = raf.readUnsignedShort();
				int extensionOffset = raf.readUnsignedIntAsInt();
				return create(extensionLookupType, openTypeLookupTable, extensionOffset);
				//TODO other lookupTypes
			default:
				return new LookupSubTable(openTypeLookupTable, offset);
			}
		}
	}
}
