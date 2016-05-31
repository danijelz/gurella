package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Array;

public class RenderComandBuffer {
	private final Array<RenderCommand> commands = new Array<RenderCommand>();

	public void add(RenderCommand command) {
		commands.add(command);
	}

	public void remove(RenderCommand command) {
		commands.removeValue(command, true);
	}
}
