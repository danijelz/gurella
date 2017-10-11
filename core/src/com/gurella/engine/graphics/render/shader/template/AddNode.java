package com.gurella.engine.graphics.render.shader.template;

public class AddNode extends EvaluateNode {
	public AddNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected float evaluate(float first, float second) {
		return first + second;
	}

	@Override
	protected String getOperatorString() {
		return " + ";
	}
}
