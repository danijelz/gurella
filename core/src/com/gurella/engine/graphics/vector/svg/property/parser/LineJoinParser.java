package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.LineJoin;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class LineJoinParser implements PropertyParser<LineJoin> {
	public static final LineJoinParser instance = new LineJoinParser();

	private LineJoinParser() {
	}

	@Override
	public LineJoin parse(String strValue) {
		if ("round".equals(strValue)) {
			return LineJoin.round;
		} else if ("bevel".equals(strValue)) {
			return LineJoin.bevel;
		} else {
			return LineJoin.miter;
		}
	}
}
