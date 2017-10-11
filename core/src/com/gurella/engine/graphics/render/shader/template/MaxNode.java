package com.gurella.engine.graphics.render.shader.template;

public class MaxNode extends EvaluateNode {
	public MaxNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected float evaluate(float first, float second) {
		return Math.max(first, second);
	}

	@Override
	protected String getOperatorString() {
		return " max ";
	}
}
