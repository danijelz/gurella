package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio.Alignment;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio.Scale;

public class PreserveAspectRatioParser implements PropertyParser<PreserveAspectRatio> {
	public static final PreserveAspectRatioParser instance = new PreserveAspectRatioParser();

	private PreserveAspectRatioParser() {
	}

	@Override
	public PreserveAspectRatio parse(String strValue) {
		Alignment alignment = transformAlignment(strValue);
		Scale scale = contains(strValue, "slice") ? Scale.Slice : Scale.Meet;
		return new PreserveAspectRatio(alignment, scale);
	}

	private static Alignment transformAlignment(String strValue) {
		if (contains(strValue, "none")) {
			return Alignment.None;
		} else if (contains(strValue, "xMinYMin")) {
			return Alignment.XMinYMin;
		} else if (contains(strValue, "xMidYMin")) {
			return Alignment.XMidYMin;
		} else if (contains(strValue, "xMaxYMin")) {
			return Alignment.XMaxYMin;
		} else if (contains(strValue, "xMinYMid")) {
			return Alignment.XMinYMid;
		} else if (contains(strValue, "xMaxYMid")) {
			return Alignment.XMaxYMid;
		} else if (contains(strValue, "xMinYMax")) {
			return Alignment.XMinYMax;
		} else if (contains(strValue, "xMidYMax")) {
			return Alignment.XMidYMax;
		} else if (contains(strValue, "xMaxYMax")) {
			return Alignment.XMaxYMax;
		} else {
			return Alignment.XMidYMid;
		}
	}

	private static boolean contains(String text, String textToFind) {
		return text.indexOf(textToFind) != -1;
	}
}