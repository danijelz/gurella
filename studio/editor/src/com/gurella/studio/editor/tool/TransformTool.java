package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;

public abstract class TransformTool implements Disposable {
	protected static Color COLOR_X = Color.RED;
	protected static Color COLOR_Y = Color.GREEN;
	protected static Color COLOR_Z = Color.BLUE;
	protected static Color COLOR_XZ = Color.CYAN;
	protected static Color COLOR_XYZ = Color.LIGHT_GRAY;
	protected static Color COLOR_SELECTED = Color.YELLOW;

	HandleType state = HandleType.idle;

	ToolHandle[] handles;

	abstract ToolType getType();

	abstract void update(Vector3 translation, Camera camera);

	abstract void render(Vector3 translation, Camera camera, GenericBatch batch);
}
