package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ToIntNode extends ShaderTemplateNode {
	private String varName;

	public ToIntNode(String value) {
		varName = value.trim();
	}

	@Override
	protected String toStringValue() {
		return "'" + varName + "'";
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.append(Integer.toString((int) context.getValue(varName)));
	}
}
