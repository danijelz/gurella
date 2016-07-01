package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class IfexpNode extends ShaderTemplateNode {
	private String firstProperty;
	private String secondProperty;
	private char operator;
	private Integer constant;

	public IfexpNode(String expression) {
		String[] params = expression.split(",");
		if (params.length < 2 || params.length > 3) {
			throw new GdxRuntimeException(
					"Invalid expression: @ifexp(" + expression + ")\nCorrect form: '@ifexp (variableName, value [, operator])'.\n"
							+ "Value can be name of variable or int literal. "
							+ "Valid operators:\n - '=' equal\n - '!' not equal\n - '>' greater\n - '<' less\n\n"
							+ "If no operator is specified it defaults to '='.");
		}

		firstProperty = params[0].trim();
		secondProperty = params[1].trim();
		operator = params.length > 2 ? params[2].trim().charAt(0) : '=';
		try {
			constant = Integer.valueOf(secondProperty);
		} catch (Exception e) {
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int first = context.getValue(firstProperty);
		int second = constant == null ? context.getValue(secondProperty) : constant.intValue();
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
		return "if ('" + firstProperty + "' " + operator + " '" + secondProperty + "')";
	}
}
