package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class RenderSceneCommand {
	Scene scene;
	Predicate<RenderableComponent> filter;
}
