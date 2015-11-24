package com.gurella.engine.graphics.vector.svg.property.value;

import com.badlogic.gdx.utils.NumberUtils;

public class Length {
	private static final float dpi = 96;
	
	public static final Length zero = new UnmodifiableLength(0, Unit.unknown);
	public static final Length one = new UnmodifiableLength(1, Unit.unknown);
	public static final Length three = new UnmodifiableLength(3, Unit.unknown);

	private float value;
	private Unit unit;

	public Length(float value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public float getPixels() {
		// TODO
		switch (unit) {
		case px:
			return value;
			/*
			 * case em: return value * renderer.getCurrentFontSize(); case ex:
			 * return value * renderer.getCurrentFontXHeight();
			 */
		case in:
			return value * dpi;
		case cm:
			return value * dpi / 2.54f;
		case mm:
			return value * dpi / 25.4f;
		case pt: // 1 point = 1/72 in
			return value * dpi / 72f;
		case pc: // 1 pica = 1/6 in
			return value * dpi / 6f;
			/*
			 * case PERCENT: Box viewPortUser =
			 * renderer.getCurrentViewPortInUserUnits(); if (viewPortUser ==
			 * null) return value; // Undefined in this situation - so just
			 * return value to avoid an NPE return value * viewPortUser.width /
			 * 100f;
			 */
		default:
			return value;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return equals((Length) obj);
	}

	public boolean equals(Length other) {
		return unit == other.unit && value == other.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + NumberUtils.floatToIntBits(value);
		return result;
	}

	public static final class UnmodifiableLength extends Length {
		public UnmodifiableLength(float value, Unit unit) {
			super(value, unit);
		}

		// TODO override setters

		@Override
		public int hashCode() {
			return super.hashCode() + 31;
		}
	}
}
