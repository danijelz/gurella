package com.gurella.engine.graphics.vector.sfnt.cff;

import com.badlogic.gdx.utils.Array;

class CffDictEntry {
	int operator;
	Array<Number> operands;

	private Object value;

	CffDictEntry(int operator, Array<Number> operands) {
		this.operator = operator;
		this.operands = operands;
	}

	CffDictEntry(int operator, Number[] operands) {
		this.operator = operator;
		this.operands = new Array<Number>(operands);
	}

	public Number getNumber() {
		if (value == null) {
			value = operands.size == 0 ? null : operands.get(0);
		}
		return (Number) value;
	}

	public Float getFloat() {
		if (value == null) {
			value = operands.size == 0 ? null : new Float(operands.get(0).floatValue());
		}
		return (Float) value;
	}

	public Integer getInteger() {
		if (value == null) {
			value = operands.size == 0 ? null : new Integer(operands.get(0).intValue());
		}
		return (Integer) value;
	}

	public Integer[] getRange() {
		if (value == null) {
			value = operands.size == 0 ? null
					: new Integer[] { new Integer(operands.get(0).intValue()),
							new Integer(operands.get(1).intValue()) };
		}
		return (Integer[]) value;
	}

	public float[] getFloatArray() {
		if (value == null) {
			float[] array = new float[operands.size];
			for (int i = 0; i < operands.size; i++) {
				array[i] = operands.get(i).floatValue();
			}
			value = array;
		}
		return (float[]) value;
	}

	public float[] getDelta() {
		if (value == null) {
			float[] array = new float[operands.size];
			float previous = 0;
			for (int i = 0; i < operands.size; i++) {
				array[i] = operands.get(i).floatValue() + previous;
				previous = array[i];
			}
			value = array;
		}
		return (float[]) value;
	}

	public Integer getSID() {
		if (value == null) {
			value = operands.size == 0 ? null : new Integer(operands.get(0).intValue());
		}
		return (Integer) value;
	}

	public Boolean getBoolean() {
		if (value == null) {
			value = operands.size == 0 ? null : getInteger().intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		}
		return (Boolean) value;
	}
}
