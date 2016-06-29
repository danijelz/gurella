package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.utils.Values;

public class BoolExp {
	public static class Lexer {
		public static final int EOL = -3;
		public static final int EOF = -2;
		public static final int INVALID = -1;

		public static final int NONE = 0;

		public static final int OR = 1;
		public static final int AND = 2;
		public static final int NOT = 3;

		public static final int PROPERTY = 4;

		public static final int LEFT = 5;
		public static final int RIGHT = 6;

		private int index = 0;
		private int len = 0;
		private String input;
		private StringBuilder lastProperty = new StringBuilder();

		public Lexer(String input) {
			this.input = input;
			len = input.length();
		}

		public int nextSymbol() {
			while (index < len) {
				char c = input.charAt(index++);
				switch (c) {
				case '\n':
				case '\r':
					return EOL;
				case '(':
					return LEFT;
				case ')':
					return RIGHT;
				case '&':
					return AND;
				case '|':
					return OR;
				case '!':
					return NOT;
				default:
					if (Values.isWhitespace(c)) {
						continue;
					} else {
						lastProperty.setLength(0);
						lastProperty.append(c);

						while (index < len) {
							char c2 = input.charAt(index++);
							switch (c2) {
							case '\n':
							case '\r':
							case '(':
							case ')':
							case '&':
							case '|':
							case '!':
								index--;
								return PROPERTY;
							default:
								if (Values.isWhitespace(c2)) {
									return PROPERTY;
								} else {
									lastProperty.append(c2);
								}
							}
						}
						return PROPERTY;
					}
				}
			}

			return EOF;
		}
	}

	public static interface BooleanExpression {
		public boolean interpret();
	}

	public static abstract class NonTerminal implements BooleanExpression {
		protected BooleanExpression left, right;

		public void setLeft(BooleanExpression left) {
			this.left = left;
		}

		public void setRight(BooleanExpression right) {
			this.right = right;
		}
	}

	public static class Terminal implements BooleanExpression {
		protected String value;

		public Terminal(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public boolean interpret() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static class Or extends NonTerminal {
		@Override
		public boolean interpret() {
			return left.interpret() || right.interpret();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " | " + right.toString() + ")";
		}
	}

	public static class And extends NonTerminal {
		@Override
		public boolean interpret() {
			return left.interpret() && right.interpret();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " & " + right.toString() + ")";
		}
	}

	public static class Not implements BooleanExpression {
		BooleanExpression negated;

		@Override
		public boolean interpret() {
			return !negated.interpret();
		}

		@Override
		public String toString() {
			return "!" + negated.toString();
		}
	}

	public static class RecursiveDescentParser {
		private Lexer lexer;
		private int symbol;
		private BooleanExpression root;

		public RecursiveDescentParser(String expression) {
			this.lexer = new Lexer(expression);
		}

		public BooleanExpression build() {
			expression();
			return root;
		}

		private void expression() {
			term();
			while (symbol == Lexer.OR) {
				Or or = new Or();
				or.setLeft(root);
				term();
				or.setRight(root);
				root = or;
			}
		}

		private void term() {
			factor();
			while (symbol == Lexer.AND) {
				And and = new And();
				and.setLeft(root);
				factor();
				and.setRight(root);
				root = and;
			}
		}

		private void factor() {
			symbol = lexer.nextSymbol();
			if (symbol == Lexer.PROPERTY) {
				root = new Terminal(lexer.lastProperty.toString());
				symbol = lexer.nextSymbol();
			} else if (symbol == Lexer.NOT) {
				Not not = new Not();
				factor();
				not.negated = root;
				root = not;
			} else if (symbol == Lexer.LEFT) {
				expression();
				symbol = lexer.nextSymbol(); // we don't care about ')'
			} else {
				throw new RuntimeException("Expression Malformed");
			}
		}
	}

	public static void main(String[] args) {
		RecursiveDescentParser parser = new RecursiveDescentParser("true & ((true | false) & !(true & false))");
		System.out.println(parser.build().toString());
		
		parser = new RecursiveDescentParser("A & ((B | C) & !(!DEF & !GHI))");
		System.out.println(parser.build().toString());
	}
}
