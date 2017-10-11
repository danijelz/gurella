package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ForNode extends ShaderTemplateNode {
	private String countProperty;
	private int iterations;
	private boolean useIterations;
	private String iterationsVariable;
	private int startIndex;

	public ForNode(String value) {
		String[] params = value.split(",");
		countProperty = params[0].trim();
		iterationsVariable = params.length > 1 ? params[1].trim() : "i";
		if (params.length > 2) {
			startIndex = Integer.parseInt(params[2].trim());
		}

		try {
			iterations = Integer.parseInt(countProperty);
			useIterations = true;
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		float count = useIterations ? context.getValue(countProperty) : iterations;
		boolean valueSet = context.isValueSet(iterationsVariable);
		float oldValue = valueSet ? context.getValue(iterationsVariable) : 0f;

		for (int i = startIndex; i < count; i++) {
			context.setValue(iterationsVariable, i);
			generateChildren(context);
		}

		if (valueSet) {
			context.setValue(iterationsVariable, oldValue);
		} else {
			context.unsetValue(iterationsVariable);
		}
	}

	@Override
	protected String toStringValue() {
		return "for '" + countProperty + "'";
	}
}
