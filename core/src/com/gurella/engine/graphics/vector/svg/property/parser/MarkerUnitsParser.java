package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.MarkerUnits;

public class MarkerUnitsParser implements PropertyParser<MarkerUnits> {
	public static final MarkerUnitsParser instance = new MarkerUnitsParser();

	private MarkerUnitsParser() {
	}

	@Override
	public MarkerUnits parse(String strValue) {
		return "userSpaceOnUse".equals(strValue) ? MarkerUnits.userSpaceOnUse : MarkerUnits.strokeWidth;
	}
}