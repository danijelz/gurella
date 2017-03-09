package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.RenderContext;

public class ShaderUnifrom {
	private String name;
	
	private ShaderVariableType type;
	private int cardinality = 1;
	
	private UniformScope scope;
	private ObjectMap<String, String> defines;

	private boolean editable;
	private boolean required;

	public void setValue(Object value) {

	}

	public Object getValue() {
		return null;
	}

	public static class AutoUniform extends ShaderUnifrom {
		public void updateValue(RenderContext context) {

		}
	}

	public enum UniformScope {
		global, material;
	}

	public enum ShaderVariableType {
		booleanType, floatType, integerType, vectorType, vector2Type, vector3Type;
	}
}
