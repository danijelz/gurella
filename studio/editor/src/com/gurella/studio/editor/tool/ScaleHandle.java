package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gurella.engine.graphics.render.GenericBatch;

public class ScaleHandle extends ToolHandle {
	private Model model;
	private ModelInstance modelInstance;

	public ScaleHandle(int id, Model model) {
		super(id);
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	@Override
	void changeColor(Color color) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.first().get(ColorAttribute.Diffuse);
		diffuse.color.set(color);
	}

	@Override
	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	@Override
	void applyTransform() {
		modelInstance.transform.set(position, rotation, scale);
	}

	@Override
	public void dispose() {
		model.dispose();
	}
}
