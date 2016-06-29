package com.gurella.engine.graphics.render.shader.template;

public class IfdefNode extends ShaderTemplateNode {
	private BooleanExpression expression;

	public IfdefNode(BooleanExpression expression) {
		this.expression = expression;
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		if (expression.evaluate()) {
			generateChildren(template, builder);
		}
	}

	@Override
	protected String toStringValue() {
		return "Expression '" + expression.toString() + "'";
	}
}
