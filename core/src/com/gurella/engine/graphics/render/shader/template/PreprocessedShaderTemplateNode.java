package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public abstract class PreprocessedShaderTemplateNode extends ShaderTemplateNode {
	boolean preprocessed;

	public PreprocessedShaderTemplateNode(boolean preprocessed) {
		this.preprocessed = preprocessed;
	}

	protected abstract void preprocess(ShaderGeneratorContext context);
}
