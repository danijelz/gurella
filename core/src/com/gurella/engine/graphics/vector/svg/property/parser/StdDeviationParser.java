package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.StdDeviation;

public class StdDeviationParser implements PropertyParser<StdDeviation> {
	public static final StdDeviationParser instance = new StdDeviationParser();

	private final Matcher matcher = Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)").matcher("");

	private StdDeviationParser() {
	}

	@Override
	public synchronized StdDeviation parse(String strValue) {
		matcher.reset(strValue);
		if (!matcher.find()) {
			return new StdDeviation(0);
		}

		float x = Float.parseFloat(matcher.group(1));
		if (!matcher.find()) {
			return new StdDeviation(x);
		}

		float y = Float.parseFloat(matcher.group(1));
		return new StdDeviation(x, y);
	}
}