package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;

public abstract class ToolHandle implements Disposable {
	int id;
	Color color;
	
	Vector3 position = new Vector3();
	Vector3 rotationEuler = new Vector3();
	Quaternion rotation = new Quaternion();
	Vector3 scale = new Vector3();

	public ToolHandle(int id, Color color) {
		this.id = id;
		this.color = color;
	}
	
	void restoreColor() {
		changeColor(color);
	}

	abstract void render(GenericBatch batch);

	abstract void applyTransform();

	abstract void changeColor(Color color);
}
