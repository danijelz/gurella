package com.gurella.engine.graphics.render.shader.template;

public class AddNode extends EvaluateNode {
	public AddNode(String expression) {
		super(expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return first + second;
	}

	@Override
	protected String getOperatorString() {
		return " + ";
	}
}
