package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class UndefineNode extends PreprocessedShaderTemplateNode {
	private String propertyName;

	public UndefineNode(boolean preprocessed, String propertyName) {
		super(preprocessed);
		this.propertyName = propertyName.trim();
	}

	@Override
	protected void preprocess(ShaderGeneratorContext context) {
		if (preprocessed) {
			context.undefine(propertyName);
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (!preprocessed) {
			context.undefine(propertyName);
		}
	}

	@Override
	protected String toStringValue() {
		return "'" + propertyName + "'";
	}
}
