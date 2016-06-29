package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public class IfdefNode extends ShaderTemplateNode {
	private BooleanExpression expression;

	public IfdefNode(String condition) {
		this.expression = BooleanExpressionParser.parse(condition);
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		if (expression.evaluate()) {
			generateChildren(template, builder);
		}
	}

	@Override
	protected String toStringValue() {
		return "Condition '" + expression.toString() + "'";
	}

	private enum Symbol {
		EOF,

		OR,
		AND,
		NOT,

		PROPERTY,

		LEFT,
		RIGHT;
	}

	private static interface BooleanExpression {
		public boolean evaluate();
	}

	private static abstract class NonTerminal implements BooleanExpression {
		protected BooleanExpression left, right;

		public void setLeft(BooleanExpression left) {
			this.left = left;
		}

		public void setRight(BooleanExpression right) {
			this.right = right;
		}
	}

	private static class Terminal implements BooleanExpression {
		protected String value;

		public Terminal(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public boolean evaluate() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static class Or extends NonTerminal {
		@Override
		public boolean evaluate() {
			return left.evaluate() || right.evaluate();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " | " + right.toString() + ")";
		}
	}

	private static class And extends NonTerminal {
		@Override
		public boolean evaluate() {
			return left.evaluate() && right.evaluate();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " & " + right.toString() + ")";
		}
	}

	private static class Not implements BooleanExpression {
		BooleanExpression negated;

		@Override
		public boolean evaluate() {
			return !negated.evaluate();
		}

		@Override
		public String toString() {
			return "!" + negated.toString();
		}
	}

	private static class BooleanExpressionParser implements Poolable {
		private int index = 0;
		private int len = 0;
		private String input;
		private StringBuilder lastProperty = new StringBuilder();

		private Symbol symbol;
		private BooleanExpression root;

		static BooleanExpression parse(String input) {
			BooleanExpressionParser parser = PoolService.obtain(BooleanExpressionParser.class);
			parser.input = input;
			parser.len = input.length();
			parser.expression();
			BooleanExpression expression = parser.root;
			PoolService.free(parser);
			return expression;
		}

		private void expression() {
			term();
			while (symbol == Symbol.OR) {
				Or or = new Or();
				or.setLeft(root);
				term();
				or.setRight(root);
				root = or;
			}
		}

		private void term() {
			factor();
			while (symbol == Symbol.AND) {
				And and = new And();
				and.setLeft(root);
				factor();
				and.setRight(root);
				root = and;
			}
		}

		private void factor() {
			symbol = nextSymbol();
			if (symbol == Symbol.PROPERTY) {
				root = new Terminal(lastProperty.toString());
				symbol = nextSymbol();
			} else if (symbol == Symbol.NOT) {
				Not not = new Not();
				factor();
				not.negated = root;
				root = not;
			} else if (symbol == Symbol.LEFT) {
				expression();
				symbol = nextSymbol(); // we don't care about ')'
			} else {
				throw new RuntimeException("Expression Malformed");
			}
		}

		public Symbol nextSymbol() {
			while (index < len) {
				char c = input.charAt(index++);
				switch (c) {
				case '(':
					return Symbol.LEFT;
				case ')':
					return Symbol.RIGHT;
				case '&':
					return Symbol.AND;
				case '|':
					return Symbol.OR;
				case '!':
					return Symbol.NOT;
				default:
					if (Values.isWhitespace(c)) {
						continue;
					} else {
						lastProperty.setLength(0);
						lastProperty.append(c);

						extractProperty();
						return Symbol.PROPERTY;
					}
				}
			}

			return Symbol.EOF;
		}

		protected void extractProperty() {
			while (index < len) {
				char c = input.charAt(index++);
				switch (c) {
				case '\n':
				case '\r':
				case '(':
				case ')':
				case '&':
				case '|':
				case '!':
					index--;
					return;
				default:
					if (Values.isWhitespace(c)) {
						return;
					} else {
						lastProperty.append(c);
					}
				}
			}
		}

		@Override
		public void reset() {
			index = 0;
			len = 0;
			input = null;
			lastProperty.setLength(0);
			symbol = null;
			root = null;
		}
	}

	public static void main(String[] args) {
		System.out.println(BooleanExpressionParser.parse("true & ((true | false) & !(true & false))").toString());
		System.out.println(BooleanExpressionParser.parse("\n\nA & ((B | C) & !\n(!DEF & !GHI))").toString());
	}
}
