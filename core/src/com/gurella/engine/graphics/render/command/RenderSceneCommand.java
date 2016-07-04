package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.base.object.ObjectInstanceMask;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.tag.TagMask;

public class RenderSceneCommand implements RenderCommand {
	Scene scene;
	LayerMask layerMask;
	TagMask tagMask;
	ObjectInstanceMask<CameraComponent<?>> cameraMask;
	
	Predicate<RenderableComponent> filter;
	
	@Override
	public void render(RenderContext context) {
		RenderSystem renderSystem = scene.renderSystem;
		// TODO Auto-generated method stub
		
	}
}
