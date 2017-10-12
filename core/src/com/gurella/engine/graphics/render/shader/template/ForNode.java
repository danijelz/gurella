package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ForNode extends ShaderTemplateNode {
	private int count;
	private boolean useCount;
	private String countVariable;

	private String iterationsVariable;

	private int startIndex;
	private boolean useStartIndex;
	private String startIndexVariable;

	public ForNode(String value) {
		String[] params = value.split(",");

		countVariable = params[0].trim();
		try {
			count = Integer.parseInt(countVariable);
			useCount = true;
		} catch (Exception e) {
		}

		iterationsVariable = params.length > 1 ? params[1].trim() : "i";

		if (params.length > 2) {
			startIndexVariable = params[2].trim();
			try {
				startIndex = Integer.parseInt(startIndexVariable);
				useStartIndex = true;
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int count = useCount ? this.count : context.getIntValue(countVariable);
		int index = useStartIndex ? this.startIndex
				: startIndexVariable == null ? 0 : context.getIntValue(startIndexVariable);
		boolean valueSet = context.isValueSet(iterationsVariable);
		float oldValue = valueSet ? context.getValue(iterationsVariable) : 0f;

		for (; index < count; index++) {
			context.setValue(iterationsVariable, index);
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
		return "for (" + countVariable + ", " + iterationsVariable + ", " + startIndex + ")";
	}
}
