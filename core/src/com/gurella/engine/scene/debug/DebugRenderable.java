package com.gurella.engine.scene.debug;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;

public interface DebugRenderable {
	void debugRender(DebugRenderContext context);
	
	public static class DebugRenderContext {
		public GenericBatch batch;
		public Camera camera;
	}
}
