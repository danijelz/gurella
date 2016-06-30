package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public interface BooleanExpression {
	public boolean evaluate(ShaderGeneratorContext context);

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
		public boolean evaluate(ShaderGeneratorContext context) {
			return !negated.evaluate(context);
		}

		@Override
		public String toString() {
			return "!" + negated.toString();
		}
	}

	public static class Or extends NonTerminal {
		@Override
		public boolean evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) || right.evaluate(context);
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " | " + right.toString() + ")";
		}
	}

	public static class And extends NonTerminal {
		@Override
		public boolean evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) && right.evaluate(context);
		}

		@Override
		public String toString() {
			return "(" + left.toString() + " & " + right.toString() + ")";
		}
	}
	
	public static class Property implements BooleanExpression {
		protected String propertyName;

		public Property(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public String toString() {
			return propertyName;
		}

		@Override
		public boolean evaluate(ShaderGeneratorContext context) {
			return context.isDefined(propertyName);
		}
	}
}