package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ValueNode extends ShaderTemplateNode {
	private String varName;

	public ValueNode(String value) {
		varName = value.trim();
	}

	@Override
	protected String toStringValue() {
		return "'" + varName + "'";
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.append(Float.toString(context.getValue(varName)));
	}
}
