package com.gurella.engine.graphics.render.shader.template;

public class SubNode extends EvaluateNode {
	public SubNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected float evaluate(float first, float second) {
		return first - second;
	}

	@Override
	protected String getOperatorString() {
		return " - ";
	}
}
