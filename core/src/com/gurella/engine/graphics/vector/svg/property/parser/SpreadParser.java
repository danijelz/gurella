package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.GradientSpread;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class SpreadParser implements PropertyParser<GradientSpread> {
	public static final SpreadParser instance = new SpreadParser();
	
	private SpreadParser() {
	}

	@Override
	public GradientSpread parse(String strValue) {
		if("reflect".equals(strValue)) {
			return GradientSpread.reflect;
		} else if("repeat".equals(strValue)) {
			return GradientSpread.repeat;
		} else {
			return GradientSpread.pad;
		}
	}
}
