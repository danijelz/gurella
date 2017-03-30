package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.command.RenderCommand;

public class RenderNode {
	RenderPath path;
	final ObjectMap<String, RenderTarget> nodeTargetsByName = new ObjectMap<String, RenderTarget>();

	final Array<RenderCommand> preCommands = new Array<RenderCommand>();
	final Array<RenderCommand> postCommands = new Array<RenderCommand>();

	final Array<Connection> inputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> inputsByName = new ObjectMap<String, Connection>();
	final Array<Connection> outputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> outputsByName = new ObjectMap<String, Connection>();

	void init(RenderContext renderContext) {
		for (int i = 0, n = inputsByIndex.size; i < n; i++) {
			inputsByIndex.get(i).outNode.init(renderContext);
		}
	}

	void process(RenderContext context) {
		context.node = this;
		for (int i = 0, n = inputsByIndex.size; i < n; i++) {
			inputsByIndex.get(i).outNode.process(context);
		}
		context.node = null;
	}

	private static class Connection {
		String inName;
		RenderNode inNode;
		String outName;
		RenderNode outNode;
	}
}
