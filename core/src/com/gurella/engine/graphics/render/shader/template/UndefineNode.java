package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class UndefineNode extends ShaderTemplateNode {
	private String propertyName;

	public UndefineNode(String propertyName) {
		this.propertyName = propertyName.trim();
	}

	@Override
	protected String toStringValue() {
		return "'" + propertyName + "'";
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.undefine(propertyName);
	}
}
