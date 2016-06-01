package com.gurella.engine.graphics.render.path;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.command.CompositeRenderCommand;
import com.gurella.engine.graphics.render.command.RenderComandBuffer;
import com.gurella.engine.graphics.render.command.RenderCommand;
import com.gurella.engine.scene.Scene;

public class RenderPath {
	private final RenderComandBuffer rootBuffer = new RenderComandBuffer();
	private final RenderComandBuffer comandBuffer = new RenderComandBuffer();
	private final RenderComandBuffer effectsBuffer = new RenderComandBuffer();
	private final Array<Effect> effects = new Array<Effect>();

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
		effects.add(effect);
	}

	public void removeEffect(Effect effect) {
		effects.removeValue(effect, true);
	}
	
	private static class RenderPathMaterialProperties {
		//TODO when render path is selected all materials can override this properties (eg.: cast shadows, receive shadows...)
	}
}
