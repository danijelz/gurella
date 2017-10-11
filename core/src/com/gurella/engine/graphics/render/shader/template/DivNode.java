package com.gurella.engine.graphics.render.shader.template;

public class DivNode extends EvaluateNode {
	public DivNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected float evaluate(float first, float second) {
		return first / second;
	}

	@Override
	protected String getOperatorString() {
		return " / ";
	}
}
