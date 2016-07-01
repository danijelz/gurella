package com.gurella.engine.graphics.render.shader.template;

public class MaxNode extends EvaluateNode {
	public MaxNode(String expression) {
		super(expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return Math.max(first, second);
	}

	@Override
	protected String getOperatorString() {
		return " max ";
	}
}
