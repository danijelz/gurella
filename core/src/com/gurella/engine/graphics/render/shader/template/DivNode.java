package com.gurella.engine.graphics.render.shader.template;

public class DivNode extends EvaluateNode {
	public DivNode(boolean preprocessed, String expression) {
		super(preprocessed, expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return first / second;
	}

	@Override
	protected String getOperatorString() {
		return " / ";
	}
}
