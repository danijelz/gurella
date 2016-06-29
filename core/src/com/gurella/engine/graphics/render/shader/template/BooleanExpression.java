package com.gurella.engine.graphics.render.shader.template;

public interface BooleanExpression {
	public boolean evaluate();

	static abstract class NonTerminal implements BooleanExpression {
		protected BooleanExpression left, right;

		public void setLeft(BooleanExpression left) {
			this.left = left;
		}

		public void setRight(BooleanExpression right) {
			this.right = right;
		}
	}

	public static class Not implements BooleanExpression {
		BooleanExpression negated;

		public Not(BooleanExpression negated) {
			this.negated = negated;
		}

		@Override
		public boolean evaluate() {
			return !negated.evaluate();
		}

		@Override
		public String toString() {
			return "!" + negated.toString();
		}
	}

	public static class Or extends NonTerminal {
		@Override
		public boolean evaluate() {
			return left.evaluate() || right.evaluate();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " | " + right.toString() + ")";
		}
	}

	public static class And extends NonTerminal {
		@Override
		public boolean evaluate() {
			return left.evaluate() && right.evaluate();
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " & " + right.toString() + ")";
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
		public boolean evaluate() {
			// TODO Auto-generated method stub
			return false;
		}
	}
}