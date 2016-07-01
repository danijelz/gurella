package com.gurella.engine.graphics.render.shader.template;

public class SubNode extends EvaluateNode {
	public SubNode(String expression) {
		super(expression);
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
