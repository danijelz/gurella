package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Array;

public class Technique {
	Material material;

	private Array<Pass> passes = new Array<Pass>();

	public boolean isApplicable(Graphics graphics) {
		return true;
	}
}
