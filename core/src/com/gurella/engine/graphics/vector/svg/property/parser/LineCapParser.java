package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.LineCap;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class LineCapParser implements PropertyParser<LineCap> {
	public static final LineCapParser instance = new LineCapParser();

	private LineCapParser() {
	}

	@Override
	public LineCap parse(String strValue) {
		if ("round".equals(strValue)) {
			return LineCap.round;
		} else if ("square".equals(strValue)) {
			return LineCap.square;
		} else {
			return LineCap.butt;
		}
	}
}
