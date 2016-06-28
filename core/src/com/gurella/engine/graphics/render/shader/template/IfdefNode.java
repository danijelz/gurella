package com.gurella.engine.graphics.render.shader.template;

public class IfdefNode extends ShaderTemplateNode {
	private Condition condition;
	
	public IfdefNode(String condition) {
		this.condition = new Condition(condition);
	}

	private static class Condition {
		String value;

		public Condition(String value) {
			this.value = value;
		}
	}
}
