package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public abstract class TransformTool implements Disposable {
	protected static Color COLOR_X = Color.RED;
	protected static Color COLOR_Y = Color.GREEN;
	protected static Color COLOR_Z = Color.BLUE;
	protected static Color COLOR_XZ = Color.CYAN;
	protected static Color COLOR_XYZ = Color.LIGHT_GRAY;
	protected static Color COLOR_SELECTED = Color.YELLOW;

	final ToolManager manager;

	ToolHandle[] handles;
	ToolHandle activeHandle;
	HandleType activeHandleType = HandleType.none;

	private TransformOperation operation;

	public TransformTool(ToolManager manager) {
		this.manager = manager;
	}

	boolean isActive() {
		return activeHandleType != HandleType.none;
	}

	void activate(ToolHandle handle, TransformComponent component, Camera camera) {
		this.activeHandleType = handle.type;
		operation = createOperation(handle, component, camera);
	}

	void deactivate() {
		if (operation != null) {
			operation.rollback();
		}
		activeHandle = null;
		this.activeHandleType = HandleType.none;
	}

	void commit() {
		operation.commit();
		operation = null;
		activeHandle = null;
		this.activeHandleType = HandleType.none;
	}

	abstract ToolType getType();

	abstract void update(Vector3 nodePosition, Vector3 cameraPosition);

	abstract void render(Vector3 nodePosition, Camera camera, GenericBatch batch);

	abstract void touchDragged(TransformComponent transform, Camera camera, int screenX, int screenY);

	abstract TransformOperation createOperation(ToolHandle handle, TransformComponent component, Camera camera);
}
