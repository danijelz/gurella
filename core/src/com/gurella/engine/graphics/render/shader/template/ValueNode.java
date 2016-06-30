package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ValueNode extends ShaderTemplateNode {
	private String varName;
	private int varValue;

	public ValueNode(String value) {
		varName = value;
	}

	@Override
	protected String toStringValue() {
		return varName + ", " + varValue;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.append(Integer.toString(context.getValue(varName)));
	}
}
