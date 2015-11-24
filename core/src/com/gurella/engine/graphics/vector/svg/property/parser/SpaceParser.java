package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Space;

public class SpaceParser implements PropertyParser<Space> {
	public static final SpaceParser instance = new SpaceParser();

	private SpaceParser() {
	}

	@Override
	public Space parse(String strValue) {
		return Space.preserve.name().equals(strValue) ? Space.preserve : Space._default;
	}
}
