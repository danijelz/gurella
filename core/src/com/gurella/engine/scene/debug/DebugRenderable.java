package com.gurella.engine.scene.debug;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;

public interface DebugRenderable {
	void debugRender(RenderContext context);
	
	public static class RenderContext {
		public GenericBatch batch;
		public Camera camera;
		public SceneNode2 focusedNode;
		public SceneNodeComponent2 focusedComponent;
	}
}
