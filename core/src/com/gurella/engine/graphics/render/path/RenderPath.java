package com.gurella.engine.graphics.render.path;

import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.command.CompositeRenderCommand;
import com.gurella.engine.graphics.render.command.RenderComandBuffer;
import com.gurella.engine.graphics.render.command.RenderCommand;
import com.gurella.engine.scene.Scene;

public class RenderPath {
	private final RenderComandBuffer rootBuffer = new RenderComandBuffer();
	private final RenderComandBuffer comandBuffer = new RenderComandBuffer();
	private final RenderComandBuffer effectsBuffer = new RenderComandBuffer();

	private final RenderContext context = new RenderContext();

	public RenderPath() {
		rootBuffer.add(new CompositeRenderCommand(comandBuffer));
		rootBuffer.add(new CompositeRenderCommand(effectsBuffer));
	}

	public void render(Scene scene) {

	}

	public void addRenderCommand(RenderCommand command) {
		comandBuffer.add(command);
	}

	public void removeRenderCommand(RenderCommand command) {
		comandBuffer.remove(command);
	}

	public void addEffect(Effect effect) {
		// comandBuffer.add(command);
	}

	public void removeEffect(Effect effect) {
		// comandBuffer.remove(command);
	}
}
