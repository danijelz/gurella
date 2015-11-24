package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.FillRule;

public class FillRuleParser implements PropertyParser<FillRule> {
	public static final FillRuleParser instance = new FillRuleParser();

	private FillRuleParser() {
	}

	@Override
	public FillRule parse(String strValue) {
		try {
			return FillRule.valueOf(strValue);
		} catch (Exception e) {
			return FillRule.nonzero;
		}
	}
}