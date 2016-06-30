package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ForNode extends ShaderTemplateNode {
	private String varName;
	private String countProperty;
	private Integer countValue;

	public ForNode(String value) {
		String[] params = value.split(",");
		countProperty = params[0].trim();
		varName = params.length > 1 ? params[1].trim() : "n";

		try {
			countValue = Integer.valueOf(countProperty);
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int count = countValue == null ? context.getValue(countProperty) : countValue.intValue();
		boolean valueSet = context.isValueSet(varName);
		int oldValue = valueSet ? context.getValue(varName) : 0;

		for (int i = 0; i < count; i++) {
			context.setValue(varName, i);
			generateChildren(context);
		}

		if (valueSet) {
			context.setValue(varName, oldValue);
		} else {
			context.unsetValue(varName);
		}
	}

	@Override
	protected String toStringValue() {
		return "for '" + countProperty + "'";
	}
}
