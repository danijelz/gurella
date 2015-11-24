package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.Array;

class CffCharsetSubTable extends CffSubTable {
	Array<String> charsets;
	
	CffCharsetSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}

	@Override
	protected void init() {
		super.init();
		charsets = new Array<String>();
		int nGlyphs = parentTable.parentTable.getNumGlyphs() -1;
		charsets.add(".notdef");
		
		short format = raf.readUnsignedByte();
		switch (format) {
		case 0:
			for (int i = 1; i < nGlyphs; i++) {
	            int sid = raf.readUnsignedShort() & 0xff;
	            charsets.add(getCffString(sid));
	        }
			break;
		case 1:
			while (charsets.size <= nGlyphs) {
	            int sid = raf.readUnsignedShort() & 0xff;
	            int count = raf.readUnsignedByte() & 0xff;
	            for (int i = 0; i <= count; i += 1) {
	                charsets.add(getCffString(sid));
	                sid ++;
	            }
	        }
			break;
		case 2:
			while (charsets.size <= nGlyphs) {
	        	int sid = raf.readUnsignedShort() & 0xff;
	        	int count = raf.readUnsignedShort() & 0xff;
	            for (int i = 0; i <= count; i += 1) {
	                charsets.add(getCffString(sid));
	                sid ++;
	            }
	        }
			break;
		default:
			throw new IllegalArgumentException("Unknown charset format: " + format);
		}
	}
	
	private String getCffString(int sid) {
		if (sid <= 390) {
			return CffConstants.cffStandardStrings.get(sid);
	    } else {
	    	return parentTable.stringIndexTable.values.get(sid - 391);
	    }
	}
}
