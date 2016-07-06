package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class IfexpNode extends ShaderTemplateNode {
	private String firstProperty;
	private String secondProperty;
	private Operator operator;
	private Integer constant;

	public IfexpNode(String expression) {
		String[] params = expression.split(",");
		if (params.length < 2 || params.length > 3) {
			throw new GdxRuntimeException("Invalid expression: @ifexp(" + expression
					+ ")\nCorrect form: '@ifexp (variableName, variableNameOrIntLiteral [, operator])'.\n"
					+ "Valid operators:\n - '=' equal\n - '!' not equal\n - '>' greater\n - '<' less\n\n"
					+ "If no operator is specified it defaults to '='.");
		}

		firstProperty = params[0].trim();
		secondProperty = params[1].trim();
		operator = params.length > 2 ? parseOperator(params[2]) : Operator.eq;
		try {
			constant = Integer.valueOf(secondProperty);
		} catch (Exception e) {
		}
	}

	private static Operator parseOperator(String operatorStr) {
		if (operatorStr.contains("==")) {
			return Operator.eq;
		} else if (operatorStr.contains("!=")) {
			return Operator.neq;
		} else if (operatorStr.contains(">=")) {
			return Operator.gte;
		} else if (operatorStr.contains("<=")) {
			return Operator.lte;
		} else if (operatorStr.contains(">")) {
			return Operator.gt;
		} else if (operatorStr.contains("<")) {
			return Operator.lt;
		} else {
			throw new IllegalArgumentException(operatorStr);
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		int first = context.getValue(firstProperty);
		int second = constant == null ? context.getValue(secondProperty) : constant.intValue();
		if (operator.evaluate(first, second)) {
			generateChildren(context);
		}
	}

	@Override
	protected String toStringValue() {
		return "if ('" + firstProperty + "' " + operator + " '" + secondProperty + "')";
	}

	private enum Operator {
		eq, neq, gt, lt, gte, lte;

		protected boolean evaluate(int first, int second) {
			switch (this) {
			case eq:
				return first == second;
			case neq:
				return first != second;
			case gt:
				return first > second;
			case lt:
				return first < second;
			case gte:
				return first >= second;
			case lte:
				return first <= second;
			default:
				throw new IllegalArgumentException();
			}
		}
	}
}
