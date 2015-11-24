package com.gurella.engine.graphics.vector.sfnt.truetype;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.vector.sfnt.PlatformId;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntEncodings;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.SfntLanguages.LanguageId;

public class NameTable extends SfntTable {
	private static final byte nameRecordSize = 12;
	
	private final IntMap<String> namesByIndex = new IntMap<String>();
	
	public NameTable(TableDirectory headerTable, int tag, long checkSum, int offset, int length) {
		super(headerTable, offset, tag, checkSum, length);
		System.out.println(findName(NameId.FullFontName));
		System.out.println(findName(NameId.FontFamilyName));
		System.out.println(findName(NameId.FontSubfamilyName));
		System.out.println(findName(NameId.PostscriptName));
		System.out.println("\n\n");
	}
	
	public NameTable(RandomAccessFile raf, TableDirectory headerTable, int tag, long checkSum, int offset, int length) {
		super(raf, headerTable, offset, tag, checkSum, length);
		System.out.println(findName(NameId.FullFontName));
		System.out.println(findName(NameId.FontFamilyName));
		System.out.println(findName(NameId.FontSubfamilyName));
		System.out.println(findName(NameId.PostscriptName));
		System.out.println("\n\n");
	}
	
	public int getFormat() {
		return readUnsignedShort(NameFormat0Offsets.format);
	}
	
	public int getCount() {
		return readUnsignedShort(NameFormat0Offsets.count);
	}
	
	private int getStringOffset() {
		return readUnsignedShort(NameFormat0Offsets.stringOffset);
	}
	
	private String getName(final int index, final int platformId, final int encodingId) {
		if(namesByIndex.containsKey(index)) {
			return namesByIndex.get(index);
		}
		
		int nameRecordOffset = NameFormat0Offsets.nameRecords.offset + (index * nameRecordSize);
		if(nameRecordOffset < 0) {
			return null;
		}
		
		String encoding = SfntEncodings.getEncoding(platformId, encodingId);
		int nameOffset = getStringOffset() + readUnsignedShort(nameRecordOffset + NameRecordOffsets.nameOffset.offset);
		int nameLength = readUnsignedShort(nameRecordOffset + NameRecordOffsets.length.offset);
		String name = readString(nameOffset, nameLength, encoding);
		namesByIndex.put(index, name);
		return name;
	}
	
	public String findName(final PlatformId platformId, final LanguageId languageId, final NameId nameId) {
		return findName(platformId.value(), languageId.value(), nameId.value());
	}
	
	public String findName(final int platformId, final int languageId, final int nameId) {
		for(int i = 0; i < getCount(); i++) {
			int nameRecordOffset = NameFormat0Offsets.nameRecords.offset + (i * nameRecordSize);
			
			int recordNameId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.nameID.offset);
			if(nameId != recordNameId) {
				continue;
			}
			
			int recordPlatformId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.platformID.offset);
			if(platformId != recordPlatformId) {
				continue;
			}
			
			int recordLanguageId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.languageID.offset);
			if(languageId != recordLanguageId) {
				continue;
			}
			
			int encodingId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.encodingID.offset);
			return getName(i, platformId, encodingId);
		}
		
		return null;
	}
	
	public String findName(final NameId nameId) {
		return findName(nameId.value());
	}
	
	public String findName(final int nameId) {
		int foundIndex = -1;
		int foundPlatformId = Integer.MAX_VALUE;
		int foundLanguageId = Integer.MAX_VALUE;
		int encodingId = 0;
		
		for(int i = 0; i < getCount(); i++) {
			int nameRecordOffset = NameFormat0Offsets.nameRecords.offset + (i * nameRecordSize);
			
			int recordNameId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.nameID.offset);
			if(nameId != recordNameId) {
				continue;
			}
			
			int recordPlatformId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.platformID.offset);
			int recordLanguageId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.languageID.offset);
			if((recordPlatformId < foundPlatformId) || (recordPlatformId == foundPlatformId && recordLanguageId < foundLanguageId)) {
				foundIndex = i;
				foundPlatformId = recordPlatformId;
				foundLanguageId = recordLanguageId;
				encodingId = readUnsignedShort(nameRecordOffset + NameRecordOffsets.encodingID.offset);
			}
		}
		
		return foundIndex < 0 ? null : getName(foundIndex, foundPlatformId, encodingId);
	}
	
	private enum NameFormat0Offsets implements Offset {
		format(0),
		count(2),
		stringOffset(4),
		nameRecords(6);

		private final int offset;

		private NameFormat0Offsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	//TODO
	private enum NameFormat1Offsets implements Offset {
		langTagCount(0),
		langTagRecords(2);

		private final int offset;

		private NameFormat1Offsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum NameRecordOffsets implements Offset {
		platformID(0),
		encodingID(2),
		languageID(4),
		nameID(6),
		length(8),
		nameOffset(10);

		private final int offset;

		private NameRecordOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	public enum NameId {
	    Unknown(-1),
	    CopyrightNotice(0),
	    FontFamilyName(1),
	    FontSubfamilyName(2),
	    UniqueFontIdentifier(3),
	    FullFontName(4),
	    VersionString(5),
	    PostscriptName(6),
	    Trademark(7),
	    ManufacturerName(8),
	    Designer(9),
	    Description(10),
	    VendorURL(11),
	    DesignerURL(12),
	    LicenseDescription(13),
	    LicenseInfoURL(14),
	    Reserved15(15),
	    PreferredFamily(16),
	    PreferredSubfamily(17),
	    CompatibleFullName(18),
	    SampleText(19),
	    PostscriptCID(20),
	    WWSFamilyName(21),
	    WWSSubfamilyName(22),
	    LightBackgoundPalette(23),
	    DarkBackgoundPalette(24);
	    
	    private static IntMap<NameId> valuesById;

		public final int value;

		private NameId(int value) {
			this.value = value;
			getValuesById().put(value, this);
		}
		
		private static IntMap<NameId> getValuesById() {
			if(valuesById== null) {
				valuesById = new IntMap<NameId>();
			}
			return valuesById;
		}

		public int value() {
			return this.value;
		}

		public boolean equals(int value) {
			return value == this.value;
		}

		public static NameId valueOf(int value) {
			return valuesById.get(value, Unknown);
		}
	}
}
