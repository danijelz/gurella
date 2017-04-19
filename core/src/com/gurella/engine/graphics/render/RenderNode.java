package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.command.RenderCommand;

public class RenderNode {
	RenderPath path;
	
	String name;
	final Array<RenderCommand> commands = new Array<RenderCommand>();

	final ObjectMap<String, RenderTarget> nodeTargetsByName = new ObjectMap<String, RenderTarget>();

	final Array<Connection> inputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> inputsByName = new ObjectMap<String, Connection>();
	final Array<Connection> outputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> outputsByName = new ObjectMap<String, Connection>();

	void init(RenderContext renderContext) {
		for (int i = 0, n = commands.size; i < n; i++) {
			commands.get(i).init(renderContext);
		}
	}

	void process(RenderContext context) {
		context.node = this;
		for (int i = 0, n = commands.size; i < n; i++) {
			commands.get(i).process(/*TODO*/ null);
		}
		context.node = null;
	}

	static class Connection {
		String inName;
		RenderNode inNode;
		RenderTarget renderTarget;
		String outName;
		RenderNode outNode;
	}
}
