package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class RenderQueue {
	private RenderableComparator comparator;
	private final Array<RenderableComponent> renderables = new Array<RenderableComponent>();

	public interface RenderableComparator {
		int compare(RenderContext context, RenderableComponent component1, RenderableComponent component2);
	}
}
