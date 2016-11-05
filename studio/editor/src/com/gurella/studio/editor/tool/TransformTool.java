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

	final int editorId;

	private final Vector3 nodePosition = new Vector3();

	TransformComponent transform;
	Camera camera;

	ToolHandle activeHandle;
	HandleType activeHandleType = HandleType.none;

	private TransformOperation operation;

	public TransformTool(int editorId) {
		this.editorId = editorId;
	}

	boolean isActive() {
		return activeHandleType != HandleType.none;
	}

	Vector3 getPosition() {
		return transform.getWorldTranslation(nodePosition);
	}

	void activate(ToolHandle handle) {
		activeHandle = handle;
		this.activeHandleType = handle.type;
		operation = createOperation(handle);
	}

	void deactivate() {
		if (operation != null) {
			operation.rollback();
			operation = null;
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

	abstract ToolHandle[] getHandles();

	abstract void update();

	abstract void render(GenericBatch batch);

	abstract void dragged(int screenX, int screenY);

	abstract TransformOperation createOperation(ToolHandle handle);
}
