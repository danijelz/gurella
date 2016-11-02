package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;

public abstract class ToolHandle implements Disposable {
	int id;
	Color color;
	Model model;
	ModelInstance modelInstance;

	Vector3 position = new Vector3();
	Vector3 rotationEuler = new Vector3();
	Quaternion rotation = new Quaternion();
	Vector3 scale = new Vector3();

	public ToolHandle(int id, Color color, Model model) {
		this.id = id;
		this.color = color;
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	abstract void applyTransform();

	void changeColor(Color color) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.first().get(ColorAttribute.Diffuse);
		diffuse.color.set(color);
	}

	void restoreColor() {
		changeColor(color);
	}

	@Override
	public void dispose() {
		model.dispose();
	}
}
