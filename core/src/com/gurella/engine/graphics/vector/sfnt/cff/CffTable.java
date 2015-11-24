package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.IntIntMap;
import com.gurella.engine.graphics.vector.Font.Glyph;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntGlyph;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;

public class CffTable extends SfntTable {
	CffHeaderSubTable cffHeaderTable;
	CffNameIndexSubTable nameTable;
	CffTopDictIndexSubTable topDictIndexTable;
	CffStringIndexSubTable stringIndexTable;
	CffSubrIndexSubTable globalSubrIndexTable;
	CffPrivateDict privateDict;
	CffSubrIndexSubTable privateSubrIndexTable;
	CffCharStringsIndexSubTable charStringsIndexTable;
	CffCharsetSubTable charsetTable;
	
	private IntIntMap encoding = new IntIntMap();
	private CffGlyphOutlineParser glyphOutlineParser;

	public CffTable(TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(directoryTable, offset, tag, checkSum, length);
	}
	
	public CffTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public void init() {
		raf.setPosition(offset);

		parseHeaderTable();
		parseNameTable();
		parseTopDictTable();
		parseStringTable();
		parseGlobalSubrTable();
		parsePrivateDict();
		parsePrivateSubrTable();
		parseCharStrings();
		parseCharset();
		parseEncoding();
		
		glyphOutlineParser = new CffGlyphOutlineParser(this);
	}

	private void parseHeaderTable() {
		cffHeaderTable = new CffHeaderSubTable(this, offset);
	}
	
	private void parseNameTable() {
		nameTable = new CffNameIndexSubTable(this, cffHeaderTable.tableEndOffset);
	}
	
	private void parseTopDictTable() {
		topDictIndexTable = new CffTopDictIndexSubTable(this, nameTable.tableEndOffset);
	}
	
	private void parseStringTable() {
		stringIndexTable = new CffStringIndexSubTable(this, topDictIndexTable.tableEndOffset);
	}
	
	private void parseGlobalSubrTable() {
		globalSubrIndexTable = new CffSubrIndexSubTable(this, stringIndexTable.tableEndOffset);
	}
	
	private void parsePrivateDict() {
		Integer[] privateDictRange = topDictIndexTable.privateDict();
		int privateDictSize = privateDictRange[0].intValue();
		int privateDictOffset = offset + privateDictRange[1].intValue();
		raf.setPosition(privateDictOffset);
		privateDict = new CffPrivateDict(raf.readBytes(privateDictSize), privateDictOffset);
	}
	
	private void parsePrivateSubrTable() {
		Integer privateSubrsOffset = privateDict.getSubrs();
		if(privateSubrsOffset != null) {
			privateSubrIndexTable = new CffSubrIndexSubTable(this, privateDict.privateDictOffset + privateSubrsOffset);
		}
	}
	
	private void parseCharStrings() {
		Integer charStringsDictEntry = topDictIndexTable.charStrings();
		int charStringsOffset = offset + charStringsDictEntry.intValue();
		charStringsIndexTable = new CffCharStringsIndexSubTable(this, charStringsOffset);
	}
	
	private void parseCharset() {
		Integer charsetDictEntry = topDictIndexTable.charset();
		int charsetOffset = offset + charsetDictEntry.intValue();
		charsetTable = new CffCharsetSubTable(this, charsetOffset);
	}
	
	private void parseEncoding() {
		int encodingDictEntryValue = topDictIndexTable.encoding().intValue();
		
		switch (encodingDictEntryValue) {
		case 0:
			parseSimpleEncoding(CffConstants.cffStandardEncoding);
			break;
		case 1:
			parseSimpleEncoding(CffConstants.cffExpertEncoding);
			break;
		default:
			parseMappedEncoding(encodingDictEntryValue);
			break;
		}
	}
	
	private void parseSimpleEncoding(String[] names) {
		for(int i = 0; i < names.length; i++) {
			String name = names[i];
			if(name.isEmpty()) {
				encoding.put(i, 0);
			} else {
				encoding.put(i, CffConstants.stringIndex(name));
			}
		}
	}

	private void parseMappedEncoding(int encodingOffset) {
		raf.setPosition(offset + encodingOffset);
		short format = raf.readUnsignedByte();
		
		switch (format) {
		case 0:
			int nCodes = raf.readUnsignedByte();
		    for (int i = 0; i < nCodes; i++) {
		        int code = raf.readUnsignedByte();
		        encoding.put(code, i);
		    }
			break;
		case 1:
			int nRanges = raf.readUnsignedByte();
		    int code = 1;
		    for (int i = 0; i < nRanges; i++) {
		        int first = raf.readUnsignedByte();
		        int nLeft = raf.readUnsignedByte();
		        for (int j = first; j <= first + nLeft; j += 1) {
		        	encoding.put(j, code);
		            code++;
		        }
		    }
			break;
		default:
			throw new IllegalArgumentException("Unknown encoding format: " + format);
		}
	}
	
	Glyph createGlyph(int glyphId) {
		return new SfntGlyph(glyphId, glyphOutlineParser.createOutline(glyphId));
	}
}
