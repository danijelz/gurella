package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class OrientParser implements PropertyParser<Float> {
	public static final OrientParser instance = new OrientParser();

	private OrientParser() {
	}
	
	@Override
	public Float parse(String strValue) {
		if("auto".equals(strValue)) {
			return Float.valueOf(Float.NaN);
		} else {
			return Float.valueOf(FloatParser.parseFloat(strValue));
		}
	}
}
