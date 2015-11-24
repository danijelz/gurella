package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Visibility;

public class VisibilityParser implements PropertyParser<Visibility> {
	public static final VisibilityParser instance = new VisibilityParser();

	private VisibilityParser() {
	}

	@Override
	public Visibility parse(String strValue) {
		try {
			return Visibility.valueOf(strValue);
		} catch (Exception e) {
			return Visibility.visible;
		}
	}
}