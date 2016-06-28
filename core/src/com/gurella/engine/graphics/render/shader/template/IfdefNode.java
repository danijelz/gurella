package com.gurella.engine.graphics.render.shader.template;

public class IfdefNode extends ShaderTemplateNode {
	private Condition condition;

	public IfdefNode(String condition) {
		this.condition = new Condition(condition);
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		if (condition.evaluate()) {
			generateChildren(template, builder);
		}
	}

	@Override
	protected String toStringValue() {
		return "Condition '" + condition.toString() + "'";
	}

	private static class Condition {
		String value;
		ConditionPart conditionPart;

		public Condition(String value) {
			this.value = value;
			parseConditionPart(value);
		}

		private ConditionPart parseConditionPart(String value) {
			ConditionPart current = new SimpleConditionPart(value);
			StringBuilder builder = new StringBuilder();
			for (int i = 0, n = value.length(); i < n; i++) {
				char c = value.charAt(i);
				switch (c) {
				case '(':

					break;
				case ')':

					break;
				case '|':

					break;
				case '&':

					break;
				case '!':

					break;
				case '^':

					break;
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					break;
				default:
					builder.append(c);
					break;
				}
			}
		}

		int index = 0;

		private String ParseAtom(String expr) {
			StringBuilder builder = new StringBuilder();

			while (index < expr.length()) {
				char c = expr.charAt(index++);

				switch (c) {
				case '(':
				case ')':
				case '|':
				case '&':
				case '!':
				case '^':
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					index--;
					return builder.toString();
				default:
					builder.append(c);
					break;
				}
			}

			return "";
		}

		//https://www.strchr.com/expression_evaluator
		// Parse multiplication and division
		String ParseFactors(String expr) {
			String num1 = ParseAtom(expr);
			for (;;) {
				// Save the operation
				char op = expr.charAt(index);
				if (op != '/' && op != '*')
					return num1;
				index++;
				String num2 = ParseAtom(expr);
				// Perform the saved operation
				if (op == '/') {
					num1 /= num2;
				} else {
					num1 *= num2;
				}
			}
		}

		// Parse addition and subtraction
		String ParseSummands(String expr) {
			String num1 = ParseFactors(expr);
			for (;;) {
				char op = expr.charAt(index);
				if (op != '-' && op != '+') {
					return num1;
				}
				index++;
				String num2 = ParseFactors(expr);
				if (op == '-') {
					num1 -= num2;
				} else {
					num1 += num2;
				}
			}
		}

		String EvaluateTheExpression(String expr) {
			return ParseSummands(expr);
		}

		boolean evaluate() {
			return value != null;// TODO
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private static abstract class ConditionPart {

	}

	private static class SimpleConditionPart extends ConditionPart {
		private String value;
		private boolean negated;
		private ConditionPart next;

		public SimpleConditionPart(String value) {
			this.value = value;
		}
	}

	private static class CompositeConditionPart extends ConditionPart {
		private ConditionPartCompositor compositor;
		private ConditionPart[] composites;

		public CompositeConditionPart(ConditionPartCompositor compositor, ConditionPart... composites) {
			this.compositor = compositor;
			this.composites = composites;
		}
	}

	private enum ConditionPartCompositor {
		and, or;
	}
}
