package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class RenderSceneCommand implements RenderCommand {
	Scene scene;
	Predicate<RenderableComponent> filter;
	
	@Override
	public void render(RenderContext context) {
		RenderSystem renderSystem = scene.renderSystem;
		// TODO Auto-generated method stub
		
	}
}
