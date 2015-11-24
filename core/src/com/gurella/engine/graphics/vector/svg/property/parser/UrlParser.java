package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class UrlParser implements PropertyParser<String> {
	public static final UrlParser instance = new UrlParser();

	private final Matcher matcher = Pattern.compile("\\s*url\\((.*)\\)\\s*").matcher("");

	private UrlParser() {
	}

	@Override
	public synchronized String parse(String strValue) {
		if (matcher.reset(strValue).matches()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
}
