package com.gurella.engine.graphics.render.path;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.command.RenderCommand;

//TODO unused
public class RenderNode {
	RenderPath path;
	final ObjectMap<String, RenderPathResource> nodeResourcesByName = new ObjectMap<String, RenderPathResource>();
	final ObjectMap<String, RenderTarget> nodeTargetsByName = new ObjectMap<String, RenderTarget>();

	final Array<RenderCommand> commands = new Array<RenderCommand>();

	final Array<Connection> inputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> inputsByName = new ObjectMap<String, Connection>();
	final Array<Connection> outputsByIndex = new Array<Connection>();
	final ObjectMap<String, Connection> outputsByName = new ObjectMap<String, Connection>();
	
	void process() {
		
	}

	private static class Connection {
		String inName;
		RenderNode inNode;
		String outName;
		RenderNode outNode;
	}
}
