package com.gurella.engine.graphics.render.shader;

public class Shader {
	private ShaderType type;
	private String source;
	
	public enum ShaderType {
		vertex, fragment;
	}
}
