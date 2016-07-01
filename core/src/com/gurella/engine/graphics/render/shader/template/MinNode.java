package com.gurella.engine.graphics.render.shader.template;

public class MinNode extends EvaluateNode {
	public MinNode(String expression) {
		super(expression);
	}

	@Override
	protected int evaluate(int first, int second) {
		return Math.min(first, second);
	}

	@Override
	protected String getOperatorString() {
		return " min ";
	}
}
