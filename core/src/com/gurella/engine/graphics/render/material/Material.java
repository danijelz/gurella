package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.path.RenderPath.RenderPathMaterialProperties;

public class Material {
	Array<Technique> techiques = new Array<Technique>();
	RenderPathMaterialProperties renderPathOverrides;

	public void bind() {
		// TODO Auto-generated method stub

	}
}
