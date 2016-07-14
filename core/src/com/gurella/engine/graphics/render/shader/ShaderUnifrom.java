package com.gurella.engine.graphics.render.shader;

import com.gurella.engine.graphics.render.RenderContext;

public class ShaderUnifrom {
	private String name;
	private ShaderVariableType type;
	private UniformScope scope;
	private String defineProperty;
	
	private boolean editable;
	private boolean required;
	private int cardinality = 1;

	public void setValue(Object value) {

	}

	public Object getValue() {
		return null;
	}

	public static class AutomaticUniform extends ShaderUnifrom {
		public void updateValue(RenderContext context) {

		}
	}

	public enum UniformScope {
		global, material;
	}

	public enum ShaderVariableType {
		booleanType, floatType, integerType, vectorType, vector3Type, vector2Type;
	}
}
