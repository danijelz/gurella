package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class IfdefNode extends ShaderTemplateNode {
	private BooleanExpression expression;

	public IfdefNode(BooleanExpression expression) {
		this.expression = expression;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (expression.evaluate(context)) {
			generateChildren(context);
		}
	}

	@Override
	protected String toStringValue() {
		return "Expression '" + expression.toString() + "'";
	}
}
