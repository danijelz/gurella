package com.gurella.engine.graphics.render.path;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.RenderQueue;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.command.CompositeRenderCommand;
import com.gurella.engine.graphics.render.command.RenderComandBuffer;
import com.gurella.engine.graphics.render.command.RenderCommand;

public class RenderPath {
	private final RenderComandBuffer rootBuffer = new RenderComandBuffer();
	private final RenderComandBuffer comandBuffer = new RenderComandBuffer();
	private final RenderComandBuffer effectsBuffer = new RenderComandBuffer();

	private final IntMap<RenderQueue> queues = new IntMap<RenderQueue>();
	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();

	public RenderPath() {
		rootBuffer.add(new CompositeRenderCommand(comandBuffer));
		rootBuffer.add(new CompositeRenderCommand(effectsBuffer));
	}

	public void render(RenderContext context) {

	}

	public void addRenderCommand(RenderCommand command) {
		comandBuffer.add(command);
	}

	public void removeRenderCommand(RenderCommand command) {
		comandBuffer.remove(command);
	}
}
