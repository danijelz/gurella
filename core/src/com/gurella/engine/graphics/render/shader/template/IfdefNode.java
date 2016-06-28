package com.gurella.engine.graphics.render.shader.template;

public class IfdefNode extends ShaderTemplateNode {
	private Condition condition;

	public IfdefNode(String condition) {
		this.condition = new Condition(condition);
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		if (condition.apply()) {
			generateChildren(template, builder);
		}
	}

	@Override
	protected String toStringValue() {
		return "Condition '" + condition.toString() + "'";
	}

	private static class Condition {
		String value;

		public Condition(String value) {
			this.value = value;
		}

		boolean apply() {
			return value != null;// TODO
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
