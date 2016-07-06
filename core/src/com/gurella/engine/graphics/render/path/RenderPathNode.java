package com.gurella.engine.graphics.render.path;

import com.badlogic.gdx.utils.Array;

//TODO unused
public class RenderPathNode {
	RenderPath path;

	Array<Connection> inputs;
	Array<Connection> outputs;

	private static class Connection {
		String inName;
		String outName;
		
		RenderPathNode other;
	}
}
