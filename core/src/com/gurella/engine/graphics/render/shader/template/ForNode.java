package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ForNode extends ShaderTemplateNode {
	private String countProperty;
	private Integer countValue;
	private String variableName;
	private int startIndex;

	public ForNode(String value) {
		String[] params = value.split(",");
		countProperty = params[0].trim();
		variableName = params.length > 1 ? params[1].trim() : "i";
		if(params.length > 2) {
			startIndex = Integer.parseInt(params[2].trim());
		}

		try {
			countValue = Integer.valueOf(countProperty);
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int count = countValue == null ? context.getValue(countProperty) : countValue.intValue();
		boolean valueSet = context.isValueSet(variableName);
		int oldValue = valueSet ? context.getValue(variableName) : 0;

		for (int i = startIndex; i < count; i++) {
			context.setValue(variableName, i);
			generateChildren(context);
		}

		if (valueSet) {
			context.setValue(variableName, oldValue);
		} else {
			context.unsetValue(variableName);
		}
	}

	@Override
	protected String toStringValue() {
		return "for '" + countProperty + "'";
	}
}
