package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;

public class RotateHandle extends ToolHandle {
	public RotateHandle(HandleType type, Color color, Model model) {
		super(type, color, model);

		switch (type) {
		case x:
			this.rotationEuler.y = 90;
			this.scale.x = 0.9f;
			this.scale.y = 0.9f;
			this.scale.z = 0.9f;
			break;
		case y:
			this.rotationEuler.x = 90;
			break;
		case z:
			this.rotationEuler.z = 90;
			this.scale.x = 1.1f;
			this.scale.y = 1.1f;
			this.scale.z = 1.1f;
			break;
		}

		modelInstance.calculateTransforms();
	}

	@Override
	void applyTransform() {
		rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
		modelInstance.transform.set(position, rotation, scale);
	}
}
