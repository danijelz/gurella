package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class PieceNode extends ShaderTemplateNode {
	String name;

	public PieceNode(String name) {
		this.name = name;
	}

	@Override
	protected String toStringValue() {
		return name;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
	}
}
