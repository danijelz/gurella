package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.command.RenderCommand;
import com.gurella.engine.graphics.render.shader.ShaderUnifrom;
import com.gurella.engine.scene.Scene;

public class RenderPath {
	private final RenderContext context = new RenderContext(this);
	private final ObjectMap<String, RenderTarget> globalTargetsByName = new ObjectMap<String, RenderTarget>();
	
	final Array<RenderCommand> preCommands = new Array<RenderCommand>();
	final Array<RenderCommand> postCommands = new Array<RenderCommand>();

	private final Array<RenderNode> rootNodes = new Array<RenderNode>();

	// passes defined by path
	private final Array<String> pathPasses = new Array<String>();

	public void init() {
		for (int i = 0, n = rootNodes.size; i < n; i++) {
			rootNodes.get(i).init(context);
		}
	}

	public void process(Scene scene) {
		context.scene = scene;
		for (int i = 0, n = rootNodes.size; i < n; i++) {
			rootNodes.get(i).process(context);
		}
		context.scene = null;
	}

	public static class RenderPathMaterialProperties {
		private Object nonGlslProperties;
		private ObjectMap<String, ShaderUnifrom> uniforms;
		private Object vetrexStruct;
		private Object fragmentStruct;
		// TODO when render path is selected all materials can provide this properties (eg.: cast shadows, receive shadows...)
	}
}
