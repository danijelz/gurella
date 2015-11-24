package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.utils.FloatArray;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class FloatArrayParser implements PropertyParser<FloatArray> {
	public static final FloatArrayParser instance = new FloatArrayParser();
	
	private final Matcher matcher = Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?").matcher("");

	private FloatArrayParser() {
	}
	
	@Override
	public synchronized FloatArray parse(String strValue) {
		matcher.reset(strValue);

		FloatArray floatArray = new FloatArray();

		while (matcher.find()) {
			float value = Float.parseFloat(matcher.group(1));
			String units = matcher.group(6);
			floatArray.add("%".equals(units) ? value / 100 : value);
		}

		return floatArray;
	}
}
