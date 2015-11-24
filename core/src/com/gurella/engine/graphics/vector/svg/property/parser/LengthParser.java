package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.Unit;

public class LengthParser implements PropertyParser<Length> {
	public static final LengthParser instance = new LengthParser();

	private LengthParser() {
	}

	@Override
	public Length parse(String strValue) {
		float value = FloatParser.parseFloat(strValue);
		Unit unit = UnitParser.instance.parse(strValue);
		return new Length(value, unit);
	}
}