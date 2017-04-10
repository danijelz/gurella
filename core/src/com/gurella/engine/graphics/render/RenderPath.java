package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.RenderPathIterator.RenderNodeConsumer;
import com.gurella.engine.graphics.render.command.RenderCommand;
import com.gurella.engine.graphics.render.shader.ShaderUnifrom;
import com.gurella.engine.scene.Scene;

public class RenderPath {
	private final RenderContext context = new RenderContext(this);
	private final ObjectMap<String, RenderTarget> globalTargetsByName = new ObjectMap<String, RenderTarget>();

	final Array<RenderCommand> preCommands = new Array<RenderCommand>();
	final Array<RenderCommand> postCommands = new Array<RenderCommand>();

	final Array<RenderNode> rootNodes = new Array<RenderNode>();
	final RenderPathIterator iterator = new RenderPathIterator();

	private final RenderNodeProcessor processor = new RenderNodeProcessor(context);

	// passes defined by path
	private final Array<String> pathPasses = new Array<String>();

	public void iterate(RenderNodeConsumer consumer) {
		synchronized (iterator) {
			iterator.iterate(rootNodes, consumer);
		}
	}

	void init() {
		iterate(new RenderNodeInitializer(context));
	}

	public void process(Scene scene) {
		context.scene = scene;
		iterate(processor);
		context.scene = null;
	}

	public static class RenderPathMaterialProperties {
		private Object nonGlslProperties;
		private ObjectMap<String, ShaderUnifrom> uniforms;
		private Object vetrexStruct;
		private Object fragmentStruct;
		// TODO when render path is selected all materials can provide this properties (eg.: cast shadows, receive
		// shadows...)
	}

	private static class RenderNodeInitializer implements RenderNodeConsumer {
		private final RenderContext context;

		RenderNodeInitializer(RenderContext context) {
			this.context = context;
		}

		@Override
		public void consume(RenderNode node) {
			node.init(context);
		}
	}

	private static class RenderNodeProcessor implements RenderNodeConsumer {
		private final RenderContext context;

		RenderNodeProcessor(RenderContext context) {
			this.context = context;
		}

		@Override
		public void consume(RenderNode node) {
			node.process(context);
		}
	}
}
