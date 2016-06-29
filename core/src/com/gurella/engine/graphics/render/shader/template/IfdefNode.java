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
