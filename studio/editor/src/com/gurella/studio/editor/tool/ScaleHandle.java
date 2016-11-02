package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;

public class ScaleHandle extends ToolHandle {
	public ScaleHandle(int id, Color color, Model model) {
		super(id, color, model);
	}

	@Override
	void applyTransform() {
		modelInstance.transform.set(position, rotation, scale);
	}
}
