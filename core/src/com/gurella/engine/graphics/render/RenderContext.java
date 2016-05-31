package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gurella.engine.graphics.render.shader.Pass;
import com.gurella.engine.graphics.render.shader.Technique;
import com.gurella.engine.scene.Scene;

public class RenderContext {
	private Scene scene;

	private Camera camera;
	private Viewport viewport;

	private Technique technique;
	private Pass pass;

	private RenderState currentRenderState;
	private Array<RenderState> renderStateStack = new Array<RenderState>();
}
