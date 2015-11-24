package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;

public class OverflowParser implements PropertyParser<Overflow> {
	public static final OverflowParser instance = new OverflowParser();

	private OverflowParser() {
	}

	@Override
	public Overflow parse(String strValue) {
		try {
			return Overflow.valueOf(strValue);
		} catch (Exception e) {
			return Overflow.visible;
		}
	}
}