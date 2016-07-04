package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.gl.GlCompliance;

public class Technique {
	Material material;

	GlCompliance glCompliance;
	Array<String> requiredExtensions;
	Array<String> defines;

	private Array<Pass> passes = new Array<Pass>();

	public boolean isApplicable(Graphics graphics) {
		return true;
	}
}
