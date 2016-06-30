package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class AddNode extends ShaderTemplateNode {
	private String varName;
	private int varValue;

	public AddNode(String value) {
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
		return varName + " + " + varValue;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.setValue(varName, context.getValue(varName) + varValue);
	}
}
