package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class FloatParser implements PropertyParser<Float> {
	private static final Matcher matcher = Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?").matcher("");

	public static final FloatParser instance = new FloatParser();

	private FloatParser() {
	}

	@Override
	public Float parse(String strValue) {
		return Float.valueOf(parseFloat(strValue));
	}

	public static synchronized float parseFloat(String strValue) {
		matcher.reset(strValue);
		if (!matcher.find()) {
			return 0;
		}

		try {
			float value = Float.parseFloat(matcher.group(1));
			String units = matcher.group(6);
			return "%".equals(units) ? value / 100 : value;
		} catch (Exception e) {
			return 0;
		}
	}
}
