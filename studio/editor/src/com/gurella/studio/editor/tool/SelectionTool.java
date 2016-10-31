package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;

public class SelectionTool extends EditorTool {
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

//	private SceneNode2 focusNode;
//
//	public void render(TransformComponent transform, Camera camera, GenericBatch batch) {
//		if (focusNode == null) {
//			return;
//		}
//
//		batch.begin(camera);
//
//		//		for (GameObject go : getProjectManager().current().currScene.currentSelection) {
//		//			// model component
//		//			ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
//		//			if (mc != null) {
//		//				getBatch().render(mc.getModelInstance(), getShader());
//		//			}
//		//		}
//
//		batch.end();
//	}

	protected enum TransformState {
		TRANSFORM_X, TRANSFORM_Y, TRANSFORM_Z, TRANSFORM_XZ, TRANSFORM_XYZ, IDLE
	}
}
