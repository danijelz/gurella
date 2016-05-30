package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.renderable.Renderable;
import com.gurella.engine.graphics.render.shader.Pass;
import com.gurella.engine.scene.Scene;

public class RenderContext {
	private Scene scene;
	private Camera camera;
	private Viewport viewport;
	private Pass pass;
	
	private RenderTarget renderTarget;
	private RenderState renderState;
	private TextureBinder textureBinder;
	
	private Renderable currentRenderable;
	private MaterialDescriptor currentMaterial;
}
