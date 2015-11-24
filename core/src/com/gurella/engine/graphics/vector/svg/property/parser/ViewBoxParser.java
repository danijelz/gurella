package com.gurella.engine.graphics.vector.svg.property.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.math.Rectangle;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;

public class ViewBoxParser implements PropertyParser<Rectangle> {
	public static final ViewBoxParser instance = new ViewBoxParser();

	private final Matcher matcher = Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)").matcher("");

	private ViewBoxParser() {
	}

	@Override
	public synchronized Rectangle parse(String strValue) {
		matcher.reset(strValue);

		if (!matcher.find()) {
			return null;
		}
		float x = Float.parseFloat(matcher.group(1));

		if (!matcher.find()) {
			return null;
		}
		float y = Float.parseFloat(matcher.group(1));

		if (!matcher.find()) {
			return null;
		}
		float width = Float.parseFloat(matcher.group(1));

		if (!matcher.find()) {
			return null;
		}
		float height = Float.parseFloat(matcher.group(1));

		return new Rectangle(x, y, width, height);
	}
}
