package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public abstract class EvaluateNode extends ShaderTemplateNode {
	private String firstName;
	private String secondName;
	private Integer constant;

	public EvaluateNode(String expression) {
		String[] params = expression.split(",");
		firstName = params[0].trim();
		secondName = params[1].trim();
		try {
			constant = Integer.valueOf(secondName);
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int first = context.getValue(firstName);
		int second = constant == null ? context.getValue(secondName) : constant.intValue();
		context.setValue(firstName, evaluate(first, second));
	}

	protected abstract int evaluate(int first, int second);

	@Override
	protected String toStringValue() {
		return firstName + getOperatorString() + secondName;
	}

	protected abstract String getOperatorString();
}
