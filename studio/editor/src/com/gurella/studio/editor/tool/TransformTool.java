package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;

public abstract class TransformTool implements Disposable {
	protected static final int X_HANDLE_ID = 0;
	protected static final int Y_HANDLE_ID = 1;
	protected static final int Z_HANDLE_ID = 2;
	protected static final int XZ_HANDLE_ID = 3;
	protected static final int XYZ_HANDLE_ID = 4;

	protected static Color COLOR_X = Color.RED;
	protected static Color COLOR_Y = Color.GREEN;
	protected static Color COLOR_Z = Color.BLUE;
	protected static Color COLOR_XZ = Color.CYAN;
	protected static Color COLOR_XYZ = Color.LIGHT_GRAY;
	protected static Color COLOR_SELECTED = Color.YELLOW;

	abstract void update(Vector3 translation, Camera camera);

	abstract void render(Vector3 translation, Camera camera, GenericBatch batch);

	abstract ToolHandle getIntersection(Vector3 cameraPosition, Ray ray, Vector3 intersection);

	protected enum TransformState {
		TRANSFORM_X, TRANSFORM_Y, TRANSFORM_Z, TRANSFORM_XZ, TRANSFORM_XYZ, IDLE
	}
}
