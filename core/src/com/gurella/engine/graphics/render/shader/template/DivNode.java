package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class DivNode extends ShaderTemplateNode {
	private String varName;
	private int varValue;

	public DivNode(String value) {
		String[] params = value.split(",");
		varName = params[0].trim();
		try {
			varValue = Integer.parseInt(params[1].trim());
		} catch (Exception e) {
			varValue = 0;
		}
	}

	@Override
	protected String toStringValue() {
		return varName + " / " + varValue;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int value = context.getValue(varName);
		context.setValue(varName, value == 0 ? 0 : value / varValue);
	}
}
