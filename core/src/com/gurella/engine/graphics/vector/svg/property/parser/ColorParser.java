package com.gurella.engine.graphics.vector.svg.property.parser;

import com.badlogic.gdx.math.MathUtils;
import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Color;

public class ColorParser implements PropertyParser<Color> {
	public static final ColorParser instance = new ColorParser();

	private ColorParser() {
	}

	@Override
	public Color parse(String strValue) {
		int intColor = parseIntColor(strValue);
		return new Color(intColor < 0 ? 0 : intColor);
	}

	// TODO
	// https://github.com/senkir/androidsvg/blob/master/src/com/caverock/androidsvg/SVGParser.java
	private static int parseIntColor(String strValue) {
		if (strValue.charAt(0) == '#') {
			return parseHexColor(strValue);
		} else if (strValue.toLowerCase().startsWith("rgb(")) {
			return parseRgbColor(strValue);
		} else {
			return ColorKeyword.getColor(strValue);
		}
	}

	private static int parseRgbColor(String strValue) {
		int rgbEnd = strValue.indexOf(")");
		if (rgbEnd < 0) {
			return -1;
		}

		String[] colourComponents = strValue.trim().substring(4, rgbEnd).split("\\s");
		if (colourComponents.length != 3) {
			return -1;
		}

		int red = parseColourComponent(colourComponents[0]);
		int green = parseColourComponent(colourComponents[1]);
		int blue = parseColourComponent(colourComponents[2]);

		return red << 16 | green << 8 | blue;
	}

	private static int parseColourComponent(String colourComponent) {
		int percentIndex = colourComponent.indexOf('%');
		if (percentIndex < 0) {
			return MathUtils.clamp(Integer.parseInt(colourComponent.trim()), 0, 255);
		} else {
			int percent = MathUtils.clamp(Integer.parseInt(colourComponent.substring(0, percentIndex).trim()), 0, 100);
			return (percent * 255 / 100);
		}
	}

	private static int parseHexColor(String strValue) {
		try {
			if (strValue.length() == 7) {
				return Integer.parseInt(strValue.substring(1), 16);
			} else if (strValue.length() == 4) {
				int threehex = Integer.parseInt(strValue.substring(1), 16);
				int h1 = threehex & 0xf00;
				int h2 = threehex & 0x0f0;
				int h3 = threehex & 0x00f;
				return h1 << 16 | h1 << 12 | h2 << 8 | h2 << 4 | h3 << 4 | h3;
			} else {
				return -1;
			}
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}