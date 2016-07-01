package com.gurella.engine.graphics.render.shader.generator;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graphics.render.material.Material;
import com.gurella.engine.graphics.render.shader.template.PieceNode;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.utils.Values;

public class ShaderGeneratorContext {
	private StringBuilder builder = new StringBuilder();

	private final ObjectSet<String> defines = new ObjectSet<String>();
	private final ObjectIntMap<String> values = new ObjectIntMap<String>();

	private ShaderTemplate template;
	private Material material;

	public void init(ShaderTemplate template) {
		this.template = template;
		builder.setLength(0);
	}

	public void define(String propertyName) {
		defines.add(propertyName);
	}

	public void undefine(String propertyName) {
		defines.remove(propertyName);
	}

	public boolean isDefined(String propertyName) {
		return defines.contains(propertyName);
	}

	public PieceNode getPiece(String pieceName) {
		return template.getPiece(pieceName);
	}

	public void append(CharSequence sequence) {
		builder.append(sequence);
	}

	public boolean isValueSet(String valueName) {
		return values.containsKey(valueName);
	}

	public int getValue(String valueName) {
		if (!values.containsKey(valueName)) {
			values.put(valueName, 0);
		}
		return values.get(valueName, 0);
	}

	public void setValue(String valueName, int value) {
		values.put(valueName, value);
	}

	public void unsetValue(String valueName) {
		values.remove(valueName, 0);
	}

	public String getShaderSource() {
		format();
		return builder.toString();
	}

	private void format() {
		int index = 0;
		int length = builder.length();
		while(index < length && Values.isWhitespace(builder.charAt(index))) {
			index++;
		}
		builder.delete(0, index);
		
		while ((index = builder.indexOf("\r\n\r\n\r\n", index)) > -1) {
			builder.delete(index, index + 2);
		}

		index = 0;
		while ((index = builder.indexOf("\n\n\n", index)) > -1) {
			builder.delete(index, index + 1);
		}

		index = 0;
		while ((index = builder.indexOf("\r\r\n", index)) > -1) {
			builder.delete(index, index + 1);
		}
	}
}
