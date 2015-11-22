package com.gurella.engine.graph.renderable;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class ModelComponent extends RenderableComponent3d {
	private Model model;

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		if (this.model != model) {
			this.model = model;
			if(model == null) {
				instance = null;
			} else {
				instance = new ModelInstance(model);
				if(transformComponent != null) {
					transformComponent.getWorldTransform(instance.transform);
				}
			}
		}
	}

	@Override
	protected void updateDefaultTransform() {
		if(instance != null) {
			instance.transform.idt();
		}
	}

	@Override
	protected void updateTransform() {
		if(instance != null) {
			transformComponent.getWorldTransform(instance.transform);
		}
	}
}
