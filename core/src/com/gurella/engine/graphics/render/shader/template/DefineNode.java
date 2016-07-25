package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class DefineNode extends PreprocessedShaderTemplateNode {
	private String propertyName;

	public DefineNode(boolean preprocessed, String propertyName) {
		super(preprocessed);
		this.propertyName = propertyName.trim();
	}

	@Override
	protected void preprocess(ShaderGeneratorContext context) {
		if (preprocessed) {
			context.define(propertyName);
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (!preprocessed) {
			context.define(propertyName);
		}
	}

	@Override
	protected String toStringValue() {
		return "'" + propertyName + "'";
	}
}
