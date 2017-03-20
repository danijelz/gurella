package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.gl.GlContext;
import com.gurella.engine.managedobject.ManagedObjectMask;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.tag.TagMask;

public class RenderSceneCommand implements RenderCommand {
	Scene scene;
	LayerMask layerMask;
	TagMask tagMask;
	ManagedObjectMask<CameraComponent<?>> cameraMask;
	Array<String> passes;
	boolean renderGui;
	
	Predicate<RenderableComponent> filter;
	
	@Override
	public void process(RenderContext renderContext, GlContext glContext) {
		RenderSystem renderSystem = scene.renderSystem;
		// TODO Auto-generated method stub
		
	}
}
