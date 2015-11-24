package com.gurella.engine.graphics.vector.sfnt.cff;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class VorgTable extends SfntTable {
	private static int vertOriginYMetricTableSize = SfntDataType.unsignedShortValue.size + SfntDataType.shortValue.size;
	
	public VorgTable(TrueTypeTableDirectory descriptor, int tag, long checkSum, int offset, int length) {
		super(descriptor, offset, tag, checkSum, length);
	}
	
	public VorgTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}
	
	public int getMajorVersion() {
		return readUnsignedShort(VorgOffsets.majorVersion);
	}

	public int getMinorVersion() {
		return readUnsignedShort(VorgOffsets.minorVersion);
	}

	public short getDefaultVertOriginY() {
		return readShort(VorgOffsets.defaultVertOriginY);
	}

	private int getNumVertOriginYMetrics() {
		return readUnsignedShort(VorgOffsets.numVertOriginYMetrics);
	}
	
	public short getVertOriginY(int glyphId) {
		int lo = 0;
        int hi = getNumVertOriginYMetrics() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midGlyphId = getGlyphId(mid);
            
			if(glyphId < midGlyphId) {
				hi = mid - 1;
			} else if (glyphId > midGlyphId) {
				lo = mid + 1;
			} else {
				return readVertOriginY(mid);
			}
        }
        return getDefaultVertOriginY();
	}

	private int getGlyphId(int index) {
		return readUnsignedShort(VorgOffsets.vertOriginYMetrics.offset + index * vertOriginYMetricTableSize);
	}
	
	private short readVertOriginY(int index) {
		return readShort(VorgOffsets.vertOriginYMetrics.offset + index * vertOriginYMetricTableSize + SfntDataType.unsignedShortValue.size);
	}

	private enum VorgOffsets implements Offset {
		majorVersion(0),
		minorVersion(4),
		defaultVertOriginY(6),
		numVertOriginYMetrics(8),
		vertOriginYMetrics(10);

		private final int offset;

		private VorgOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
