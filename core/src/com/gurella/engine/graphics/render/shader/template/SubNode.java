package com.gurella.engine.graphics.render.shader.template;

public class SubNode extends EvaluateNode {
	public SubNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return first - second;
	}

	@Override
	protected String getOperatorString() {
		return " - ";
	}
}
