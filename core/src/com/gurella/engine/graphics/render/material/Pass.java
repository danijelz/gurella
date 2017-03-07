package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class Pass implements Disposable {
	public final Technique technique;

	transient ShaderProgram shaderProgram;

	public Pass(Technique technique) {
		this.technique = technique;
	}

	public void begin() {
		// TODO Auto-generated method stub
		shaderProgram.begin();
	}

	@Override
	public void dispose() {
		if (shaderProgram != null) {
			shaderProgram.dispose();
		}
	}
}
