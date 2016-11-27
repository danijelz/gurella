package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.path.RenderPath.RenderPathMaterialProperties;
import com.gurella.engine.managedobject.ManagedObject;

public class Material extends ManagedObject implements Disposable {
	Array<Technique> techiques = new Array<Technique>();
	RenderPathMaterialProperties renderPathOverrides;

	transient ShaderProgram shaderProgram;

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
