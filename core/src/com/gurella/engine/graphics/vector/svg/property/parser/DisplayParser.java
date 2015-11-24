package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Display;

public class DisplayParser implements PropertyParser<Display> {
	public static final DisplayParser instance = new DisplayParser();

	private DisplayParser() {
	}

	@Override
	public Display parse(String strValue) {
		Display display = Display.getValueByDisplayName(strValue);
		return display == null ? Display.inline : display;
	}
}