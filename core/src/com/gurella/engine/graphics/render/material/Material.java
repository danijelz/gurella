package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.path.RenderPath.RenderPathMaterialProperties;
import com.gurella.engine.managedobject.ManagedObject;

public class Material extends ManagedObject implements Disposable {
	Array<Technique> techiques = new Array<Technique>();
	RenderPathMaterialProperties renderPathOverrides;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void begin() {
		// TODO Auto-generated method stub

	}

	public void begin(String passName) {
		// TODO Auto-generated method stub

	}
}
