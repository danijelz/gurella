package com.gurella.engine.graphics.vector.sfnt.truetype.glyf;

import static com.gurella.engine.graphics.vector.sfnt.truetype.glyf.GlyfTableConstants.*;

import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.ShortArray;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.sfnt.LocaTable;
import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntGlyph;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.SfntTableTag;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class GlyfTable extends SfntTable {
	private LocaTable locaTable;
	
	public GlyfTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public GlyfTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public void init() {
		locaTable = getTable(SfntTableTag.loca);
	}
	
	public SfntGlyph createGlyph(int glyphId) {
		return parse(glyphId);
	}
	
	private SfntGlyph parse(int glyphId) {
		int relativeGlyphOffset = locaTable.getOffset(glyphId);
		
		if (relativeGlyphOffset == locaTable.getOffset(glyphId + 1)) {
			return new SfntGlyph(glyphId, Path.emptyPath);
		}
		
		int glyphOffset = offset + locaTable.getOffset(glyphId);
		raf.setPosition(glyphOffset);
		short numberOfContours = raf.readShort();
		short xMin = raf.readShort();
		short yMin = raf.readShort();
		short xMax = raf.readShort();
		short yMax = raf.readShort();
		
		GlyfOtline outline = GlyfOtline.obtain();
		if (numberOfContours >= 0) {
			parseSimpleGlyphOutline(glyphOffset, numberOfContours, outline);
		} else {
			parseCompositeGlyphOutline(glyphOffset, numberOfContours, outline);
		}
		
		SfntGlyph glyph = new SfntGlyph(glyphId, outline.createOutlinePath());
		outline.free();
		return glyph;
	}

	private void parseOutline(int glyphId, GlyfOtline outline) {
		int glyphOffset = offset + locaTable.getOffset(glyphId);
		raf.setPosition(glyphOffset);
		short numberOfContours = raf.readShort();
		raf.setPosition(glyphOffset + GlyfOtlineOffset.glyfData.offset);
		
		if (numberOfContours >= 0) {
			parseSimpleGlyphOutline(glyphOffset, numberOfContours, outline);
		} else {
			parseCompositeGlyphOutline(glyphOffset, numberOfContours, outline);
		}
	}
	
	private void parseSimpleGlyphOutline(int glyphOffset, int numberOfContours, GlyfOtline outline) {
		for (int i = 0; i < numberOfContours; i++) {
			outline.endPointOfContours.add(raf.readUnsignedShort());
		}
		
		int pointCount =  outline.endPointOfContours.get(outline.endPointOfContours.size - 1) + 1;
		int instructionLength = raf.readUnsignedShort();
		raf.skipBytes(instructionLength);
		
		parseSimpleGlyphFlags(outline, pointCount);
		parseSimpleGlyphXCoordinates(outline, pointCount);
		parseSimpleGlyphYCoordinates(outline, pointCount);
	}

	private void parseSimpleGlyphFlags(GlyfOtline otline, int pointCount) {
		ByteArray flags = otline.flags;
		for (int i = 0; i < pointCount; i++) {
			byte flag = raf.readByte();
			flags.add(flag);
			
			if ((flag & repeat) != 0) {
				byte repeats = raf.readByte();
				for (int r = 1; r <= repeats; r++) {
					flags.add(flag);
				}
				i += repeats;
			}
		}
	}
	
	private void parseSimpleGlyphXCoordinates(GlyfOtline otline, int pointCount) {
		ByteArray flags = otline.flags;
		ShortArray xCoordinates = otline.xCoordinates;
		short x = 0;
		
		for (int i = 0; i < pointCount; i++) {
			byte flag = flags.get(i);
			
			if ((flag & xDual) != 0) {
				if ((flag & xShortVector) != 0) {
					x += raf.readUnsignedByte();
				}
			} else {
				if ((flag & xShortVector) != 0) {
					x -= raf.readUnsignedByte();
				} else {
					x += raf.readShort();
				}
			}
			
			xCoordinates.add(x);
		}
	}
	
	private void parseSimpleGlyphYCoordinates(GlyfOtline otline, int pointCount) {
		ByteArray flags = otline.flags;
		ShortArray yCoordinates = otline.yCoordinates;
		short y = 0;
		
		for (int i = 0; i < pointCount; i++) {
			byte flag = flags.get(i);
			
			if ((flag & yDual) != 0) {
				if ((flag & yShortVector) != 0) {
					y += raf.readUnsignedByte();
				}
			} else {
				if ((flag & yShortVector) != 0) {
					y -= raf.readUnsignedByte();
				} else {
					y += raf.readShort();
				}
			}
			
			yCoordinates.add(-y);
		}
	}
	
	private void parseCompositeGlyphOutline(int glyphOffset, int numberOfContours, GlyfOtline outline) {
		GlyfOtline componentOutline = GlyfOtline.obtain();

		int componentOffset = glyphOffset + GlyfOtlineOffset.glyfData.offset;
		int firstPointIndex = 0;
		int flags;

		do {
			int argument1;
			int argument2;
			float xscale = 1.0f;
			float yscale = 1.0f;
			float scale01 = 0.0f;
			float scale10 = 0.0f;
			int xtranslate = 0;
			int ytranslate = 0;

			raf.setPosition(componentOffset);

			flags = raf.readUnsignedShort();
			int glyphIndex = raf.readUnsignedShort();

			// Get the arguments as just their raw values
			if ((flags & ARG_1_AND_2_ARE_WORDS) != 0) {
				argument1 = raf.readUnsignedShort();
				argument2 = raf.readUnsignedShort();
			} else {
				argument1 = raf.readUnsignedByte();
				argument2 = raf.readUnsignedByte();
			}

			// Get the scale values (if any)
			if ((flags & WE_HAVE_A_SCALE) != 0) {
				xscale = yscale = raf.readUnsignedShort() / 0x4000;
			} else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
				xscale = raf.readUnsignedShort() / 0x4000;
				yscale = raf.readUnsignedShort() / 0x4000;
			} else if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
				xscale = raf.readUnsignedShort() / 0x4000;
				scale01 = raf.readUnsignedShort() / 0x4000;
				scale10 = raf.readUnsignedShort() / 0x4000;
				yscale = raf.readUnsignedShort() / 0x4000;
			}

			componentOffset = raf.getPosition();
			parseOutline(glyphIndex, componentOutline);

			if ((flags & ARGS_ARE_XY_VALUES) != 0) {
				xtranslate = argument1;
				ytranslate = argument2;
			} else {
				short point1x = outline.xCoordinates.get(argument1);
				short point1y = outline.yCoordinates.get(argument1);

				short point2x = outline.xCoordinates.get(argument1);
				short point2y = outline.yCoordinates.get(argument1);
				xtranslate = point1x - point2x;
				ytranslate = point1y - point2y;
			}

			for (int i = 0; i < componentOutline.endPointOfContours.size; i++) {
				outline.endPointOfContours.add(componentOutline.endPointOfContours.get(i) + firstPointIndex);
			}

			for (int i = 0; i < componentOutline.flags.size; i++) {
				outline.flags.add(componentOutline.flags.get(i));
				int x = componentOutline.xCoordinates.get(i);
				int y = componentOutline.yCoordinates.get(i);
				outline.xCoordinates.add(Math.round(x * xscale + y * scale10) + xtranslate);
				outline.yCoordinates.add(Math.round(x * scale01 + y * yscale) + ytranslate);
			}
			
			componentOutline.reset();

		} while ((flags & MORE_COMPONENTS) != 0);

		componentOutline.free();
	}
	
	enum GlyfOtlineOffset implements Offset {
		numberOfContours(0),
		xMin(2),
		yMin(4),
		xMax(6),
		yMax(8),
		glyfData(10);

		final int offset;

		private GlyfOtlineOffset(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
