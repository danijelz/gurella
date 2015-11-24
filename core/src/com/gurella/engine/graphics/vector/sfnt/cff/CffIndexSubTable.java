package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.Array;

abstract class CffIndexSubTable<T> extends CffSubTable {
	protected int count;
	protected short offSize;
	protected int[] offsets;
	protected int dataOffset;
	protected Array<T> values;
	
	CffIndexSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}
	
	@Override
	protected void init() {
		super.init();
		
		values = new Array<T>();
		
		count = raf.readUnsignedShort();
		if(count == 0) {
			tableEndOffset = offset + 2;
			return;
		}
		
		offSize = raf.readUnsignedByte();
		offsets = new int[count + 1];
		for(int i = 0; i <= count; i++){
			if(offSize == 1) {
				offsets[i] = raf.readUnsignedByte() - 1;
			} else if(offSize == 2) {
				offsets[i] = raf.readUnsignedShort() - 1;
			} else if(offSize == 3) {
				offsets[i] = raf.readUnsignedInt24() - 1;
			} else if(offSize == 4) {
				offsets[i] = (int) raf.readUnsignedInt() - 1;
			}
		}
		
		dataOffset = raf.getPosition();
		tableEndOffset = dataOffset + offsets[count];
		for (int i = 0; i < count; i++) {
	        int valueOffset = dataOffset + offsets[i];
			int nextdataOffset = dataOffset + offsets[i + 1];
			raf.setPosition(valueOffset);
			//TODO this should not be initialized until needed
			byte[] valueData = raf.readBytes(nextdataOffset - valueOffset);
			values.add(createValue(valueData));
	    }
	}
	
	abstract T createValue(byte[] valueData);
}
