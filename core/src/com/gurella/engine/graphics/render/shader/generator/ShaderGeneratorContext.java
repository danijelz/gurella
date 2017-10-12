package com.gurella.engine.graphics.render.shader.generator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.GraphicsService;
import com.gurella.engine.graphics.render.shader.template.PieceNode;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.utils.Values;

public class ShaderGeneratorContext implements Poolable {
	private StringBuilder builder = new StringBuilder();

	private final ObjectFloatMap<String> values = new ObjectFloatMap<String>();

	private ShaderTemplate root;

	public void init(ShaderTemplate template) {
		this.root = template;
		builder.setLength(0);

		values.put("pi", MathUtils.PI);
		values.put("pi2", MathUtils.PI2);
		values.put("e", MathUtils.E);

		define("GL20");
		if (GraphicsService.isGl30Available()) {
			define("GL30");
		}
		for (String extension : GraphicsService.getGlExtensions()) {
			define(extension);
		}
	}

	public void define(String propertyName) {
		values.put(propertyName, 0);
	}

	public void undefine(String propertyName) {
		values.remove(propertyName, 0);
	}

	public boolean isDefined(String propertyName) {
		return values.containsKey(propertyName);
	}

	public PieceNode getPiece(String pieceName) {
		return root.getPiece(pieceName);
	}

	public void append(CharSequence sequence) {
		builder.append(sequence);
	}

	public boolean isValueSet(String valueName) {
		return values.containsKey(valueName);
	}

	public float getValue(String valueName) {
		return values.get(valueName, 0.0f);
	}

	public int getIntValue(String valueName) {
		float value = values.get(valueName, 0.0f);
		if (value != (int) value) {
			throw new RuntimeException("value must be integer");
		}
		return (int) value;
	}

	public void setValue(String valueName, float value) {
		values.put(valueName, value);
	}

	public void unsetValue(String valueName) {
		values.remove(valueName, 0.0f);
	}

	public String getShaderSource(boolean format) {
		if (format) {
			format();
		}
		return builder.toString();
	}

	private void format() {
		int index = 0;
		int length = builder.length();
		while (index < length && Values.isWhitespace(builder.charAt(index))) {
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

	@Override
	public void reset() {
		builder.setLength(0);
		values.clear();
		root = null;
	}
}
