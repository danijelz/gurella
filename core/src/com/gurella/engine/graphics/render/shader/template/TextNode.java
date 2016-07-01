package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class TextNode extends ShaderTemplateNode {
	StringBuffer text;

	public TextNode(StringBuffer text) {
		this.text = text;
	}

	void append(CharSequence sequence) {
		text.append(sequence);
	}

	@Override
	protected String toStringValue() {
		return text.toString().replaceAll("(\r\n|\n|\r)", "\\\\n");
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		context.append(text);
	}
}
