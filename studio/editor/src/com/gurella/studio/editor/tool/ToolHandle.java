package com.gurella.studio.editor.tool;

import static com.gurella.studio.editor.tool.TransformTool.COLOR_SELECTED;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;

public abstract class ToolHandle implements Disposable {
	final HandleType type;

	final Model model;
	final ModelInstance modelInstance;

	final Vector3 position = new Vector3();
	final Vector3 rotationEuler = new Vector3();
	final Quaternion rotation = new Quaternion();
	final Vector3 scale = new Vector3();

	Color color;

	public ToolHandle(HandleType type, Color color, Model model) {
		this.type = type;
		this.color = color;
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	abstract void applyTransform();
	
	void focusGained() {
		changeColor(COLOR_SELECTED);
	}
	
	void focusLost() {
		changeColor(color);
	}

	private void changeColor(Color newColor) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.first().get(ColorAttribute.Diffuse);
		diffuse.color.set(newColor);
	}

	@Override
	public void dispose() {
		model.dispose();
	}
}
