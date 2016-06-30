package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public abstract class ShaderTemplateNode {
	Array<ShaderTemplateNode> children = new Array<ShaderTemplateNode>();

	public void addChild(ShaderTemplateNode child) {
		children.add(child);
	}

	protected abstract void generate(ShaderGeneratorContext context);

	protected void generateChildren(ShaderGeneratorContext context) {
		for (int i = 0, n = children.size; i < n; i++) {
			children.get(i).generate(context);
		}
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int indent) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			builder.append('\t');
		}

		builder.append(getClass().getSimpleName());
		builder.append(": {");
		builder.append(toStringValue());
		builder.append(toStringChildren(indent + 1));

		if (children.size > 0) {
			builder.append("\n");
			for (int i = 0; i < indent; i++) {
				builder.append('\t');
			}
		}

		builder.append("}");
		return builder.toString();
	}

	protected String toStringValue() {
		return "";
	}

	private String toStringChildren(int indent) {
		if (children.size == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (ShaderTemplateNode child : children) {
			builder.append("\n");
			builder.append(child.toString(indent));
		}
		return builder.toString();
	}
}
