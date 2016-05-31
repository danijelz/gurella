package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;

public class Technique {
	public final Shader shader;
	
	private Array<Pass> passes = new Array<Pass>();

	public Technique(Shader shader) {
		this.shader = shader;
	}

	public boolean isApplicable(Graphics graphics) {
		return true;
	}
}
