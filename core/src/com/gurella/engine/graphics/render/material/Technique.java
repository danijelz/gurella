package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.gl.GlCompliance;

public class Technique {
	Material material;
	
	String name;

	GlCompliance glCompliance;
	Array<String> requiredExtensions;
	
	ObjectMap<String, String> defines;

	Array<Pass> passes = new Array<Pass>();

	public boolean isApplicable(Graphics graphics) {
		return true;
	}
}
