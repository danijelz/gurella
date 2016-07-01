package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class IfexpNode extends ShaderTemplateNode {
	private String firstName;
	private String secondName;
	private char operator;
	private Integer constant;

	public IfexpNode(String expression) {
		String[] params = expression.split(",");
		firstName = params[0].trim();
		secondName = params[1].trim();
		operator = params.length > 2 ? params[2].trim().charAt(0) : '=';
		try {
			constant = Integer.valueOf(secondName);
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int first = context.getValue(firstName);
		int second = constant == null ? context.getValue(secondName) : constant.intValue();
		if (evaluate(first, second)) {
			generateChildren(context);
		}
	}

	protected boolean evaluate(int first, int second) {
		switch (operator) {
		case '!':
			return first != second;
		case '>':
			return first > second;
		case '<':
			return first < second;
		default:
			return first == second;
		}
	}

	@Override
	protected String toStringValue() {
		return "if ('" + firstName + "' " + operator + " '" + secondName + "')";
	}
}
