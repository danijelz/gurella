package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.RenderPath.RenderPathMaterialProperties;
import com.gurella.engine.managedobject.ManagedObject;

public class Material extends ManagedObject implements Disposable {
	Array<Technique> techiques = new Array<Technique>();
	RenderPathMaterialProperties renderPathOverrides;

	private ObjectMap<String, String> defines;

	public MaterialInstance createInstance() {
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
