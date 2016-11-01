package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gurella.engine.graphics.render.GenericBatch;

public class TranslateHandle extends ToolHandle {
	Model model;
	ModelInstance modelInstance;

	public TranslateHandle(int id, Model model) {
		super(id);
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	@Override
	void changeColor(Color color) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.get(0).get(ColorAttribute.Diffuse);
		diffuse.color.set(color);
	}

	@Override
	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	@Override
	void applyTransform() {
		rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
		modelInstance.transform.set(position, rotation, scale);
	}

	@Override
	public void dispose() {
		this.model.dispose();
	}
}
