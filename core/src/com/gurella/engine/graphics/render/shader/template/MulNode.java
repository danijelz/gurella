package com.gurella.engine.graphics.render.shader.template;

public class MulNode extends EvaluateNode {
	public MulNode(String expression) {
		super(expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return first * second;
	}

	@Override
	protected String getOperatorString() {
		return " * ";
	}
}
