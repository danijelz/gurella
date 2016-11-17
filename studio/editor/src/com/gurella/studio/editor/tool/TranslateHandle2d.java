package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;

public class TranslateHandle2d extends ToolHandle {
	public TranslateHandle2d(HandleType type, Color color, Model model) {
		super(type, color, model);
	}

	@Override
	void applyTransform() {
		rotation.setEulerAngles(0, 0, rotationEuler.z);
		modelInstance.transform.set(position, rotation, scale);
	}
}
