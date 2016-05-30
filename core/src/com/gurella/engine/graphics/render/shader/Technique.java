package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;

public class Technique {
	private Array<Pass> passes = new Array<Pass>();
	
	public boolean isApplicable(Graphics graphics) {
		return true;
	}
}
