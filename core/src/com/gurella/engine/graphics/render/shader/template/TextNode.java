package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

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
	protected void generate(ShaderGeneratorContext context) {
		context.append(text);
	}
}
