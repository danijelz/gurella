package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

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
		if (model != null) {
			model.dispose();
			model = null;
		}
	}
}
