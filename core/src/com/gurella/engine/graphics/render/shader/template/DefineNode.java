package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class DefineNode extends ShaderTemplateNode {
	private String propertyName;

	public DefineNode(String propertyName) {
		this.propertyName = propertyName.trim();
	}

	@Override
	protected String toStringValue() {
		return "'" + propertyName + "'";
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.define(propertyName);
	}
}
