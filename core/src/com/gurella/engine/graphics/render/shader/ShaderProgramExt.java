package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderProgramExt extends ShaderProgram {
	public ShaderProgramExt(FileHandle vertexShader, FileHandle fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public ShaderProgramExt(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public static class ProgramUniform {
		String name;
		int location;
		int type;
		int size;

		// TODO value

		boolean dirty;
	}

	public static class ProgramAttribute {
		String name;
		int location;
		int type;
		int size;

		boolean enabled;
		boolean normalize;
		int stride;
		int offset;

		boolean dirty;
	}
}
