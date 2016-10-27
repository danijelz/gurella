package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graphics.render.GenericBatch;

public class ScaleHandle extends ToolHandle {
	Vector3 position = new Vector3();
	Vector3 rotationEuler = new Vector3();
	Quaternion rotation = new Quaternion();
	Vector3 scale = new Vector3();

	private Model model;
	private ModelInstance modelInstance;

	public ScaleHandle(int id, Model model) {
		super(id);
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	public void changeColor(Color color) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.first().get(ColorAttribute.Diffuse);
		diffuse.color.set(color);
	}

	public void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	public void applyTransform() {
		modelInstance.transform.set(position, rotation, scale);
	}

	@Override
	public void dispose() {
		model.dispose();
	}
}
