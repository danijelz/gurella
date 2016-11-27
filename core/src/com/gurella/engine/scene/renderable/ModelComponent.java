package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.gurella.engine.metatype.ModelDescriptor;

@ModelDescriptor(descriptiveName = "3D Model")
public class ModelComponent extends RenderableComponent3d {
	private Model model;
	public transient ModelInstance instance;

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		if (this.model != model) {
			this.model = model;
			if (model == null) {
				instance = null;
			} else {
				instance = new ModelInstance(model);
				if (transformComponent != null) {
					transformComponent.getWorldTransform(instance.transform);
				}
			}
			setDirty();
		}
	}

	@Override
	protected ModelInstance getModelInstance() {
		return instance;
	}

	@Override
	public void reset() {
		super.reset();
		instance = null;
		model = null;
	}
}
