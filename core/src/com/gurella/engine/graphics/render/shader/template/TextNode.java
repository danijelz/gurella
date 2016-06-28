package com.gurella.engine.graphics.render.shader.template;

public class TextNode extends ShaderTemplateNode {
	String text;

	public TextNode(String text) {
		this.text = text;
	}

	@Override
	protected String toStringValue() {
		return text.toString().replace("\n", "\\n");
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		builder.append(text);
	}
}
