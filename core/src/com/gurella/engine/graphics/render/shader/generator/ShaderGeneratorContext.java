package com.gurella.engine.graphics.render.shader.generator;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graphics.render.material.Material;
import com.gurella.engine.graphics.render.shader.template.PieceNode;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;

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
		return builder.toString();
	}
}
