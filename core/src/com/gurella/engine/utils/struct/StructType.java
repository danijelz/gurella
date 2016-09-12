package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.ImmutableArray;

public class StructType {
	final Array<StructProperty> _properties = new Array<StructProperty>();
	public final ImmutableArray<StructProperty> properties = new ImmutableArray<StructProperty>(_properties);

	public static abstract class StructProperty {
		int wordOffset;

		public StructProperty(int wordOffset) {
			this.wordOffset = wordOffset;
		}
	}
	
	public static class FloatStructProperty extends StructProperty {
		public FloatStructProperty(int wordOffset) {
			super(wordOffset);
		}
		
		public float get(ArrayOfStructs aos, int index) {
			return aos.getFloatByIndex(index, wordOffset);
		}
		
		public void set(ArrayOfStructs aos, int index, float value) {
			aos.setFloatByIndex(index, wordOffset, value);
		}
	}
}
