package com.gurella.engine.graphics.render.shader.template;

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
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		generateChildren(template, builder);
	}
}
