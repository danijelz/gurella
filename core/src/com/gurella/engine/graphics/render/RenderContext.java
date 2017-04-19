package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.gl.GlContext;
import com.gurella.engine.graphics.render.material.Pass;
import com.gurella.engine.graphics.render.material.Technique;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.camera.CameraViewport;

public class RenderContext {
	final RenderPath path;
	
	GlContext glContext;
	
	RenderNode node;

	Scene scene;
	Camera camera;
	CameraViewport viewport;

	String passName;
	Technique technique;
	Pass pass;

	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();

	public final RenderComandBuffer comandBuffer = new RenderComandBuffer();
	public final ObjectMap<Object, Object> userData = new ObjectMap<Object, Object>();

	RenderContext(RenderPath path) {
		this.path = path;
	}
}
