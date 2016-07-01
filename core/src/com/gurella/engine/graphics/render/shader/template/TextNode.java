package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class TextNode extends ShaderTemplateNode {
	StringBuilder text = new StringBuilder();

	public TextNode(CharSequence sequence) {
		text.append(sequence);
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
